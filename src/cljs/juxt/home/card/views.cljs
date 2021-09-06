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
   [juxt.home.card.people :as people]
   [juxt.home.card.calendar :as calendar]
   [re-frame.core :as rf]
   [tailwind-hiccup.core :refer [tw]]
   ["/juxt/card/stories/Navbar" :refer (NavBar)]))

(defn menu []
  (let [page @(rf/subscribe [::sub/page])
        user @(rf/subscribe [::sub/logged-in-user-profile])]
    (if user
      [:> NavBar {:logo "https://home.juxt.site/x-on-dark.svg"
                  :navigation
                  (for [{:keys [name label] :as item} nav/pages
                        :when label]
                    (assoc item
                           :id name
                           :href (u/route->url name)
                           :name label
                           :current (= page name)))
                  :user user}]
      [:div "waiting for user..."])))

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

       ::nav/people
       [people/people]

       ::nav/calendar
       [calendar/view]

       ;; else
       (do
         (prn page menu)
         [:div [:h1 "Page not ready"]]))]))
