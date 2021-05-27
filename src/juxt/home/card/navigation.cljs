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
         controllers (rfc/apply-controllers (:controllers old-match) new-match)]
     {:db (assoc db :current-route (assoc new-match :controllers controllers))})))

(defn on-navigate [new-match]
  (when new-match
    (rf/dispatch [::navigated new-match])))

(def router
  (reitit/router
   [config/application-context
    ["index.html" {:name ::index}]
    ["kanban.html" {:name ::kanban}]]
   {:data {:coercion rss/coercion}}))

(defn init-routes! []
  (rfe/start!
   router
   on-navigate
   {:use-fragment false}))
