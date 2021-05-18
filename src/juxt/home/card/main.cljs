;; Copyright © 2021, JUXT LTD.

(ns ^:figwheel-hooks juxt.home.card.main
  (:require
   [reagent.dom]
   [re-frame.core :as rf]
   [juxt.home.card.navigation :as nav]
   [juxt.home.card.views :as views]
   juxt.home.card.events))

(defn mount-root
  []
  (reagent.dom/render
   [views/ui]
   (js/document.getElementById "app")))

(defn ^:export init
  []
  (rf/dispatch [:initialize])
  (nav/init-routes!)
  (mount-root))

(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (println "After load")
  (rf/clear-subscription-cache!)
  (nav/init-routes!)
  (mount-root))

(defonce run (init))
