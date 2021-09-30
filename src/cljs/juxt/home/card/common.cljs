(ns juxt.home.card.common
  (:require
   [helix.dom :as d]
   ;;TODO figure out whats wrong with my defnc macro...
   [helix.core :refer [defnc]]))

(defnc hook-info
  [{:keys [hook]}]
  (d/div
   (when (:isLoading hook)
     (d/p "Loading..."))
   (when (:isError hook)
     (js/console.log (:error hook))
     (d/p "Error fetching"))))
