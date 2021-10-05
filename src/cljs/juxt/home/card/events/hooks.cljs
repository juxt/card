(ns juxt.home.card.events.hooks
  (:require
   [react-query :refer [useMutation useQueryClient]]
   [cljs-bean.core :refer [->clj ->js]]
   [juxt.home.card.config :as config]
   [juxt.home.card.graphql :as graphql]
   [juxt.home.card.query-hooks :as query-hooks]))

(defn use-events
  []
  (query-hooks/use-query
   ["my-events" "TODO user id"]
   #(query-hooks/graphql-q graphql/all-holidays {})))

(defn use-holidays
  ([] (use-holidays nil))
  ([{:keys [user-id] :as opts}]
   (let [received-holidays (fn process-hols
                             [hols]
                             (let [hols (->clj hols)
                                   grouped-hols
                                   (->> hols
                                        flatten
                                        (filter #(= user-id (:juxt.pass.alpha/user %)))
                                        (remove #(nil? (:juxt.pass.alpha/user %)))
                                        (group-by :juxt.pass.alpha/user))]
                               (if user-id
                                 ;;we just want one users hols if a user id is passed
                                 (get grouped-hols user-id)
                                 ;; otherwise return everything
                                 grouped-hols)))]
     (query-hooks/use-query
      (conj ["holidays"] user-id)
      #(-> (query-hooks/fetch (str config/site-api-origin "/card/holidays"))
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
  (let [{:keys [data]} (query-hooks/use-self)
        holidays (use-holidays
                  {:user-id (:id data)
                   :query-opts
                   ;; doing the processing in this select opt means
                   ;; this hook won't cause a rerender when other
                   ;; holiday items are changed
                   {:select #(map format-holiday %)
                    ;; my holidays will only be modified by me, so
                    ;; no need to refetch (we invalidate manually
                    ;; when a mutation happens)
                    :staleTime js/Infinity}})]
    holidays))

(defn use-delete-event
  []
  (let [{:keys [data]} (query-hooks/use-self)
        user-id (:id data)
        ;; TODO don't hardcode key, could be holidays/timesheets but need to
        ;; make some js changes so we store type in the event data
        key #js ["holidays" user-id]]
    (query-hooks/use-delete-mutation
     {:mutation-fn-props
      {:toast
       {:pending "Deleting event..."
        :success "Event deleted! 💥"
        :error "Error deleting event..."}}})))