(ns juxt.home.card.calendar
  (:require [helix.core :as helix :refer [$]]
            [juxt.lib.helix :refer [defnc]]
            [juxt.home.card.query-hooks :as query-hooks]
            [juxt.home.card.common :as common]
            ["/juxt/card/stories/Calendar" :refer [EventCalendar]]
            [helix.dom :as d]))

(defnc view
  []
  (let [self (query-hooks/use-self)]
    (d/section
     {:class "page-section"}
     (common/render-errors self)
     ($ EventCalendar {:isCurrentUser true
                       :events []
                       :projectOptions []}))))
