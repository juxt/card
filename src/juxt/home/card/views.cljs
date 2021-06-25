;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.views
  (:require
   [juxt.home.card.navigation :as nav]
   [juxt.home.card.config :as config]
   [juxt.home.card.subscriptions :as sub]
   [juxt.home.card.slate :as slate]
   [re-frame.core :as rf]
   [tailwind-hiccup.core :refer [tw]]))

(defn menu []
  [:div
   [:ul [:li.text-sm [:a {:href "kanban.html"} "Kanban"]]]
   [:ul [:li.text-sm [:a {:href "slate.html"} "Slate"]]]])

(defn ui []
  (let [page @(rf/subscribe [::sub/page])]
    [:div
     (case page
       ::nav/kanban [:div [:h1.text-lg "Kanban"] [menu]]
       ::nav/slate
       [:<>
        [slate/card]
        [slate/new]
        [menu]]

       [:div [:h1 "Page not ready"]])]))
