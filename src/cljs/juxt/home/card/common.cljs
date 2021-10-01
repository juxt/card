(ns juxt.home.card.common
  (:require
   [helix.dom :as d]
   [juxt.lib.helix :refer [defnc]]))

(defnc hook-info
  [{:keys [hook]}]
  (d/div
   (when (:isLoading hook)
     (d/p "Loading..."))
   (when (:isError hook)
     (js/console.log (:error hook))
     (d/p "Error fetching"))))
