(ns juxt.home.card.people.views
  (:require [helix.core :as helix :refer [$]]
            [juxt.lib.helix :refer [defnc]]
            [juxt.home.card.people.hooks :as hooks]
            [juxt.home.card.common :as common]
            [cljs-bean.core :refer [->js]]
            ["/juxt/card/stories/People" :refer [People]]
            [helix.dom :as d]
            [juxt.home.card.query-hooks :as query-hooks]))

(defnc view []
  (let [{:keys [data] :as people} (hooks/use-people)
        default-username (:data (query-hooks/use-self
                                 {:query-opts
                                  {:select #(:username %)}}))
        {:keys [selected]} (common/use-query-params)
        ;; todo use selected to get profile from people
        profile (->js (or (first (first (vals data))) {}))]
    (def default-username default-username)
    (def selected selected)
    (def directory directory)
    (d/section
     ($ common/hook-info {:hook people})
     ($ People {:profile profile
                :directory (->js data)
                :user profile ;; todo - this should be the logged in user
                :onUpdateEvent #()
                :onDeleteEvent #()}))))
