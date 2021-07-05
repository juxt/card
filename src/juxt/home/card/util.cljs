;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.util
  (:require
   [re-frame.core :as rf]
   [tailwind-hiccup.core :refer [tw]]
   [reitit.frontend.easy :as rfe]))

(defn route->url
  "k: page handler i.e. :entity
  params: path-params map
  query: query-params map"
  ([k]
   (route->url k nil nil))
  ([k params]
   (route->url k params nil))
  ([k params query]
   (rfe/href k params query)))

(defn href [content target & [params query]]
  [:a (tw ["underline" "text-yellow-600" "hover:text-yellow-800" "cursor-pointer"]
          {:href (route->url target params query)})
   content])
