;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.events
  (:require
   [juxt.home.card.config :as config]
   [reagent.dom]
   [re-frame.core :as rf]
   [goog.object :as gobj]))

(rf/reg-event-fx
 :initialize
 (fn [_ _]
   {:fx [[:dispatch
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
      :fx [[:dispatch [:put-entity new-child]]
           [:dispatch [:put-entity new-container]]]})))

(defn drop-nth [coll n]
  (vec (keep-indexed #(if (not= %1 n) %2) coll)))

(rf/reg-event-fx
 :unlink-paragraph
 (fn [{:keys [db]} [_ container-id ix]]
   (let [container (get-in db [:doc-store container-id])
         new-container (update container :content drop-nth ix)]
     {:db (-> db
              (assoc-in [:doc-store container-id] (mark-optimistic new-container)))
      :fx [[:dispatch [:put-entity new-container]]]})))

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
