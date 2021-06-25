;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.views
  (:require
   [juxt.home.card.navigation :as nav]
   [juxt.home.card.config :as config]
   [juxt.home.card.subscriptions :as sub]
   [juxt.home.card.slate :as slate]
   [re-frame.core :as rf]
   [tailwind-hiccup.core :refer [tw]]))

(def table-style [:text-sm])
(def thead-style [:border-2])
(def tbody-style [:border-2])
(def tr-style [:border-2 :p-2])
(def td-style [:border-2 :p-2])
(def th-style td-style)
(def sidebar-style [:bg-white])

(defn link [href text]
  [:a
   {:href href
    :class "text-yellow-800"}
   text])

(defn definition-list [data]
  [:div
   [:dl
    (tw ["sm:divide-y" "sm:divide-gray-200"])
    (for [{:keys [label value]} data]
      [:div
       (tw [:py-4 "sm:py-5" "sm:grid" "sm:grid-cols-3" "sm:gap-4" "sm:px-6"])
       [:dt (tw [:text-sm :font-medium :text-gray-500]) label]
       [:dd
        (tw [:mt-1 :text-sm "sm:mt-0" "sm:col-span-2"])
        value]])]])

(defn definition-list-panel [title description data]
  [:div
   (tw [:bg-white :overflow-hidden])
   [:div
    (tw [:px-4 :py-5 "sm:px-6"])
    [:h3
     (tw [:text-lg :leading-6 :font-medium])
     title]
    [:p
     (tw [:mt-1 :max-w-2xl :text-sm])
     description]]
   (definition-list data)])

(defn user-details []
  (let [sub (rf/subscribe [::sub/user-details])]
    (fn []
      (let [user-details @sub]
        (definition-list
          [{:label "Username" :value (:username user-details)}
           {:label "Name" :value (:name user-details)}
           {:label "Email" :value (:email user-details)}
           {:label "Start date" :value "TBD"}
           {:label "GitHub id" :value ""}
           {:label "Keybase id" :value ""}])))))

(defn panel [{:keys [title description]} & content]
  [:div (tw ["md:grid" "md:grid-cols-3" "md:gap-6"])

   [:div (tw ["md:col-span-1"])
    [:div (tw [:px-4 "sm:px-0"])
     [:h3 (tw [:text-lg :font-medium :leading-6 :text-gray-900]) title]
     [:p (tw [:mt-1 :text-sm :text-gray-600]) description]]]

   [:div (tw [:mt-5 "md:mt-0" "md:col-span-2"])
    [:div (tw ["sm:overflow-hidden"])
     [:div (tw [:px-4 :py-5 :bg-white :space-y-6 "sm:p-6"])
      content]]

    ]])

(defn personal []
  [:<>
   [:div.p-4
    [:h2 "Personal Details"]]
   [:p.p-4
    [:em "Here is where we will ask for next-of-kin, emergency contact, GP,
    dietary, plus consent for given purposes. For example, for the purpose of
    sending you gifts of food and drink."]
    [:em "For each purpose, we'll explain who will have access to this
    data."]]])

(defn spacer []
  [:div
   (tw [:hidden "sm:block"])
   [:div (tw [:py-5]) [:div (tw [:border-t :border-gray-200])]]])

(defn button [label]
  [:div
   (tw ["px-4" "py-3" "text-right" "sm:px-6"])
   [:button
    (tw
     ["inline-flex"
      "justify-center"
      "py-2"
      "px-4"
      "border"
      "border-transparent"
      "text-sm"
      "font-medium"
      "text-white"
      "bg-yellow-600"
      "hover:bg-yellow-700"
      "focus:outline-none"
      "focus:ring-2"
      "focus:ring-offset-2"
      "focus:ring-yellow-500"])
    label]])

(defn input [{:keys [label type] :or {type "input"}} ]
  [:div
   [:label (tw ["block" "text-sm" "font-medium" "text-gray-700"]) label]
   [:input
    (tw
     ["mt-1"
      "px-1"
      "focus:ring-yellow-500"
      "focus:border-yellow-500"
      "block"
      "w-full"
      "border-2"]
     {:type type})]])

(defn text [data]
  data)

(declare card-content)

(defmulti render-component :juxt.site.alpha/type)

(defmethod render-component :default [component]
  [:span.text-red-700.font-bold (str "error on type '" (:juxt.site.alpha/type component) "' on " [:pre (pr-str component)])])

(defmethod render-component nil [_]
  [:span.text-red-700.font-bold (str "some nil component...")])

(defmethod render-component "User" [user]
  [:a.text-yellow-700.font-bold
   {:href (:crux.db/id user)}
   (str "@" (:juxt.pass.alpha/username user))])

(defmethod render-component "DocumentSection" [section]
  [:div
   [:h1 (tw ["text-xl" "font-medium"]) (:title section)]
   [:h2 (:subtitle section)]
   (card-content section)])

(defmethod render-component "Paragraph" [section]
  [:div
   (card-content section)])

(defmethod render-component "Task" [task]
  [:div
   [:input (tw ["mx-2"] {:type "checkbox" :checked (= (:status task) "DONE")})]
   [:span (tw ["px-4"]) [:a (tw ["text-indigo-700"]
                                {:href (:crux.db/id task)}) (:title task)]]
   [:span.text-sm "(pri:" (:priority task) ", deadline: " (:deadline task) ")"]
   #_(card-content task)])

(defmethod render-component "Checklist" [task]
  [:div (tw "border-2")
   (card-content task)])

(defn card-content [card]
  [:div (tw ["py-2"])
   #_[:p (tw ["pb-4" "text-sm" "text-gray-400"])
    (:juxt.site.alpha/type card)]

   #_[:p (tw ["text-sm" "text-gray-400"]) "Priority: " (:priority card)]
   #_[:p (tw ["text-sm" "text-gray-400"]) "Status: " (:status card)]
   #_[:p (tw ["text-sm" "text-gray-400"]) "Classification: " (:classification card)]
   #_[:hr (tw ["pb-4"])]

   (for [segment (:content card)]
     (cond
       (vector? segment)
       (let [[typ data] segment]
         (case typ
           "text" [text data]
           "em" [:span.italic [text data]]
           [text data]))
       (map? segment)
       (render-component segment)

       (string? segment)
       [:span segment]

       :else [:span.text-red-700 "(unsupported segment type)"]))])

(defn menu []
  [:div
   [:ul [:li.text-sm [:a {:href "index.html"} "Index"]]]
   [:ul [:li.text-sm [:a {:href "kanban.html"} "Kanban"]]]
   [:ul [:li.text-sm [:a {:href "slate.html"} "Slate"]]]
   ])

(defn index []
  (let [root (rf/subscribe
              [::sub/card
               (str config/site-api-origin
                    "/card/cards/section-containing-checklist-1"
                    #_"/card/cards/task-1")])]

    [:div
     [:div (tw ["bg-gray-100" "p-6" "appearance-none"])
      (render-component @root)]
     [menu]
     ]))

(defn ui []
  (let [page @(rf/subscribe [::sub/page])]
    [:div
     (case page
       ::nav/index [index]
       ::nav/kanban [:div [:h1.text-lg "Kanban"] [menu]]
       ::nav/slate
       [:<>
        [slate/card]
        [slate/new]]

       [:div [:h1 "Page not ready"]])]))
