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
   (let [user "mal";;(:user db)
         old-match (:current-route db)
         controllers (rfc/apply-controllers (:controllers old-match) new-match)
         data (:data new-match)]

     (println ">>> data is" (pr-str data))

     (merge
      {:db (-> db
               (assoc
                :current-route
                (assoc new-match :controllers controllers)
                :show-menu? false))}
      (when-let [fx (:fx data)]
        (if user
          {:fx fx}
          (js/console.warn "Tried to dispatch an 'on navigation' event without a user in db.")))))))

(defn on-navigate [new-match]
  (when new-match
    (rf/dispatch [::navigated new-match])))

(def router
  (reitit/router
   [config/application-context
    ["cards/"
     ["" {:name ::cards
          :fx [[:dispatch [:get-cards]]]}]
     [":card" {:name ::card
                :fx [[:dispatch [:set-current-card]]]}]]]
   {:data {:coercion rss/coercion}}))

(defn init-routes! []
  (rfe/start!
   router
   on-navigate
   {:use-fragment false}))
