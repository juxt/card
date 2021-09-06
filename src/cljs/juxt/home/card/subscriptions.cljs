;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.subscriptions
  (:require
   [juxt.home.card.config :as config]
   [potpuri.core :as p]
   [re-frame.core :as rf]
   [clojure.string :as str]))

(rf/reg-sub
 ::current-route
 (fn [db _]
   (:current-route db)))

(rf/reg-sub
 ::page
 :<- [::current-route]
 (fn [current-route _]
   (get-in current-route [:data :name])))

(defn unfold-children [node index]
  (cond-> node
    (:juxt.card.alpha/children node)
    (update
     :juxt.card.alpha/children
     (fn [children]
       (mapv (fn [child-or-ref]
               (cond
                 (string? child-or-ref) ; if true, it's a ref
                 (if-let [subnode (get index child-or-ref)]
                   (unfold-children subnode index)
                   child-or-ref)
                 :else child-or-ref))
             children)))))

(defn resolve-content-references [node index]
  (cond-> node
    (:juxt.card.alpha/content node)
    (update
     :juxt.card.alpha/content
     (fn [content]
       (mapv (fn [segment-or-ref]
               (cond
                 (string? segment-or-ref) ; if true, it's a ref
                 (if-let [subnode (get index segment-or-ref)]
                   subnode
                   (do
                     (println "Failed to lookup" segment-or-ref)
                     segment-or-ref
                     ))
                 :else segment-or-ref))
             content)))))

(rf/reg-sub
 ::card
 (fn [db [_ id]]
   (let [card (get-in db [:doc-store id])]
     (resolve-content-references card (:doc-store db)))))

(rf/reg-sub
 ::card-props
 (fn [db [_ id]]
   (let [card (get-in db [:doc-store id])
         card (resolve-content-references card (:doc-store db))]
     {"title" (:juxt.card.alpha/title card)
      "subtitle" (:juxt.card.alpha/subtitle card)
      "id" (:crux.db/id card)
      })))


(rf/reg-sub
 ::doc
 (fn [db [_ id]]
   (get-in db [:doc-store id])))

(rf/reg-sub
 ::current-card
 (fn [db _]
   (:current-card db)))

(rf/reg-sub
 ::cards
 (fn [db _]
   (:cards db)))

(rf/reg-sub
 ::actions
 (fn [db _]
   (:actions db)))

(defn process-db-user
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

(defn format-holiday
  [{:keys [start-date end-date start end crux.db/id all-day? description] :as holiday}]
  {:id id
   :start (or start start-date)
   :end (or end end-date)
   :allDay (not (false? all-day?))
   :title description})

(defn- assoc-holidays
  [holidays people]
  (for [{:keys [user-id] :as user} people
        :let [user-holidays (get holidays user-id)]]
    (assoc user :holidays (map format-holiday user-holidays))))

(rf/reg-sub
 ::route-params
 (fn [db _]
   (-> db :current-route :parameters)))

(rf/reg-sub
 ::holidays
 (fn [db]
   (:holidays db)))

(rf/reg-sub
 ::my-holidays
 :<- [::user-info]
 :<- [::holidays]
 (fn [[{id :id} holidays]]
   (get holidays id)))

(rf/reg-sub
 ::my-timesheets
 (fn [db]
   (:my-timesheets db)))

(rf/reg-sub
 ::my-events
 :<- [::my-holidays]
 :<- [::my-timesheets]
 (fn [[hols timesheets]]
   (into hols timesheets)))

(rf/reg-sub
 ::raw-people
 (fn [db] (:people db)))

(rf/reg-sub
 ::people
 :<- [::raw-people]
 :<- [::holidays]
 (fn [[people holidays]]
   (->> people
        (map process-db-user)
        (assoc-holidays holidays)
        (sort-by :name))))

(rf/reg-sub
 ::user-info
 (fn [db]
   (:user-info db)))

(rf/reg-sub
 ::current-user-profile
 :<- [::people]
 :<- [::user-info]
 :<- [::route-params]
 (fn [[people {logged-in-user :username} route-params] _]
   (let [current-user-id
         (or
          (-> route-params :query :selected)
          logged-in-user)]
     (p/find-first people {:id current-user-id}))))

(rf/reg-sub
 ::logged-in-user-profile
 :<- [::people]
 :<- [::user-info]
 (fn [[people {logged-in-user :username}] _]
   (p/find-first people {:id logged-in-user})))

(defn- last-name
  [name]
  (last (str/split name " ")))

(defn- last-initial
  "get first letter of last word in name"
  [{:keys [name]}]
  (let [last-initial (first (last-name name))]
    last-initial))

(rf/reg-sub
 ::user-directory
 :<- [::people]
 (fn [people _]
   (->> people
        (map #(assoc % :last-name (:name %)))
        (sort-by (comp last-name :name))
        (group-by last-initial))))
