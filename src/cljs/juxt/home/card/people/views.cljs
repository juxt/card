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
  (let [{:keys [data] :as people} (hooks/use-people)
        directory (group-by (fn last-initial [{full-name :name}]
                              (first (last (str/split full-name " "))))
                            (vals data))
        self (:data (query-hooks/use-self
                     {:query-opts
                      {:select #(:username %)}}))
        {:keys [selected]} (common/use-query-params)]
    (d/section
     ($ common/hook-info {:hook people})
     ($ People {:profile (->js (get data (or selected self) {}))
                :directory (->js directory)
                :user (->js (get data self {}))
                :onUpdateEvent #()
                :onDeleteEvent #()}))))
