;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.events
  (:require
   [juxt.home.card.config :as config]
   [reagent.core :as reagent]
   [reagent.dom]
   [re-frame.core :as rf]
   [goog.object :as gobj]
   [goog.dom :as gdom]))

(rf/reg-event-fx
 :initialize
 (fn [_ _]
   {;; https://code.juxt.site/home/card/issues/2
    #_#_
    :interval {:action :start
               :id :main-global-doc-refresh
               :frequency 5000
               :event [:global-doc-refresh]}
    :fx [[:dispatch
          [:get-card "section-containing-checklist-1"
           #_"task-1"]]]}))

(rf/reg-event-fx
 :get-card
 (fn [_ [_ id]]
   (->
    (js/fetch (str config/site-api-origin "/card/components/" id)
              #js {"credentials" "include"
                   "headers" #js {"accept" "application/json"}})
    (.then (fn [response] (.json response)))
    (.then (fn [json] (rf/dispatch [:received-card-components json])
)))
   ;; Remember to return at least a map
   {}))

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

(rf/reg-event-fx
 :new-paragraph
 (fn [{:keys [db]} [_ container-id ix]]
   (println "create para from " ix)
   (let [child-id (str config/site-api-origin "/card/cards/" (str (random-uuid)))
         new-child {:crux.db/id child-id
                    :juxt.site.alpha/type "Paragraph"
                    :content [["text" ""]]}
         container (get-in db [:doc-store container-id])
         new-container (update container :content
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
                               (nth (:content container) (dec ix)))
         new-container (update container :content drop-nth ix)]
     (cond-> {:db (-> db
                      (assoc-in [:doc-store container-id] (mark-optimistic new-container)))
              :fx [[:dispatch [:put-entity new-container]]]}
       previous-sibling-id
       (assoc :focus-to-element previous-sibling-id)))))

(rf/reg-event-fx
 :save-paragraph
 (fn [{:keys [db]} [_ id new-value]]
   (let [old-card (get-in db [:doc-store id])
         _ (assert old-card)
         new-card (assoc old-card :content (vec
                                            (for [child (gobj/get (first new-value) "children")]
                                              (if-let [id (gobj/get child "_id")]
                                                id
                                                [(gobj/get child "_type") (gobj/get child "text")]))))]
     (if (= old-card new-card)
       {:db db}
       {:db (assoc-in db [:doc-store id] (mark-optimistic new-card))
        :fx [[:dispatch [:put-entity new-card]]]}))))

(rf/reg-event-fx
 :mark-save-succeeded
 (fn [{:keys [db]} [_ id]]
   {:db (update-in db [:doc-store id] dissoc :optimistic)}))

(rf/reg-event-fx
 :mark-save-failed
 (fn [{:keys [db]} [_ id]]
   {:db (update-in db [:doc-store id] assoc :error true)}))

(rf/reg-event-fx
 :put-entity
 (fn [_ [_ entity]]
   (let [id (:crux.db/id entity)]
     (->
      (js/fetch id
                (clj->js
                 {"credentials" "include"
                  "headers" {"content-type" "application/json"}
                  "method" "put"
                  "body" (js/JSON.stringify (clj->js entity))}))
      (.then (fn [response]
               (let [status (.-status response)]
                 (if (<= 200 status 299)
                   (rf/dispatch [:mark-save-succeeded id])
                   (rf/dispatch [:mark-save-failed id])))))))
   {}))

(rf/reg-event-fx
 :check-action
 (fn [{:keys [db]} [_ id]]
   (let [old-card (get-in db [:doc-store id])
         new-card (assoc old-card :status "DONE")]
     {:db (assoc-in db [:doc-store id] (mark-optimistic new-card))
      :fx [[:dispatch [:put-entity new-card]]]})))

(rf/reg-event-fx
 :uncheck-action
 (fn [{:keys [db]} [_ id]]
   (let [old-card (get-in db [:doc-store id])
         new-card (assoc old-card :status "TODO")]
     {:db (assoc-in db [:doc-store id] (mark-optimistic new-card))
      :fx [[:dispatch [:put-entity new-card]]]})))

;; https://code.juxt.site/home/card/issues/2
#_#_#_#_
(rf/reg-fx
 :interval
 (let [live-intervals (atom {})]
   (fn [{:keys [action id frequency event]}]
     (if (= action :start)
       (swap! live-intervals assoc id (js/setInterval #(rf/dispatch event) frequency))
       (do (js/clearInterval (get @live-intervals id))
           (swap! live-intervals dissoc id))))))

(rf/reg-event-fx
 :get-doc
 (fn [{:keys [db]} [_ id]]
   (->
    (js/fetch id
              #js {"credentials" "include"
                   "headers" #js {"accept" "application/json"}})
    (.then (fn [response] (.json response)))
    (.then (fn [json] (rf/dispatch [:received-doc json]))))
   {:db db}))

(rf/reg-event-db
 :received-doc
 (fn [db [kw json]]
   (let [docs
         (->> [(js->clj json :keywordize-keys true)]
              (map (juxt :crux.db/id identity))
              (into {}))]
     (update db :doc-store (fnil merge {}) docs))))

(rf/reg-event-fx
 :global-doc-refresh
 (fn [{:keys [db]} _]
   (let [doc-events
         (doall (for [doc-id (keys (:doc-store db))]
                  [:dispatch [:get-doc doc-id]]))]
     {:fx doc-events})))
