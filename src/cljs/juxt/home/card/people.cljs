;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.people
  (:require
   ["/juxt/card/People" :refer [People]]
   [juxt.home.card.subscriptions :as sub]
   [re-frame.core :as rf]))

(defn people []
  (let [profile @(rf/subscribe [::sub/current-user-profile])
        directory @(rf/subscribe [::sub/user-directory])
        user @(rf/subscribe [::sub/logged-in-user-profile])]
    (when (and (:id profile) (map? directory) (map? user))
      [:> People {:profile profile
                  :directory directory
                  :user user}])))
