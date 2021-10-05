(ns juxt.home.card.query-hooks
  "Treated as a sort of library of domain agnostic hooks and react-query wrapper
  functions. No card specific logic pls (but site specific is ok)"
  (:require
   [cljs-bean.core :refer [->clj ->js]]
   [potpuri.core :refer [deep-merge]]
   [kitchen-async.promise :as p]
   [react-query :refer [useQuery useMutation useQueryClient]]
   [juxt.home.card.config :as config]
   [juxt.home.card.common :as common]))

(defn fetch
  ([url] (fetch url {}))
  ([url opts]
   (let [controller ^js (new js/window.AbortController)
         signal (.-signal controller)
         promise (.fetch
                  js/window url
                  (clj->js (deep-merge {:method "GET"
                                        :signal signal
                                        :headers {"Content-Type" "application/json"}
                                        :timeout 5000
                                        :credentials "include"}
                                       opts)))]
     (when-let [{:keys [pending success error]}
                (:toast opts)]
       (common/toast! promise
                      (or pending
                          "Waiting for server...")
                      (or success
                          "Success!")
                      (or error
                          "Error... Try again later")))
     (set! (.-cancel promise) (fn cancelFetch [] (.abort controller)))
     (.then promise #(if (= 204 (.-status %))
                       ""
                       (.json %))))))

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

(defn handle-delete
  [client key id]
  (p/try
    (.cancelQueries client key)
    (let [previous-vals (.getQueryData client key)
          new-vals (remove #(= id (:crux.db/id %))
                           previous-vals)]
      (js/console.log "del" key id previous-vals new-vals)
      (.setQueryData client key new-vals)
      {:previous previous-vals
       :key key
       :client client
       :new new-vals})
    (p/catch :default e
      (js/console.error
       "error deleting item" {:key key
                              :e e
                              :id id}))))

(defn handle-error
  [err new-item context]
  (p/let [previous (:previous context)
          client (:client context)]
    (.setQueryData client (:key context) (:previous context))))

(defn use-mutation
  [mutation-fn opts]
  (let [client (useQueryClient)
        key (:key opts)]
    (useMutation
     #(mutation-fn % (merge {:toast true} (:mutation-fn-props opts)))
     (clj->js
      (merge
       {:onMutate #(handle-delete client key %)
        :onError handle-error
        :onSettled (fn [_ err] (when-not err (.invalidateQueries client key)))}
       opts)))))

(defn use-delete-mutation
  "Returns a mutation object. When you call 'mutate' on that mutation object with
  an id as its only argument, it will send a DELETE request for the given id"
  [opts]
  (use-mutation delete-req! opts))

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
           ;;todo can I not convert to clj in the fetch wrapper function?
           (let [user (->clj user)]
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
