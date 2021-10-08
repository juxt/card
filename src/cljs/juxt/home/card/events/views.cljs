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
  (let [{:keys [data isLoading isPlaceholderData] :as events} (hooks/use-my-holidays)
        delete-mutation (hooks/use-delete-event)
        update-mutation (hooks/use-update-event)]
    (d/section
     {:class "page-section"}
     ($ common/hook-info {:hook events})
     ($ EventCalendar {:isCurrentUser true
                       :isLoading (or isLoading isPlaceholderData)
                       :onDeleteEvent #(.mutate delete-mutation %)
                       :onUpdateEvent #(.mutate update-mutation %)
                       :events (->js data)}))))
