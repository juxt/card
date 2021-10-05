(ns juxt.home.card.query-hooks
  "Treated as a sort of library of domain agnostic hooks and react-query wrapper
  functions. No card specific logic pls (but site specific is ok)"
  (:require
   [cljs-bean.core :refer [->clj ->js]]
   [potpuri.core :refer [deep-merge find-index]]
   [kitchen-async.promise :as p]
   [react-query :refer [useQuery useMutation useQueryClient]]
   [juxt.home.card.config :as config]
   [juxt.home.card.common :as common]))

(defn fetch
  ([url] (fetch url {}))
  ([url opts]
   (let [promise (.fetch
                  js/window url
                  (clj->js
                   (deep-merge
                    {:method "GET"
                     :headers {"Content-Type"
                               "application/json"}
                     :timeout 5000
                     :credentials "include"}
                    opts)))]
     (let [{:keys [pending success error] :as toast}
           (:toast opts)]
       (when toast
         (common/toast! promise
                        (or pending
                            "Waiting for server...")
                        (or success
                            "Success!")
                        (or error
                            "Error... Try again later"))))
     (.then promise #(if (or (= 204 (.-status %))
                             (= 201 (.-status %)))
                       ;; don't try and .json nil bodies, is there a better way
                       ;; to do this?
                       ""
                       (.json %))))))

(defn prepare-body
  "Takes a clj map and converts it to be used as a fetch body"
  [m]
  (js/JSON.stringify (clj->js m :keyword-fn #(subs (str %) 1))))

(defn graphql-q
  [query-string variables]
  (-> (fetch "https://www.learnwithjason.dev/graphql"
             {:method "POST"
              :body (js/JSON.stringify (->js {:query query-string
                                              :variables variables}))})
      (.then (fn [x] (.-data x)))))

(defn use-query
  "Defaults to wrapping useQuery hook in ->clj so the result can be destructured.
  Pass {clj: false} in the options if using this from JS land"
  ([id fetcher] (use-query id fetcher nil))
  ([id fetcher {:keys [query-opts clj] :or {clj true}}]
   (let [hook (useQuery (->js id) fetcher (if clj (->js query-opts) query-opts))]
     (if clj (->clj hook) (->js hook)))))

(defn delete-req!
  ([id] (delete-req! id {}))
  ([id opts]
   (fetch id (merge opts {:method "DELETE"}))))

(defn update-req!
  [id opts]
  (fetch id (merge opts {:method "PATCH"})))

(defn create-req!
  [id opts]
  (fetch id (merge opts {:method "PUT"})))

(defn handle-error
  [err _new-item context]
  (p/let [previous (:previous context)
          client (:client context)]
    (prn "Error, rolling back..." err)
    (.setQueryData client (:key context) previous)))

(defn use-mutation
  [mutation-fn {:keys [key new-data-fn on-mutate-fn mutation-fn-props]
                :as opts}]
  (let [client (useQueryClient)]
    (useMutation
     #(mutation-fn % (merge {:toast true} mutation-fn-props))
     (clj->js
      (merge
       {:onMutate
        #(p/let [_ (.cancelQueries client key)
                 previous-vals (.getQueryData client key)
                 new-vals (new-data-fn % previous-vals)]
           (when (and previous-vals new-vals)
             (.setQueryData client key new-vals))
           (when on-mutate-fn
             (on-mutate-fn client key %))
           {:previous previous-vals
            :key key
            :client client
            :new new-vals})
        :onError handle-error
        :onSettled (fn [_ err] (when-not err (.invalidateQueries client key)))}
       opts)))))

(defn use-delete-mutation
  "Returns a mutation object. When you call 'mutate' on that mutation object with
  an id as its only argument, it will send a DELETE request for the given id"
  [opts]
  (use-mutation
   delete-req!
   (merge
    {:new-data-fn (fn [id previous-vals]
                    (remove #(= id (:crux.db/id %))
                            previous-vals))}
    opts)))

(defn use-create-mutation
  [{:keys [fetch-fn process-item-fn] :as opts}]
  (use-mutation
   (or
    fetch-fn
    (fn [{:keys [crux.db/id] :as body}]
      (create-req! id {:body (prepare-body body)})))
   (merge
    {:new-data-fn (fn [new-item previous-vals]
                    (let [{:keys [crux.db/id]
                           :as new-item} (process-item-fn (->clj new-item))]
                      (if-let [idx (and id
                                        (find-index previous-vals
                                                    {:crux.db/id id}))]
                        (assoc previous-vals idx new-item)
                        (conj previous-vals new-item))))}
    opts)))

(def initialUser {:id "loading"
                  :email "Loading..."
                  :fields {}
                  :name "Loading..."
                  :imageUrl "https://www.gravatar.com/avatar?d=mp"})

(defn use-self
  ([] (use-self {}))
  ([opts]
   (let [assoc-image
         (fn assoc-image
           [user]
           (when-let [user (->clj user)]
             (assoc user
                    :imageUrl (str (:id user)
                                   "/slack/"
                                   (:username user)
                                   ".jpg"))))]
     (use-query
      "self"
      #(-> (fetch (str config/site-api-origin "/_site/user"))
           (.then assoc-image))
      (deep-merge
       {:query-opts {:placeholderData initialUser
                     :retry false
                     ;; user data is cached until page is refreshed
                     :staleTime js/Infinity}}
       opts)))))
