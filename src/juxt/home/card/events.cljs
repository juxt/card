;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.events
  (:require
   [juxt.home.card.config :as config]
   [reagent.dom]
   [re-frame.core :as rf]
   [tick.alpha.api :as t]
   [day8.re-frame.tracing :refer [fn-traced]]))

(rf/reg-event-fx
 :initialize
 (fn-traced [_ _]
   {:fx [[:dispatch [:get-user]]]}))

(rf/reg-event-fx
 :get-user
 (fn-traced [db [kw]]
   (->
    (js/fetch (str config/site-api-origin "/_site/user")
              #js {"credentials" "include"
                   "headers" #js {"accept" "application/json"}})
    (.then (fn [response] (.json response)))
    (.then (fn [json] (rf/dispatch [:received-user json])
)))
   ;; Remember to return at least a map
   {}))

(rf/reg-event-db
 :received-user
 (fn [db [kw json]]
   (println "Received user")
   (assoc db :user (js->clj json :keywordize-keys true))))
