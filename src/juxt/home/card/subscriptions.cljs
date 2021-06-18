;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.subscriptions
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 ::current-route
 (fn [db _]
   (:current-route db)))

(rf/reg-sub
 ::page
 :<- [::current-route]
 (fn [current-route _]
   (get-in current-route [:data :name])))

(defn resolve-references [node index-of-new index]
  (update
   node
   :content
   (fn [content]
     (mapv (fn [segment]
            (cond
              (string? segment)
              (if-let [subnode (or (get index-of-new segment) (get index segment))]
                (resolve-references subnode index-of-new index)
                segment)
              :else segment))
          content))))

(rf/reg-sub
 ::card
 (fn [db [_ id]]
   (let [card (or (get-in db [:new-card-components id]) (get-in db [:card-components id]))]
     (resolve-references card (:new-card-components db) (:card-components db)))))
