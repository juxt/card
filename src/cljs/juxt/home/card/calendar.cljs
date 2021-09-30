(ns juxt.home.card.calendar
  (:require [helix.core :as helix :refer [$]]
            [juxt.lib.helix :refer [defnc]]
            [juxt.home.card.query-hooks :as query-hooks]
            [juxt.home.card.common :as common]
            [cljs-bean.core :refer [->js]]
            ["/juxt/card/stories/Calendar" :refer [EventCalendar]]
            [helix.dom :as d]))

(defnc view
  []
  (let [events (query-hooks/use-my-holidays)]
    (d/section
     {:class "page-section"}
     ($ common/hook-info {:hook events})
     ($ EventCalendar {:isCurrentUser true
                       :events (->js (:data events))}))))
