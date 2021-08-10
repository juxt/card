;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.subscriptions
  (:require
   [juxt.home.card.config :as config]
   [re-frame.core :as rf]))

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

(rf/reg-sub
 ::current-user-profile
 (fn [db _]
   (let [{:keys [name email]} (-> db :people first :user)
         {:juxt.home/keys [slack-profile-pic]} (-> db :people first :slack)]
     (def db db)
     {
      :name name
      :imageUrl
      slack-profile-pic
      :coverImageUrl
      "https://images.unsplash.com/photo-1444628838545-ac4016a5418a?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80",
      :about
      "<p>Tincidunt quam neque in cursus viverra orci, dapibus nec tristique. Nullam ut sit dolor consectetur urna, dui cras nec sed. Cursus risus congue arcu aenean posuere aliquam.</p>
   <p>Et vivamus lorem pulvinar nascetur non. Pulvinar a sed platea rhoncus ac mauris amet. Urna, sem pretium sit pretium urna, senectus vitae. Scelerisque fermentum, cursus felis dui suspendisse velit pharetra. Augue et duis cursus maecenas eget quam lectus. Accumsan vitae nascetur pharetra rhoncus praesent dictum risus suspendisse.</p>"
      :fields {
               :Phone "(555) 123-4567",
               :Email email
               :Title "Senior Front-End Developer",
               :Team "Product Development",
               :Location "San Francisco",
               :Sits "Oasis, 4th floor",
               :Salary "$145,000",
               :Birthday "June 8, 1990",
               }
      })
   ))
