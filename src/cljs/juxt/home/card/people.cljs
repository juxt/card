;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.people
  (:require
   ["/juxt/card/people" :refer (People)]
   [juxt.home.card.subscriptions :as sub]
   [re-frame.core :as rf])
  )

(defn people []
  (let [profile @(rf/subscribe [::sub/current-user-profile])
        directory @(rf/subscribe [::sub/user-directory])]
    (when (and (:id profile) (map? directory))
      [:> People {:profile profile
                  :directory directory}])))
