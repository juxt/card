;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.events
  (:require
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]
   [juxt.home.card.config :as config]
   [reagent.core :as reagent]
   [reagent.dom]
   [re-frame.core :as rf]
   [goog.object :as gobj]
   [goog.dom :as gdom]))

(rf/reg-event-fx
 :initialize
 (fn [{:keys [db]} _]))

(rf/reg-event-fx
 :set-current-card
 (fn [{:keys [db]} _]
   (let [card-id (get-in db [:current-route :path-params :card])]
     {:db (assoc db :current-card (str config/site-api-origin "/card/cards/" card-id))
      :fx [[:dispatch
            [:get-card card-id]]]})))

(defn http-request [db m]
  {:db (assoc db :loading? true)
   :http-xhrio (merge {:timeout 8000
                       :with-credentials true
                       :response-format (ajax/json-response-format {:keywords? true})
                       :on-failure [:http-failure]} m)})

(defn get-request [db m]
  (http-request db (merge {:method :get} m)))

(defn put-request [db m]
  ;; This mess is only while we're waiting for the latest ajax to be released
  ;; with a fix for stringified namespaced-keys. Usually we'd just use a
  ;; ajax/request-format with :params as the entity
  (http-request db (merge {:method :put
                           :headers {"content-type" "application/json"}
                           :body (js/JSON.stringify (clj->js (:params m) :keyword-fn (fn [x] (subs (str x) 1))))}
                          m)))

(rf/reg-event-fx
 :get-card
 (fn [{:keys [db]} [_ id]]
   (get-request db {:uri (str config/site-api-origin "/card/components/" id)
                    :on-success [:received-card-components]})))

(rf/reg-event-db
 :received-card-components
 (fn [db [kw json]]
   (println "Received card")
   (let [components
         (->> (js->clj json :keywordize-keys true)
              (map (juxt :crux.db/id identity))
              (into {}))]
     (update db :doc-store (fnil merge {}) components))))

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
   (println "create para from " ix)
   (let [child-id (str config/site-api-origin "/card/cards/" (str (random-uuid)))
         new-child {:crux.db/id child-id
                    :juxt.site.alpha/type "Paragraph"
                    :juxt.card.alpha/content [["text" ""]]}
         container (get-in db [:doc-store container-id])
         new-container (update container :juxt.card.alpha/content
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
   (let [container (get-in db [:doc-store container-id])
         previous-sibling-id (when (> (dec ix) 0)
                               (nth (:juxt.card.alpha/content container) (dec ix)))
         new-container (update container :juxt.card.alpha/content drop-nth ix)]
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
               [(gobj/get child "_type") (gobj/get child "text")]))))]
     (if (= old-para new-para)
       {:db db}
       {:db (assoc-in db [:doc-store id] (mark-optimistic new-para))
        :fx [[:dispatch [:put-entity new-para]]]}))))

(rf/reg-event-fx
 :mark-save-succeeded
 (fn [{:keys [db]} [_ id]]
   {:db (update-in db [:doc-store id] clear-optimistic)}))

(rf/reg-event-fx
 :mark-save-failed
 (fn [{:keys [db]} [_ id]]
   {:db (update-in db [:doc-store id] assoc :error true)}))

(rf/reg-event-fx
 :put-entity
 (fn [{:keys [db]} [_ entity]]
   (let [id (:crux.db/id entity)]
     (put-request
      db
      {:uri id
       :params entity ;;(js/JSON.stringify (clj->js entity :keyword-fn (fn [x] (subs (str x) 1))))
       :on-success [:mark-save-succeeded id]
       :on-failure [:mark-save-failed id]})
     )
   ))

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
     {:db (assoc-in db [:doc-store id] (mark-optimistic new-card))
      :fx [[:dispatch [:put-entity new-card]]]})))

(rf/reg-event-fx
 :delete-attribute
 (fn [{:keys [db]} [_ id attr]]
   (let [old-card (get-in db [:doc-store id])
         new-card (dissoc old-card attr)]
     {:db (assoc-in db [:doc-store id] (mark-optimistic new-card))
      :fx [[:dispatch [:put-entity new-card]]]})))

(rf/reg-event-fx
 :new-card
 (fn [{:keys [db]} _]
   (let [card-id (str config/site-api-origin "/card/cards/" (str (random-uuid)))
         card-init-para-id (str config/site-api-origin "/card/cards/" (str (random-uuid)))
         card {:crux.db/id card-id
               :juxt.card.alpha/content [card-init-para-id]}
         para {:crux.db/id card-init-para-id
               :juxt.card.alpha/content [["text" ""]]}]
     (prn card)
     {:db (-> db
              (assoc-in [:doc-store card-id] (mark-optimistic card))
              (assoc-in [:doc-store card-init-para-id] (mark-optimistic para))
              (assoc :current-card card-id))
      :fx [[:dispatch [:put-entity card]]
           [:dispatch [:put-entity para]]]})))
