(ns juxt.home.card.events.views
  (:require [helix.core :as helix :refer [$]]
            [juxt.lib.helix :refer [defnc]]
            [juxt.home.card.query-hooks :as query-hooks]
            [juxt.home.card.common :as common]
            [juxt.home.card.events.hooks :as hooks]
            [cljs-bean.core :refer [->js]]
            ["/juxt/card/stories/Calendar" :refer [EventCalendar]]
            [helix.dom :as d]))

(defnc calendar
  [props]
  (let [delete-mutation (hooks/use-delete-event)
        update-mutation (hooks/use-update-event)]
    ($ EventCalendar
       {:isCurrentUser true
        :onDeleteEvent #(.mutate delete-mutation %)
        :onUpdateEvent #(.mutate update-mutation %)
        & props})))

(defnc timesheets
  []
  (let [{:keys [data isLoading isPlaceholderData] :as events}
        (hooks/use-my-timesheets)]
    (d/section
     {:class "page-section"}
     ($ common/hook-info {:hook events})
     ($ calendar
        {:isLoading (or isLoading isPlaceholderData)
         :events (->js data)
         :eventType "Timesheet"}))))

(defnc holidays
  []
  (let [{:keys [data isLoading isPlaceholderData] :as events}
        (hooks/use-my-holidays)]
    (d/section
     {:class "page-section"}
     ($ common/hook-info {:hook events})
     ($ calendar
        {:isLoading (or isLoading isPlaceholderData)
         :events (->js data)
         :eventType "Holiday"}))))
