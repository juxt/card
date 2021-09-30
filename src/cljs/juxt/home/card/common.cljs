(ns juxt.home.card.common
  (:require
   [helix.dom :as d]
   [juxt.lib.helix :refer [defnc]]))

(defnc render-errors
  [data]
  (when (:isError data)
    (js/console.log (:error data))
    (d/div "Error fetching")))
