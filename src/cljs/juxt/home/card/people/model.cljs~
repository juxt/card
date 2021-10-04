(ns juxt.home.card.people
  (:require [helix.core :as helix :refer [$]]
            [juxt.lib.helix :refer [defnc]]
            [juxt.home.card.query-hooks :as query-hooks]
            [juxt.home.card.common :as common]
            [cljs-bean.core :refer [->js]]
            ["/juxt/card/stories/People" :refer [People]]
            [helix.dom :as d]))

(def profile
  (->js {:name "Mike Bruce",
         :id "mic",
         :imageUrl "https://home.juxt.site/_site/users/mic/slack/mic.png",
         :coverImageUrl
         "https://images.unsplash.com/photo-1444628838545-ac4016a5418a?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80",
         :about
         "<p>Tincidunt quam neque in cursus viverra orci, dapibus nec tristique. Nullam ut sit dolor consectetur urna, dui cras nec sed. Cursus risus congue arcu aenean posuere aliquam.</p>\n   <p>Et vivamus lorem pulvinar nascetur non. Pulvinar a sed platea rhoncus ac mauris amet. Urna, sem pretium sit pretium urna, senectus vitae. Scelerisque fermentum, cursus felis dui suspendisse velit pharetra. Augue et duis cursus maecenas eget quam lectus. Accumsan vitae nascetur pharetra rhoncus praesent dictum risus suspendisse.</p>",
         :fields {:Phone "(555) 123-4567",
                  :Email "mic@juxt.pro",
                  :Title "Senior Front-End Developer",
                  :Team "Product Development",
                  :Location "San Francisco",
                  :Sits "Oasis, 4th floor",
                  :Salary "$145,000",
                  :Birthday "June 8, 1990",}}))

(defnc view []
  (let [events (query-hooks/use-my-holidays)]
    (d/section
     {:class "page-section"}
     ($ common/hook-info {:hook events})
     ($ People {:profile profile
                :directory (->js [])
                :user profile
                :onUpdateEvent #()
                :onDeleteEvent #()}))))
