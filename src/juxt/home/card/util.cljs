;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.util
  (:require
   [re-frame.core :as rf]
   [tailwind-hiccup.core :refer [tw]]))

(defn href [content target & [params]]
  [:a (tw ["underline" "text-yellow-600" "hover:text-yellow-800" "cursor-pointer"]
          {:href "/cards/" ; TODO: How to derive this? (useful for status)
           :on-click
           (fn [_]
             (rf/dispatch [:navigate target (or params {})]))})
   content])
