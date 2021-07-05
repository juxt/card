;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.views
  (:require
   [cljs.pprint :as pprint]
   [juxt.home.card.navigation :as nav]
   [juxt.home.card.config :as config]
   [juxt.home.card.subscriptions :as sub]
   [juxt.home.card.slate :as slate]
   [re-frame.core :as rf]
   [tailwind-hiccup.core :refer [tw]]))

(defn ui []
  (let [page @(rf/subscribe [::sub/page])]
    [:div
     (case page
       ::nav/card
       [:<>
        [slate/card]
        [slate/new]]

       ::nav/cards
       [:<>
        [slate/cards]
        [slate/new]]

       ;; else
       [:div [:h1 "Page not ready"]])]))
