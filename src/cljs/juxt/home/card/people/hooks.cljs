(ns juxt.home.card.people.hooks
  (:require [cljs-bean.core :refer [->clj ->js]]
            [potpuri.core :refer [find-first]]
            [react-query :refer [useQueryClient]]
            [juxt.home.card.query-hooks :as query-hooks]
            [juxt.home.card.config :as config]
            [clojure.string :as str]))

(defn process-user
  [{:keys [user slack]}]
  (let [{:keys [name email juxt.pass.alpha/username]} user
        {:juxt.home/keys [slack-profile-pic]
         :juxt.pass.alpha/keys [user]} slack]
    {:name name
     :user-id user
     :id username
     :email email
     :imageUrl
     slack-profile-pic
     :coverImageUrl
     "https://images.unsplash.com/photo-1444628838545-ac4016a5418a?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80",
     :about
     "<p>Tincidunt quam neque in cursus viverra orci, dapibus nec tristique. Nullam ut sit dolor consectetur urna, dui cras nec sed. Cursus risus congue arcu aenean posuere aliquam.</p>
   <p>Et vivamus lorem pulvinar nascetur non. Pulvinar a sed platea rhoncus ac mauris amet. Urna, sem pretium sit pretium urna, senectus vitae. Scelerisque fermentum, cursus felis dui suspendisse velit pharetra. Augue et duis cursus maecenas eget quam lectus. Accumsan vitae nascetur pharetra rhoncus praesent dictum risus suspendisse.</p>"
     :fields {:Phone "(555) 123-4567",
              :Email email
              :Title "Senior Front-End Developer",
              :Team "Product Development",
              :Location "San Francisco",
              :Sits "Oasis, 4th floor",
              :Salary "$145,000",
              :Birthday "June 8, 1990"}}))

(def people-url (str config/site-api-origin "/card/users/"))

(defn use-people
  ([] (use-people {}))
  ([{:keys [user-id] :as opts}]
   (let [self (query-hooks/use-self)
         received-people
         (fn process-people [people]
           (process-user (first (->clj people))))]
     (query-hooks/use-query
      (conj ["people"] user-id)
      #(-> (query-hooks/fetch (str people-url user-id)
                              {:query-opts
                               {:placeholderData self}})
           (.then received-people))
      opts))))

(defn use-directory
  ([] (use-directory {}))
  ([opts]
   (let [client (useQueryClient)
         people->directory
         (fn [people]
           (let [people (->clj people)]
             (group-by
              (fn last-initial [{full-name :name}]
                (first (last (str/split full-name " "))))
              (map process-user people))))
         populate-cache
         (fn [people]
           ;; because we are loading every person here, we can give some data
           ;; to ["people" {username}] without doing additional requests.
           (doseq [person (->clj people)]
             (.setQueryData
              client
              #js ["people" (:username person)]
              (process-user person)))
           people)
         directory
         (query-hooks/use-query
          ["people"]
          #(-> (query-hooks/fetch people-url)
               (.then populate-cache)
               (.then people->directory))
          opts)]
     directory)))
