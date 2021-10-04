(ns juxt.home.card.people
  (:require [helix.core :as helix :refer [$]]
            [juxt.lib.helix :refer [defnc]]
            [juxt.home.card.query-hooks :as query-hooks]
            [juxt.home.card.common :as common]
            [cljs-bean.core :refer [->js]]
            ["/juxt/card/stories/People" :refer [People]]
            [helix.dom :as d]))

(defnc view []
  (let [people (query-hooks/use-people)
        {:keys [selected]} (common/use-query-params)
        directory (:data people)
        profile (->js (or (first (first (vals directory))) {})) ;; todo - this should be a hook?
        ]
    (def selected selected)
    (def directory directory)
    (d/section
     ($ common/hook-info {:hook directory})
     ($ People {:profile profile
                :directory (->js directory)
                :user profile ;; todo - this should be the logged in user
                :onUpdateEvent #()
                :onDeleteEvent #()}))))
