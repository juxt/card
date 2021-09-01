;; Copyright © 2021, JUXT LTD.

 (ns juxt.home.card.calendar
  (:require
   ["/juxt/card/stories/Calendar" :refer [EventCalendar]]
   [juxt.home.card.subscriptions :as sub]
   [re-frame.core :as rf]))

 (defn view []
  (let [events @(rf/subscribe [::sub/my-events])]
    [:> EventCalendar
     {:events events
      :isCurrentUser true
      :onUpdateEvent #(rf/dispatch [:update-event %])
      :onDeleteEvent #(rf/dispatch [:delete-entity
                                    % [[:dispatch [:get-holidays]]]])}]))
