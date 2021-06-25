;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.slate
  (:require
   [juxt.home.card.config :as config]
   [juxt.home.card.subscriptions :as sub]
   [re-frame.core :as rf]
   [tailwind-hiccup.core :refer [tw]]
   [goog.object :as gobj]
   ["react" :as react :refer [createElement useCallback useEffect useMemo useState]]
   ["slate" :as slate :refer [createEditor Editor Transforms Node]]
   ["slate-react" :refer [Editable Slate withReact ReactEditor]]
   [clojure.string :as str]))

(defn CodeElement
  [props]
  (aset (.-attributes props) "className" "bg-yellow-100 p-2")
  (createElement "pre" (.-attributes props)
    (createElement "code" #js{}
                   (.-children props))))

(defn DefaultElement
  [props]
  (aset (.-attributes props) "className" "bg-gray-100 p-2 border-2")
  (createElement "p" (.-attributes props)
                 (.-children props)))

(defn Leaf
  [props]
  (let [leaf (gobj/get props "leaf")
        style (gobj/get leaf "style")]
    (createElement "span"
      (doto (gobj/clone (.-attributes props))
        (gobj/set "style" style))
      (.-children props))))

(defn Block
  [props]
  (let [editor (useMemo #(withReact (createEditor)) #js [])
        [value setValue] (useState (.-content props))
        save (.-save props)
        container-id (.-containerId props)
        id (.-id props)
        index (.-index props)
        [timeout storeTimeout] (useState nil)
        renderElement (useCallback
                       (fn [props]
                         (case (.-type (.-element props))
                           "code" (createElement CodeElement props)
                           (createElement DefaultElement props))))
        renderLeaf (useCallback
                    (fn renderLeaf [props]
                      (createElement Leaf props))
                    #js [])]
    (aset js/window "focusmap" (assoc (or (aget js/window "focusmap") {})
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
      #js { ;;:className (clj->js (:class (tw ["bg-yellow-100"])))
           :renderElement renderElement
           :renderLeaf renderLeaf
           ;;           :autoFocus true
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

(defmulti render-entity (fn [container-id component] (:juxt.site.alpha/type component)))

(defmethod render-entity :default [container-id component]
  [:div (str "type: '" (:juxt.site.alpha/type component) "'")])

;; A segment is an individual component of a linear sequence making up a
;; paragraph.
(defn render-segment [container-id child]
  (cond
    (vector? child) ; it's a text segment, not an @ mention
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
      (render-entity container-id child)
      {:key (:crux.db/id child)})))

(defn render-block [container-id component]
  [:div
   [:> Block {:containerId container-id
              :id (:crux.db/id component)
              :index (:ix component)
              :content
              ;; An entity of type 'Paragraph' maps to a block with a single
              ;; 'paragraph' child. We don't allow more than one Slate paragraph
              ;; in a Site paragraph.
              [{:type "paragraph"
                :id (:crux.db/id component)
                :children (map-indexed
                           (fn [ix child]
                             ^{:key ix}
                             (assoc (render-segment container-id child) :_ix ix))
                           (:juxt.card.alpha/content component))}]
              :save (fn [val]
                      (let [s (.string Node #js {:children val})]
                        ;; when-not (str/blank? s)
                        (prn "Save! " (:crux.db/id component) " -> " val)
                        (rf/dispatch [:save-paragraph (:crux.db/id component) val])))}]])

(defmethod render-entity :default [container-id component]
  [:div
   (render-block container-id component)])

(defmethod render-entity "juxt.card.types/task" [container-id component]
  [:div
   [:input (tw ["mx-2"] {:type "checkbox"
                         :checked (case (:juxt.card.alpha/status component) "DONE" true "TODO" false)
                         :onChange (fn [ev]
                                     (if (.-checked (.-target ev))
                                       (rf/dispatch [:check-action (:crux.db/id component)])
                                       (rf/dispatch [:uncheck-action (:crux.db/id component)])))})]
   (render-block container-id component)])

(defn card [id]
  (let [id @(rf/subscribe [::sub/current-card])
        data @(rf/subscribe [::sub/card id])]
    [:<>
     [:div (tw (cond-> ["m-4" "border-2"]
                 (:optimistic data) (conj "border-green-200")
                 (:error data) (conj "border-red-400")))

      (when-let [title (:juxt.card.alpha/title data)]
        [:h1 (tw ["m-4" "text-xl"]) title])
      (when-let [subtitle (:juxt.card.alpha/subtitle data)]
        [:h2 (tw ["m-4" "text-lg"]) subtitle])
      [:p (tw ["m-4" "text-gray-400"]) "URL: " [:a {:href id} id]]
      (map-indexed
       (fn [ix child]
         ^{:key ix}
         [:div (tw (cond-> ["border-2" "m-2" "p-2"]
                     (:optimistic child) (conj "border-green-200")
                     (:error data) (conj "border-red-400")))
          [:p (tw ["text-sm" "text-gray-200"]) (:crux.db/id child)]
          ;; TODO: Arguably should be done in the (re-frame) subscription
          (render-segment id (assoc child :ix ix))])

       (:juxt.card.alpha/content data))]

     #_[:pre (tw ["w-auto" "whitespace-pre-wrap"]) (map :crux.db/id (:juxt.card.alpha/content data))]]))

(defn new []
  [:div (tw ["p-4"])
   [:button (tw ["inline-flex" "items-center" "px-2.5" "py-1.5" "border" "border-transparent" "text-xs" "font-medium" "rounded" "shadow-sm" "text-white" "bg-yellow-600" "hover:bg-yellow-700" "focus:outline-none" "focus:ring-2" "focus:ring-offset-2" "focus:ring-yellow-500"]
                {:type "button"
                 :onClick (fn [_] (rf/dispatch [:new-card]))}) "New"]])
