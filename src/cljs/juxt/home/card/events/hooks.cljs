(ns juxt.home.card.events.hooks
  (:require
   [potpuri.core :refer [deep-merge]]
   [cljs-bean.core :refer [->clj ->js]]
   [juxt.home.card.config :as config]
   [juxt.home.card.graphql :as graphql]
   [juxt.home.card.common :as common]
   [juxt.home.card.query-hooks :as query-hooks]
   [clojure.string :as str]))

(defn use-events
  []
  (query-hooks/use-query
   ["my-events" "TODO user id"]
   #(query-hooks/graphql-q graphql/all-holidays {})))

(defn use-holidays
  ([] (use-holidays {}))
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
      (deep-merge
       {:query-opts {:placeholderData []
                     :staleTime (* 1000 60 60 24)}}
       opts)))))

(defn use-my-holidays
  []
  (let [{:keys [data]} (query-hooks/use-self)
        holidays (use-holidays
                  {:user-id (:id data)
                   :query-opts
                   ;; doing the processing in this select opt means
                   ;; this hook won't cause a rerender when other
                   ;; holiday items are changed
                   {:select #(map common/format-holiday %)
                    ;; my holidays will only be modified by me, so
                    ;; no need to refetch (we invalidate manually
                    ;; when a mutation happens)
                    :staleTime js/Infinity}})]
    holidays))

(defn use-timesheets
  ([] (use-timesheets {}))
  ([{:keys [user-id] :as opts}]
   (let [received-timesheets
         (fn process-events
           [events]
           (let [events (->clj events)
                 grouped-events
                 (->> events
                      flatten
                      (filter #(= user-id (:juxt.pass.alpha/user %)))
                      (remove #(nil? (:juxt.pass.alpha/user %)))
                      (group-by :juxt.pass.alpha/user))]
             (if user-id
                                 ;;we just want one users events if a user id is passed
               (get grouped-events user-id)
                                 ;; otherwise return everything
               grouped-events)))]
     (query-hooks/use-query
      (conj ["timesheets"] user-id)
      #(-> (query-hooks/fetch (str config/site-api-origin "/card/timesheets"))
           (.then received-timesheets))
      (deep-merge
       {:query-opts {:placeholderData []
                     :staleTime (* 1000 60 60 24)}}
       opts)))))

(defn use-my-timesheets
  []
  (let [{:keys [data]} (query-hooks/use-self)
        timesheets (use-timesheets
                    {:user-id (:id data)
                     :query-opts
                     {:select #(map common/format-timesheet %)
                      :staleTime js/Infinity}})]
    timesheets))

(defn use-delete-event
  []
  (let [{:keys [data]} (query-hooks/use-self)
        user-id (:id data)
        ;; TODO don't hardcode key, could be holidays/timesheets but need to
        ;; make some js changes so we store type in the event data
        key #js ["holidays" user-id]]
    (query-hooks/use-delete-mutation
     {:key key
      :mutation-fn-props
      {:toast
       {:pending "Deleting event..."
        :success "Event deleted! 💥"
        :error "Error deleting event..."}}})))

(defn prepare-holiday
  [event user-id]
  (let [{:keys [start end id type
                title isStartHalfDay isEndHalfDay
                ;; if this is called from a fullcalendar event, props not
                ;; supported by fullcalendar (isStartHalfDay etc) will be in
                ;; extendedProps
                extendedProps]} event
        id (if (empty? id)
             (str
              config/site-api-origin
              "/card/holidays/"
              (random-uuid))
             id)
        holiday {:crux.db/id id
                 :start start
                 :end end
                 :half-start (or isStartHalfDay
                                 (:isStartHalfDay extendedProps))
                 :half-end (or isEndHalfDay
                               (:isEndHalfDay extendedProps))
                 :description title
                 :juxt.site.alpha/type "Holiday"
                 :juxt.pass.alpha/user user-id}]
    holiday))

(defn prepare-timesheet
  [event user-id]
  (let [{:keys [start end id type
                title project
                ;; if this is called from a fullcalendar event, props not
                ;; supported by fullcalendar (project etc) will be in
                ;; extendedProps
                extendedProps]} event
        id (if (empty? id)
             (str
              config/site-api-origin
              "/card/timesheets/"
              (random-uuid))
             id)
        timesheet {:crux.db/id id
                   :start start
                   :end end
                   :project project
                   :description title
                   :juxt.site.alpha/type "timesheet"
                   :juxt.pass.alpha/user user-id}]
    timesheet))

(defn use-update-event
  []
  (let [{:keys [data]} (query-hooks/use-self)
        user-id (:id data)
        ;; TODO don't hardcode key, could be holidays/timesheets but need to
        ;; make some js changes so we store type in the event data
        holiday? true
        key #js [(if holiday? "holidays" "timesheets") user-id]
        prepare-event (if holiday?
                        prepare-holiday
                        prepare-timesheet)]
    (query-hooks/use-create-mutation
     {:key key
      :process-item-fn #(prepare-event % user-id)
      :fetch-fn (fn [event]
                  (let [event (prepare-event (->clj event) user-id)]
                    (prn "creating new " event)
                    (query-hooks/create-req!
                     (:crux.db/id event)
                     {:body (query-hooks/prepare-body event)
                      :toast
                      {:pending "Updating event..."
                       :success "Event updated! 🎉"
                       :error "Error updating event... 😭🤷‍♀️"}})))})))
