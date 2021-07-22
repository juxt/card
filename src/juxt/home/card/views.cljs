;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.views
  (:require
   [cljs.pprint :as pprint]
   [juxt.home.card.navigation :as nav]
   [juxt.home.card.config :as config]
   [juxt.home.card.util :as u]
   [juxt.home.card.subscriptions :as sub]
   [juxt.home.card.slate :as slate]
   [juxt.home.card.test :as test]
   [juxt.home.card.kanban :as kanban]
   [re-frame.core :as rf]
   [tailwind-hiccup.core :refer [tw]]
   ["/demo/navbar" :refer (NavBar)]))

(defn menu []
  [:div
   [:> NavBar {"label" "barbar"}]
   #_[:ul (tw ["p-4" "flex" "flex-row" "space-x-2"])
    [:li (u/href "All Cards" ::nav/cards)]
    [:li (u/href "Kanban" ::nav/kanban)]
    [:li (u/href "Test" ::nav/test)]]])

(defn actions-kanban []
  (->>
   @(rf/subscribe [::sub/actions])
   (map :action)
   (group-by :juxt.card.alpha/status)
   (kanban/kanban)))

(defn ui []
  (let [page @(rf/subscribe [::sub/page])]
    [:div
     [menu]
     (case page
       ::nav/card
       [:<>
        (let [id @(rf/subscribe [::sub/current-card])]
          [slate/card-view id nil nil nil])
        [slate/new]]

       ::nav/cards
       [:<>
        [slate/cards]
        [slate/new]]

       ::nav/kanban
       [:<>
        [actions-kanban]
        [slate/new]]

       ::nav/test
       [:<>
        [test/test123]]

       ;; else
       [:div [:h1 "Page not ready"]])]))
