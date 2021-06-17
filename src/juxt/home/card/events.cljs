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
     (update db :card-components (fnil merge {}) components))))

(rf/reg-event-db
 :new-paragraph
 (fn [db [_ card-id]]
   (println "Create a new paragraph on" card-id)

   ;; We create a new entity (which we'll POST to Crux ofc!)

   ;; Set the :juxt.site.alpha/type to 'Paragraph'
   ;; Set the content to a seeded value
   ;; Update the card relating to card-id with the id
   ;; Insert the new entity into card-components

   (let [id (str config/site-api-origin "/card/cards/" (str (random-uuid)))
         new-para {:crux.db/id id
                   :juxt.site.alpha/type "Paragraph"
                   :content [["text" ""]]}]
     (-> db
         (update-in [:card-components card-id :content] conj id)
         (assoc-in [:card-components id] new-para)))))

(rf/reg-event-fx
 :save-paragraph
 (fn [{:keys [db]} [_ id new-value]]
   (let [old-card (get-in db [:card-components id])
         _ (assert old-card)
         new-card (assoc old-card :content (vec
                                            (for [child (gobj/get (first new-value) "children")]
                                              (if-let [id (gobj/get child "_id")]
                                                id
                                                [(gobj/get child "_type") (gobj/get child "text")]))))]
     {:db (assoc-in db [:card-components id] new-card)
      :fx [[:dispatch [:put-entity new-card]]]})))

(rf/reg-event-fx
 :put-entity
 (fn [_ [_ entity]]
   (js/fetch (:crux.db/id entity)
             (clj->js
              {"credentials" "include"
               "headers" {"content-type" "application/json"}
               "method" "put"
               "body" (js/JSON.stringify (clj->js entity))}))
   {}))
