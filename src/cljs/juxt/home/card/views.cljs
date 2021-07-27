;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.views
  (:require
   [cljs.pprint :as pprint]
   [juxt.home.card.navigation :as nav]
   [juxt.home.card.config :as config]
   [juxt.home.card.util :as u]
   [juxt.home.card.subscriptions :as sub]
   [juxt.home.card.slate :as slate]
   [juxt.home.card.kanban :as kanban]
   [re-frame.core :as rf]
   [tailwind-hiccup.core :refer [tw]]
   ["/juxt/card/navbar" :refer (NavBar)]))

(def menu-items [{"label" "All Cards"
                  :target ::nav/cards}
                 {"label" "Kanban"
                  :target  ::nav/kanban}])

(defn menu []
  (let [page @(rf/subscribe [::sub/page])]
    [:> NavBar {:logo "https://home.juxt.site/x-on-dark.svg"
                :navigation
                (for [{:keys [target] :as item} menu-items]
                  (assoc item
                         :href (u/route->url target)
                         :current (= page target)))
                :user {:name "Jeremy Taylor"
                       :email "jdt@juxt.pro"
                       :imageUrl "https://home.juxt.site/_site/users/jdt/slack/jdt.jpg"}}]))

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

       ;; else
       [:div [:h1 "Page not ready"]])]))
