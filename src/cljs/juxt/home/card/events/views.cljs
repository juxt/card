(ns juxt.home.card.events.views
  (:require [helix.core :as helix :refer [$]]
            [juxt.lib.helix :refer [defnc]]
            [juxt.home.card.query-hooks :as query-hooks]
            [juxt.home.card.common :as common]
            [juxt.home.card.events.hooks :as hooks]
            [cljs-bean.core :refer [->js]]
            ["/juxt/card/stories/Calendar" :refer [EventCalendar]]
            [helix.dom :as d]))

(defnc view
  []
  (let [{:keys [data] :as events} (hooks/use-my-holidays)
        delete-mutation (hooks/use-delete-event)
        update-mutation (hooks/use-update-event)]
    (d/section
     {:class "page-section"}
     ($ common/hook-info {:hook events})
     ($ EventCalendar {:isCurrentUser true
                       :onDeleteEvent #(do
                                         (prn "del" %)
                                         (.mutate delete-mutation %))
                       :onUpdateEvent #(do
                                         (prn "up" %)
                                         (.mutate update-mutation %))
                       :events (->js data)}))))
