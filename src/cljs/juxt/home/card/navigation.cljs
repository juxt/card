;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.navigation
  (:require
   [juxt.home.card.config :as config]
   [re-frame.core :as rf]
   [reitit.frontend :as reitit]
   [reitit.coercion.spec :as rss]
   [reitit.frontend.controllers :as rfc]
   [reitit.frontend.easy :as rfe]))

(rf/reg-fx
 :navigate!
 (fn [route]
   (apply rfe/push-state route)))

(rf/reg-event-fx
 :navigate
 (fn [_ [_ & route]]
   {:navigate! route}))

(rf/reg-event-fx
 ::navigated
 (fn [{:keys [db]} [_ new-match]]
   (let [old-match (:current-route db)
         controllers (rfc/apply-controllers (:controllers old-match) new-match)
         data (:data new-match)]

     (merge
      {:db (-> db
               (assoc
                :current-route
                (assoc new-match :controllers controllers)
                :show-menu? false))}
      (if-let [fx (:fx data)]
        {:fx fx}
        {})))))

(defn on-navigate [new-match]
  (when new-match
    (rf/dispatch [::navigated new-match])))

(def router
  (reitit/router
   [config/application-context
    ["kanban" {:name ::kanban
               :fx [[:dispatch [:get-actions]]]}]
    ["cards"
     ["" {:name ::cards
          :fx [[:dispatch [:get-cards]]]}]
     ["/:card" {:name ::card
               :fx [[:dispatch [:set-current-card]]]}]]

    ["people" {:name ::people
                :fx [[:dispatch [:get-people]]]}]]

   {:data {:coercion rss/coercion}}))

(defn init-routes! []
  (rfe/start!
   router
   on-navigate
   {:use-fragment false}))
