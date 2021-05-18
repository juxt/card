;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.subscriptions
  (:require
   [re-frame.core :as rf]
   [tick.alpha.api :as t]))

(rf/reg-sub
 ::current-route
 (fn [db _]
   (:current-route db)))

(rf/reg-sub
 ::page
 :<- [::current-route]
 (fn [current-route _]
   (get-in current-route [:data :name])))

(rf/reg-sub
 ::user-details
 (fn [db _]
   (get-in db [:user])))
