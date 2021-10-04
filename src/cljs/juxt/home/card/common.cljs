(ns juxt.home.card.common
  (:require
   [helix.dom :as d]
   [react-router-dom :refer  [useLocation]]
   [cljs-bean.core :refer [->clj]]
   [juxt.lib.helix :refer [defnc]]))

(defn use-query-params
  []
  (->clj
   (js/Object.fromEntries
    (js/URLSearchParams. (.-search (useLocation))))))

(defnc hook-info
  [{:keys [hook]}]
  (d/div
   (when (:isLoading hook)
     (d/p "Loading..."))
   (when (:isError hook)
     (js/console.log (:error hook))
     (d/p "Error fetching"))))
