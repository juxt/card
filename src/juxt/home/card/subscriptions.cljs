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

(defn resolve-references [node index]
  (update
   node
   :juxt.card.alpha/content
   (fn [content]
     (mapv (fn [segment]
            (cond
              (string? segment)
              (if-let [subnode (get index segment)]
                (resolve-references subnode index)
                segment)
              :else segment))
          content))))

(rf/reg-sub
 ::card
 (fn [db [_ id]]
   (let [card (get-in db [:doc-store id])]
     (resolve-references card (:doc-store db)))))
