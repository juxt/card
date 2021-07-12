;; Copyright © 2021, JUXT LTD.

;; TODO: Rename this namespace!

(ns juxt.home.card.slate
  (:require
   [cljs.pprint :as pprint]
   [clojure.string :as str]
   [juxt.home.card.config :as config]
   [juxt.home.card.subscriptions :as sub]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [tailwind-hiccup.core :refer [tw]]
   [goog.object :as gobj]
   ["react" :as react :refer [createElement useCallback useEffect useMemo useState]]
   ["slate" :as slate :refer [createEditor Editor Transforms Node]]
   ["slate-react" :refer [Editable Slate withReact ReactEditor]]
   [clojure.string :as str]
   [juxt.home.card.navigation :as nav]
   [juxt.home.card.util :as u]
   [juxt.home.card.kanban :as kanban]))

(defn button [label on-click]
  [:button (tw ["inline-flex" "items-center" "px-2.5" "my-2" "border" "border-transparent" "text-xs" "font-medium" "rounded" "shadow-sm" "text-white" "bg-yellow-600" "hover:bg-yellow-700" "focus:outline-none" "focus:ring-2" "focus:ring-offset-2" "focus:ring-yellow-500"]
               {:type "button"
                :onClick on-click}) label])

(defn red-button [label on-click]
  [:button (tw ["inline-flex" "items-center" "px-2.5" "my-2" "border" "border-transparent" "text-xs" "font-medium" "rounded" "shadow-sm" "text-white" "bg-red-300" "hover:bg-red-900" "focus:outline-none" "focus:ring-2" "focus:ring-offset-2" "focus:ring-red-700"]
               {:type "button"
                :onClick on-click}) label])

(defn CodeElement
  [props]
  (aset (.-attributes props) "className" "bg-yellow-100 p-2")
  (createElement "pre" (.-attributes props)
    (createElement "code" #js{}
                   (.-children props))))

(defn DefaultElement
  [props]
  (aset (.-attributes props) "className" "py-2")
  (createElement "div" (.-attributes props)
                 (.-children props)))

(defn HeadingElement
  [props]
  (aset (.-attributes props) "className" "my-4 text-xl")
  (createElement "h1" (.-attributes props)
                 (.-children props)))

(defn SubheadingElement
  [props]
  (aset (.-attributes props) "className" "m-4 text-lg")
  (createElement "h2" (.-attributes props)
                 (.-children props)))

(defn Leaf
  [props]
  (let [leaf (gobj/get props "leaf")
        style (gobj/get leaf "style")]
    (createElement "span"
      (doto (gobj/clone (.-attributes props))
        (gobj/set "style" style))
      (.-children props))))

