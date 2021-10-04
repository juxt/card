(ns juxt.home.card.query-hooks
  (:require
   [clojure.string :as string]
   [juxt.home.card.config :as config]
   [juxt.home.card.graphql :as graphql]
   [juxt.home.card.people.model :as people-model]
   [cljs-bean.core :refer [->clj ->js]]
   [react-query :refer [useQuery]]))

(defn fetch
  ([url] (fetch url {}))
  ([url opts]
   (let [controller ^js (new js/window.AbortController)
         signal (.-signal controller)
         promise (.fetch
                  js/window url
                  (clj->js (merge {:method "GET"
                                   :signal signal
                                   :headers {"Content-Type" "application/json"}
                                   :timeout 5000
                                   :credentials "include"}
                                  opts)))]
     (set! (.-cancel promise) (fn cancelFetch [] (.abort controller)))
     (.then promise #(.json %)))))

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
   (let [hook (useQuery id fetcher (if clj (->js query-opts) query-opts))]
     (if clj (->clj hook) (->js hook)))))

(def initialUser {:id "loading"
                  :email "Loading..."
                  :fields {}
                  :name "Loading..."
                  :imageUrl "https://www.gravatar.com/avatar?d=mp"})

(defn use-self
  ([] (use-self nil))
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
     (use-query "self"
                #(-> (fetch (str config/site-api-origin "/_site/user"))
                     (.then assoc-image))
                (merge
                 {:query-opts {:placeholderData initialUser
                               :retry false
                           ;; user data is cached until page is refreshed
                               :staleTime js/Infinity}}
                 opts)))))

;; showing example of graphql query, can't use it for hols until we fix the cors
;; issue on home.juxt.site/graphql
(defn use-events
  []
  (use-query "my-events"
             #(-> (graphql-q graphql/gql-test {:now (.toISOString (new js/Date))}))))

(defn use-holidays
  ([] (use-holidays nil))
  ([opts]
   (let [received-holidays (fn process-hols
                             [hols]
                             (let [hols (->clj hols)]
                               (->> hols
                                    flatten
                                    (remove #(nil? (:juxt.pass.alpha/user %)))
                                    (group-by :juxt.pass.alpha/user)
                                    ->js)))]
     (use-query "holidays"
                #(-> (fetch (str config/site-api-origin "/card/holidays"))
                     (.then received-holidays))
                (merge
                 {:query-opts {:placeholderData []
                               :staleTime (* 1000 60 60 24)}}
                 opts)))))

(defn format-holiday
  [{:keys [start-date end-date start end crux.db/id all-day? description]}]
  {:id id
   :start (or start start-date)
   :end (or end end-date)
   :allDay (not (false? all-day?))
   :title description})

(defn use-my-holidays
  []
  (let [{:keys [data]} (use-self)
        user-id (keyword (:id data))
        holidays (use-holidays {:query-opts
                                ;; doing the processing in this select opt means
                                ;; this hook won't cause a rerender when other
                                ;; holiday items are changed
                                {:select #(->js
                                           (map format-holiday
                                                ;; fetch just my holidays
                                                (get (->clj %) user-id)))
                                 ;; my holidays will only be modified by me, so
                                 ;; no need to refetch (we invalidate manually
                                 ;; when a mutation happens)
                                 :staleTime js/Infinity}})]
    holidays))

(defn use-people
  ([] (use-people nil))
  ([opts]
   (let [received-people (fn process-people
                           [people]
                           (->> (map people-model/process-user (->clj people))
                                (group-by (fn last-initial [{full-name :name}]
                                            (first (last (string/split full-name " ")))))))]
     (use-query "people"
                #(-> (fetch (str config/site-api-origin "/card/users/"))
                     (.then received-people))
                (merge
                 {:query-opts {:placeholderData []
                               :staleTime (* 1000 60 60 24)}}
                 opts)))))
