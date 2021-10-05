(ns juxt.home.card.people.views
  (:require [helix.core :as helix :refer [$]]
            [juxt.lib.helix :refer [defnc]]
            [juxt.home.card.people.hooks :as hooks]
            [juxt.home.card.common :as common]
            [cljs-bean.core :refer [->js]]
            ["/juxt/card/stories/People" :refer [People]]
            [helix.dom :as d]
            [clojure.string :as str]
            [juxt.home.card.query-hooks :as query-hooks]))

(defnc view []
  (let [self (:data (query-hooks/use-self {:query-opts {:select #(:username %)}}))
        {:keys [selected]} (common/use-query-params)
        directory (hooks/use-directory)
        profile (hooks/use-people {:user-id (or selected self)})]
    (d/section
     ($ People {:profile (->js (:data profile))
                :directory (->js (:data directory))
                :isDirectoryLoading (:isLoading directory)
                :isProfileLoading (:isLoading profile)
                :user self
                :onUpdateEvent #()
                :onDeleteEvent #()}))))