(defn Paragraph
  [props]
  (let [editor (useMemo #(withReact (createEditor)) #js [])
        [value setValue] (useState (.-content props))
        save (.-save props)
        container-id (.-containerId props)
        id (.-id props)
        index (.-index props)
        [timeout storeTimeout] (useState nil)

        renderElement
        (useCallback
         (fn [props]
           (cond
             (= (.-type (.-element props)) "code")
             (createElement CodeElement props)
             :else
             (createElement DefaultElement props))))

        renderLeaf
        (useCallback
         (fn renderLeaf [props]
           (createElement Leaf props))
         #js [])]

    (aset
     js/window "focusmap"
     (assoc (or (aget js/window "focusmap") {})
            id
            (fn []
              (.focus ReactEditor editor)
              ;; setTimeout is a workaround https://github.com/ianstormtaylor/slate/issues/3813
              (js/setTimeout
               #(.move Transforms
                       editor
                       #js {:distance 999999999999}) 0))))
    (createElement
     Slate
     #js {:editor editor
          :value value
          :onChange (fn [val]
                      (setValue val)
                      (when timeout (js/clearTimeout timeout))
                      (storeTimeout
                       (js/setTimeout
                        (fn [] (when save (save val)))
                        1000)))}

     (createElement
      Editable
      #js {:renderElement renderElement
           :renderLeaf renderLeaf
           :onKeyDown
           (fn [ev]
             (cond
               (= (.-key ev) "Enter")
               (do
                 (.preventDefault ev)
                 (when-not (str/blank? (.. ev -target -textContent))
                   (rf/dispatch [:new-paragraph container-id (inc index)])))

               (= (.-key ev) "Backspace")
               (when (str/blank? (.. ev -target -textContent))
                 (rf/dispatch [:unlink-paragraph container-id index]))

               (and (= (.-key ev) "`") (.-ctrlKey ev))
               (do
                 (.preventDefault ev)
                 (let [[match] (es6-iterator-seq
                                (.nodes Editor editor #js {:match (fn [n] (= (.-type n) "code"))}))]
                   (.setNodes Transforms
                              editor
                              #js {:type (if match "paragraph" "code")}
                              #js {:match (fn [n] (.isBlock Editor editor n))})))))}))))

(defn Field
  [props]
  (let [editor (useMemo #(withReact (createEditor)) #js [])
        [value setValue] (useState (.-content props))
        save (.-save props)
        id (.-id props)
        eltype ^string (.-eltype props)
        [timeout storeTimeout] (useState nil)
        renderElement
        (useCallback
         (fn [props]
           (createElement
            "div" #js {"className" "m-1"}
            (createElement (case eltype
                             "h1" HeadingElement
                             "h2" SubheadingElement) props))))
        renderLeaf (useCallback
                    (fn renderLeaf [props]
                      (createElement Leaf props))
                    #js [])]
    (createElement
     Slate
     #js {:editor editor
          :value value
          :onChange (fn [val]
                      (setValue val)
                      (when timeout (js/clearTimeout timeout))
                      (storeTimeout
                       (js/setTimeout
                        (fn [] (when save (save val)))
                        1000)))}

     (createElement
      Editable
      #js {:renderElement renderElement
           :renderLeaf renderLeaf
           :onKeyDown
           (fn [ev]
             (when
                 (= (.-key ev) "Enter")
                 (.preventDefault ev)
                 ))}))))

(declare render-paragraph)

;; A segment is an individual component of a linear sequence making up a
;; paragraph.
(defn render-segment [container-id child]
  (cond
    (vector? child)                     ; it's a text segment, not an @ mention
    (let [[type content] child]
      (case type
        "text" {:text content
                :_type :text}
        "em" {:text content
              :_type :em
              :style #js {:fontStyle "italic"}}
        {:text (str "(unknown:<" type ">)")}))
    (map? child)
    (with-meta
      (render-paragraph container-id child)
      {:key (:crux.db/id child)})))

(defn render-paragraph [container-id component]
  (cond
    (= (:juxt.site.alpha/type component) "User")
    {:text (str "@" (:juxt.pass.alpha/username component))
     :_id (:crux.db/id component)}

    :else
    (let [id (:crux.db/id component)]
      [:div
       [:div {:className
              (str "right-0 mx-4 flex flex-row h-5 gap-x-4"
                   (if (contains? #{"TODO" "DONE"} (:juxt.card.alpha/status component))
                     "text-gray-700"
                     "text-gray-300"))}

        [:div {:className "flex items-center"}
         [:div
          [:input
           {:id (str "task-checkbox-" id)
            :type "checkbox"
            :checked (contains? #{"TODO" "DONE"} (:juxt.card.alpha/status component))
            :onChange (fn [ev]
                        (if (.-checked (.-target ev))
                          (rf/dispatch [:set-attribute id :juxt.card.alpha/status "TODO"])
                          (rf/dispatch [:delete-attribute id :juxt.card.alpha/status])))}]]

         [:div {:className "ml-1 text-sm"}
          [:label {:for (str "task-checkbox-" id)} "Action?"]]]

        (when (contains? #{"TODO" "DONE"} (:juxt.card.alpha/status component))
          [:div {:className "flex items-center gap-x-2"}
           [:div {:className "ml-1 text-sm"}
            [:label {:for (str "task-checkbox2-" id)} "Assignee"]]
           [:input
            {:className "border-2 w-10"
             :id (str "task-checkbox2-" id)
             :type "text"
             :onChange (fn [ev]
                         )}]])

        (when (contains? #{"TODO"} (:juxt.card.alpha/status component))
          [:div {:className "flex items-center gap-x-2"}
           [:div {:className "ml-1 text-sm"}
            [:label {:for (str "task-checkbox3-" id)} "Deadline"]]
           [:input
            {:className "border-2"
             :id (str "task-checkbox3-" id)
             :type "text"
             :onChange (fn [ev])}]])]

       [:div {:className "flex gap-x-3 py-0"}
        (when (contains? #{"TODO" "DONE"} (:juxt.card.alpha/status component))
          [:input {:type "checkbox"
                   :checked (= (:juxt.card.alpha/status component) "DONE")
                   :onChange (fn [ev]
                               (rf/dispatch [:set-attribute id :juxt.card.alpha/status
                                             (if (.-checked (.-target ev))
                                               "DONE" "TODO")]))}])
        [:div {:className "flex-grow my-2 hover:bg-white focus:bg-white"}
         [:> Paragraph
          {:containerId container-id
           :id id
           :index (:_ix component)
           :content
           ;; An entity of type 'Paragraph' maps to a block with a single
           ;; 'paragraph' child. We don't allow more than one Slate paragraph
           ;; in a Site paragraph.
           [{:type "paragraph"
             :children
             (doall
              (map-indexed
               (fn [ix segment]
                 ^{:key ix}
                 (assoc (render-segment container-id segment) :_ix ix))
               (:juxt.card.alpha/content component)))}]
           :save (fn [val]
                   (let [s (.string Node #js {:children val})]
                     (rf/dispatch [:save-paragraph (:crux.db/id component) val])))}]]]

       ])))

(defn field [id label eltype attr provider]
  (let [data @(rf/subscribe [::sub/card id])]
    (let [v (get data attr)]
      [:div {:className "flex"}
       [:span (tw ["m-2" "text-gray-300"]) label]
       [:div {:className "flex-grow hover:bg-white"}
        [:> Field
         {:id id
          :eltype eltype
          :content
          [{:type "paragraph"
            :children [{:text (or v "")}]}]
          :save (fn [val]
                  (let [s (.string Node #js {:children val})]
                    (rf/dispatch [:set-attribute id attr s])))}]]
       [button "Delete" (fn [ev] (rf/dispatch [:delete-attribute id attr]))]])))

(declare card)

(defmulti render-card (fn [card _ _] (:juxt.site.alpha/type card)))

(defmethod render-card :default [{id :crux.db/id :as data} parent-id ix]
  [:div (tw (cond-> ["m-4" "border-2" "border-dotted" "border-gray-200" "bg-gray-100"]
              (:optimistic data) (conj "border-green-200")
              (:error data) (conj "border-red-400")))

   [:div (tw ["px-4" "right-0" "flex" "flex-row"])
    [red-button "Delete" (fn [ev] (rf/dispatch [:delete-card id]))]
    [:p (tw ["mx-4" "py-2" "text-gray-300" "text-xs"]) "URL: " id]]

   (if (:juxt.card.alpha/title data)
     [:div (tw ["p-2"])
      (field id "title" "h1" :juxt.card.alpha/title HeadingElement)]
     (when (:juxt.card.alpha/children data)
       [:div (tw ["m-4" "flex" "flex-auto" "gap-x-2" "text-gray-500" "text-sm"])
        [button "Add title" (fn [ev] (rf/dispatch [:set-attribute id :juxt.card.alpha/title ""]))]]))

   (if (:juxt.card.alpha/subtitle data)
     [:div (tw ["p-2"])
      (field id "subtitle" "h2" :juxt.card.alpha/subtitle SubheadingElement)]
     (when (:juxt.card.alpha/children data)
       [:div (tw ["m-4" "flex" "flex-auto" "gap-x-2" "text-gray-500" "text-sm"])
        [button "Add subtitle" (fn [ev] (rf/dispatch [:set-attribute id :juxt.card.alpha/subtitle ""]))]]))

   (if (and (:juxt.card.alpha/children data) (:juxt.site.alpha/type data))
     [:div (tw ["p-2"])
      (field id "type" "h2" :juxt.site.alpha/type SubheadingElement)]
     (when (:juxt.card.alpha/children data)
       [:div (tw ["m-4" "flex" "flex-auto" "gap-x-2" "text-gray-500" "text-sm"])
        [button "Add type" (fn [ev] (rf/dispatch [:set-attribute id :juxt.site.alpha/type "Card"]))]]))

   (cond
     (:juxt.card.alpha/children data)
     (doall
      (map-indexed
       (fn [ix child-id]
         (card child-id id ix))
       (:juxt.card.alpha/children data)))

     ;; We're a leaf
     (:juxt.card.alpha/content data)
     (render-paragraph parent-id (assoc data :_ix ix)))])

(defmethod render-card "Kanban" [{id :crux.db/id
                                  children :juxt.card.alpha/children
                                  title :juxt.card.alpha/title
                                  :as card} parent-id ix]
  [:<>
   [:h1 title]
   (kanban/kanban
    (into {}
          (map-indexed
           (fn [ix id]
             (let [col @(rf/subscribe [::sub/card id])]
               [(str ix ":"  (:juxt.card.alpha/title col)) [{:crux.db/id "foo" :juxt.card.alpha/content [["text" "Hello World!"]]}]]))
           children)
))
   [:div (tw ["p-4"])
    [button "New Column" (fn [_] (rf/dispatch [:new-paragraph id (count children)]))]]])

(defn card [id parent-id ix]
  (let [data @(rf/subscribe [::sub/card id])]
    ^{:key id} (render-card data parent-id ix)))

(defn pprint-str
  [x]
  (with-out-str (pprint/pprint x)))

(defn pprint-code
  [x]
     [:code
      {:style {:text-align "left"}}
      [:pre (pprint-str x)]])

(defn cards []
  (let [cards @(rf/subscribe [::sub/cards])]
    [:div (tw ["flex" "flex-col"])

     [:div (tw ["-my-2" "overflow-x-auto" "sm:-mx-6" "lg:-mx-8"])

      [:div (tw ["py-2" "align-middle" "inline-block" "min-w-full" "sm:px-6" "lg:px-8"])

       [:div (tw ["shadow" "overflow-hidden" "border-b" "border-gray-200" "sm:rounded-lg"])

        [:div (tw ["min-w-full" "divide-y" "divide-gray-200"])

         [:table
          [:thead (tw ["bg-gray-50"])

           [:tr
            (for [col ["Title" "Status" "Content"]]
              ^{:key col}
              [:th (tw ["px-6" "py-3" "text-left" "text-xs" "font-medium" "text-gray-500" "uppercase" "tracking-wider"]
                       {:scope "col"}) col])]]

          [:tbody (tw ["bg-white" "divide-y" "divide-gray-200"])
           (for [{:keys [card]} cards
                 :let [{:juxt.card.alpha/keys [children title status]
                        :crux.db/keys [id]} card]]
             ^{:key id}
             [:tr
              [:td (tw ["px-6" "py-4" "whitespace-nowrap"])
               (u/href
                (if (not (str/blank? title)) title "(no title)")
                ::nav/card {:card (last (str/split id "/"))})]
              [:td (tw ["px-6" "py-4" "whitespace-nowrap"])
               (if (not (str/blank? status)) status "(no status)")]
              [:td (tw ["px-6" "py-4" "whitespace-nowrap"])
               (pr-str children)]])]]]]]]]))

(defn new []
  [:div
   [:div (tw ["p-4 flex gap-x-2"])
    [button "New Card" (fn [_] (rf/dispatch [:new-card]))]]
   [:div (tw ["px-4"])
    [:h3 "Stationery"]]
   [:div (tw ["p-4 flex gap-x-2"])
    [button "New Candidate" (fn [_] (rf/dispatch [:new-card]))]
    [button "New Link" (fn [_] (rf/dispatch [:new-card]))]
    [button "New Idea" (fn [_] (rf/dispatch [:new-card]))]
    [button "New Project" (fn [_] (rf/dispatch [:new-card]))]
    [button "New Timesheet" (fn [_] (rf/dispatch [:new-card]))]
    [button "New Holiday" (fn [_] (rf/dispatch [:new-card]))]
    [button "New Article" (fn [_] (rf/dispatch [:new-card]))]
    [button "New Sales Profile" (fn [_] (rf/dispatch [:new-card]))]
    [button "New Feedback" (fn [_] (rf/dispatch [:new-card]))]]])
