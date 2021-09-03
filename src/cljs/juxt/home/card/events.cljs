;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.events
  (:require
   ;;[ajax.core :as ajax]
   ;;[day8.re-frame.http-fx]
   [juxt.home.card.config :as config]
   [reagent.core :as reagent]
   [reagent.dom]
   [re-frame.core :as rf]
   [goog.object :as gobj]
   [goog.dom :as gdom]
   [superstructor.re-frame.fetch-fx]
   [juxt.home.card.navigation :as nav]))

(rf/reg-event-fx
 :initialize
 (fn [{:keys [db]} _]
   {:fx [[:dispatch [:get-people]]
         [:dispatch [:get-holidays]]
         [:dispatch [:get-self]]]}))

(rf/reg-event-fx
 :set-current-card
 (fn [{:keys [db]} _]
   (let [card-id (get-in db [:current-route :path-params :card])]
     {:db (assoc db :current-card (str config/site-api-origin "/card/cards/" card-id))
      :fx [[:dispatch [:get-card card-id]]]})))

(rf/reg-event-fx
 :get-card
 (fn [{:keys [db]} [_ id]]
   {:fetch
    {:method :get
     :url (str config/site-api-origin "/card/components/" id)
     :timeout 5000
     :mode :cors
     :response-content-types {#"application/.*json" :json}
     :on-success [:received-card-components]
     :on-failure [:http-failure]}}))

(rf/reg-event-fx
 :get-cards
 (fn [{:keys [db]} _]
   {:fetch
    {:method :get
     :url (str config/site-api-origin "/card/cards/")
     :timeout 5000
     :mode :cors
     :response-content-types {#"application/.*json" :json}
     :on-success [:received-cards]
     :on-failure [:http-failure]}}))

(rf/reg-event-fx
 :get-actions
 (fn [{:keys [db]} _]
   {:fetch
    {:method :get
     :url (str config/site-api-origin "/card/actions/")
     :timeout 5000
     :mode :cors
     :response-content-types {#"application/.*json" :json}
     :on-success [:received-actions]
     :on-failure [:http-failure]}}))

(rf/reg-event-fx
 :get-people
 (fn [{:keys [db]} _]
   {:fetch
    {:method :get
     :url (str config/site-api-origin "/card/users/")
     :timeout 5000
     :mode :cors
     :response-content-types {#"application/.*json" :json}
     :on-success [:received-people]
     :on-failure [:http-failure]}}))

(rf/reg-event-fx
 :get-holidays
 (fn [{:keys [db]} _]
   {:fetch
    {:method :get
     :url (str config/site-api-origin "/card/holidays")
     :timeout 5000
     :mode :cors
     :response-content-types {#"application/.*json" :json}
     :on-success [:received-holidays]
     :on-failure [:http-failure]}}))

(rf/reg-event-fx
 :get-self
 (fn [{:keys [db]} _]
   {:fetch
    {:method :get
     :url (str config/site-api-origin "/_site/user")
     :timeout 5000
     :mode :cors
     :response-content-types {#"application/.*json" :json}
     :on-success [:received-self]
     :on-failure [:http-failure]}}))

(rf/reg-event-fx
 :received-self
 (fn [{:keys [db]} [_ {self :body}]]
   {:db (assoc db :user-info self)}))

(rf/reg-event-fx
 :http-failure
 (fn [_ o]
   (println "HTTP FAILURE!" o)))

(rf/reg-event-db
 :received-card-components
 (fn [db [kw card]]
   (let [components
         (->> card
              :body
              (map (juxt :crux.db/id identity))
              (into {}))]
     (update db :doc-store (fnil merge {}) components))))

(rf/reg-event-db
 :received-cards
 (fn [db [_ response]]
   (assoc db :cards (:body response))))

(rf/reg-event-db
 :received-actions
 (fn [db [_ response]]
   (-> db
       (update :doc-store (fnil merge {})
               (into {}
                     (for [{:keys [action]} (:body response)]
                       [(:crux.db/id action) action])))
       (assoc :actions (:body response)))))

(rf/reg-event-db
 :received-people
 (fn [db [_ response]]
   (assoc db :people (:body response))))

(rf/reg-event-db
 :received-holidays
 (fn [db [_ response]]
   (let [raw-holidays (:body response)
         holidays (group-by :juxt.pass.alpha/user (flatten raw-holidays))]
     (assoc db :holidays holidays))))

(rf/reg-event-fx
 :put-holiday
 (fn [{db :db} [_ holiday]]
   (let [user (get-in db [:user-info :id])
         {:keys [allDay start end id description]} holiday
         holiday {:crux.db/id (if (empty? id)
                                (str
                                 config/site-api-origin
                                 "/card/holidays/"
                                 (random-uuid))
                                id)
                  :start start
                  :end end
                  :description description
                  :all-day? allDay
                  :juxt.site.alpha/type "Holiday"
                  :juxt.pass.alpha/user user}]
     {:fx [[:dispatch [:put-entity
                       holiday
                       [[:dispatch [:get-holidays]]]]]]})))

(rf/reg-event-fx
 :put-timesheet
 (fn [{db :db} [_ timesheet]]
   (let [user (get-in db [:user-info :id])
         username (get-in db [:user-info :username])
         {:keys [allDay start end id description code]} timesheet
         timesheet {:crux.db/id (if (empty? id)
                                  (str
                                   config/site-api-origin
                                   "/card/users/"
                                   username
                                   "/timesheet/"
                                   (random-uuid))
                                  id)
                    :start start
                    :end end
                    :description description
                    :all-day? allDay
                    :code code
                    :juxt.site.alpha/type "Timesheet"
                    :juxt.pass.alpha/user user}]
     {:fx [[:dispatch [:put-entity timesheet]]]})))

(rf/reg-event-fx
 :update-event
 (fn [{db :db} [_ js-event]]
   (let [event-type :holiday
         event (js->clj js-event :keywordize-keys true)]
     {:fx [[:dispatch [:put-holiday event]]]})))

(rf/reg-fx
 :focus-to-element
 (fn [element-id]
   (reagent/after-render #(if-let [el (gdom/getElement element-id)]
                            (.focus el)
                            (if-let [focus! (get (or (aget js/window "focusmap") {}) element-id)]
                              (focus!)
                              (println "DOM node not found" element-id))))))

(defn mark-optimistic [o]
  (assoc o :optimistic true))

(defn clear-optimistic [o]
  (dissoc o :optimistic))

(rf/reg-event-fx
 :new-paragraph
 (fn [{:keys [db]} [_ container-id ix]]
   (let [child-id (str config/site-api-origin "/card/cards/" (str (random-uuid)))
         new-child {:crux.db/id child-id
                    :juxt.site.alpha/type "Paragraph"
                    :juxt.card.alpha/content [{"text" ""}]}
         container (get-in db [:doc-store container-id])
         new-container (update container :juxt.card.alpha/children
                               (fn [v]
                                 (into
                                  (into (subvec v 0 ix) [child-id])
                                  (subvec v ix))))]
     {:db (-> db
              (assoc-in [:doc-store container-id] (mark-optimistic new-container))
              (assoc-in [:doc-store child-id] (mark-optimistic new-child)))
      :focus-to-element child-id
      :fx [[:dispatch [:put-entity new-child]]
           [:dispatch [:put-entity new-container]]]})))

(defn drop-nth [coll n]
  (vec (keep-indexed #(if (not= %1 n) %2) coll)))

(rf/reg-event-fx
 :unlink-paragraph
 (fn [{:keys [db]} [_ container-id ix]]
   (println "Unlink paragraph: container-id" container-id "ix" ix)
   (let [container (get-in db [:doc-store container-id])
         previous-sibling-id (when (> (dec ix) 0)
                               (nth (:juxt.card.alpha/children container) (dec ix)))
         new-container (update container :juxt.card.alpha/children drop-nth ix)]
     (cond-> {:db (-> db
                      (assoc-in [:doc-store container-id] (mark-optimistic new-container)))
              :fx [[:dispatch [:put-entity new-container]]]}
       previous-sibling-id
       (assoc :focus-to-element previous-sibling-id)))))

(rf/reg-event-fx
 :save-paragraph
 (fn [{:keys [db]} [_ id new-value]]
   (let [old-para (get-in db [:doc-store id])
         _ (assert old-para)
         new-para
         (assoc
          old-para
          :juxt.card.alpha/content
          (vec
           (for [child (gobj/get (first new-value) "children")]
             (if-let [id (gobj/get child "_id")]
               id
               (clj->js (dissoc (js->clj child) "_ix"))))))]
     (if (= old-para new-para)
       {:db db}
       {:db (assoc-in db [:doc-store id] (mark-optimistic new-para))
        :fx [[:dispatch [:put-entity new-para]]]}))))

(rf/reg-event-fx
 :mark-save-succeeded
 (fn [{:keys [db]} [_ id fx]]
   {:db (update-in db [:doc-store id] clear-optimistic)
    :fx fx}))

(rf/reg-event-fx
 :mark-save-failed
 (fn [{:keys [db]} [_ id]]
   {:db (update-in db [:doc-store id] assoc :error true)}))

(rf/reg-event-fx
 :delete-entity
 (fn [_ [_ id fx]]
   {:fetch
    {:method :delete
     :url id
     :timeout 5000
     :mode :cors
     :on-success [:mark-save-succeeded id (or fx [])]
     :on-failure [:http-failure]}}))

(rf/reg-event-fx
 :put-entity
 (fn [{:keys [db]} [_ entity fx]]
   (let [id (:crux.db/id entity)
         ;; TODO: Let's avoid calling put-entity when there's already one in flight
         entity (dissoc entity :optimistic)
         body (js/JSON.stringify (clj->js entity :keyword-fn #(subs (str %) 1)))]
     {:fetch
      {:method :put
       :url id
       :timeout 5000
       :mode :cors
       :body body
       :headers {"content-type" "application/json"}
       :on-success [:mark-save-succeeded id (or fx [])]
       :on-failure [:http-failure]}})))

(rf/reg-event-fx
 :check-action
 (fn [{:keys [db]} [_ id]]
   (let [old-card (get-in db [:doc-store id])
         new-card (assoc old-card :juxt.card.alpha/status "DONE")]
     {:db (assoc-in db [:doc-store id] (mark-optimistic new-card))
      :fx [[:dispatch [:put-entity new-card]]]})))

(rf/reg-event-fx
 :uncheck-action
 (fn [{:keys [db]} [_ id]]
   (let [old-card (get-in db [:doc-store id])
         new-card (assoc old-card :juxt.card.alpha/status "TODO")]
     {:db (assoc-in db [:doc-store id] (mark-optimistic new-card))
      :fx [[:dispatch [:put-entity new-card]]]})))

(rf/reg-event-fx
 :set-attribute
 (fn [{:keys [db]} [_ id attr val]]
   (let [old-card (get-in db [:doc-store id])
         new-card (assoc old-card attr val)]
     (assert old-card (str "Failed to get doc with id " id))
     {:db (assoc-in db [:doc-store id] (mark-optimistic new-card))
      :fx [[:dispatch [:put-entity new-card]]]})))

;; Consider reinstating for multiple types
#_(rf/reg-event-fx
 :conj-attribute
 (fn [{:keys [db]} [_ id attr val]]
   (let [old-card (get-in db [:doc-store id])
         _ (assert old-card (str "Failed to get doc with id " id))
         new-card (update old-card attr conj val)]
     {:db (assoc-in db [:doc-store id] (mark-optimistic new-card))
      :fx [[:dispatch [:put-entity new-card]]]})))

(rf/reg-event-fx
 :delete-attribute
 (fn [{:keys [db]} [_ id attr]]
   (let [old-card (get-in db [:doc-store id])
         new-card (dissoc old-card attr)]
     (assert old-card)
     {:db (assoc-in db [:doc-store id] (mark-optimistic new-card))
      :fx [[:dispatch [:put-entity new-card]]]})))

(rf/reg-event-fx
 :new-card
 (fn [{:keys [db]} _]
   (let [segment (str (random-uuid))
         card-id (str config/site-api-origin "/card/cards/" segment)
         card-init-para-id (str config/site-api-origin "/card/cards/" (str (random-uuid)))
         card {:crux.db/id card-id
               :juxt.card.alpha/title ""
               :juxt.card.alpha/subtitle ""
               :juxt.card.alpha/children [card-init-para-id]}
         para {:crux.db/id card-init-para-id
               :juxt.card.alpha/content [{"text" ""}]}]
     (prn card)
     {:db (-> db
              (assoc-in [:doc-store card-id] (mark-optimistic card))
              (assoc-in [:doc-store card-init-para-id] (mark-optimistic para))
              ;; TODO: Let's deprecated :current-card and replace by :current-route
              (assoc :current-card card-id))
      :fx [[:dispatch [:put-entity card]]
           [:dispatch [:put-entity para]]
           [:dispatch [:navigate ::nav/card {:card segment}]]]})))

(rf/reg-event-fx
 :delete-card
 (fn [{:keys [db]} [_ id]]
   (println "Event: delete card" id)
   {:db (cond-> db
          id (update :doc-store dissoc id)
          (= (:current-card db) id) (assoc :current-card nil))
    :fx [(when (= (:current-card db) id)
           [:dispatch [:navigate ::nav/cards]])
         [:dispatch [:delete-entity id [[:dispatch [:get-cards]]]]]]}))


(defn swap
  [items i j]
  (assert (< -1 i (count items)))
  (assert (< -1 j (count items)))
  (assoc items i (items j) j (items i)))

(rf/reg-event-fx
 :swap-children
 (fn [{:keys [db]} [_ id source-index destination-index]]
   (let [card (get-in db [:doc-store id])
         new-card (update card :juxt.card.alpha/children swap source-index destination-index)]
     {:db (assoc-in db [:doc-store id] (mark-optimistic new-card))
      :fx [[:dispatch [:put-entity new-card]]]})))
