;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.slate
  (:require
   [juxt.home.card.config :as config]
   [juxt.home.card.subscriptions :as sub]
   [re-frame.core :as rf]
   [tailwind-hiccup.core :refer [tw]]
   [goog.object :as gobj]
   ["react" :as react :refer [createElement useCallback useEffect useMemo useState]]
   ["slate" :as slate :refer [createEditor Editor Transforms]]
   ["slate-react" :refer [Editable Slate withReact]]
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
    (createElement
     Slate
     #js {:editor editor
          :value value
          :onChange (fn [val]
                      (setValue val)
                      (when timeout (js/clearTimeout timeout))
                      (storeTimeout (js/setTimeout
                                     (fn []
                                       (when save (save val)))
                                     1000)))}

     (createElement
      Editable
      #js { ;;:className (clj->js (:class (tw ["bg-yellow-100"])))
           :renderElement renderElement
           :renderLeaf renderLeaf
           :autoFocus true
           :onFocus (fn [ev]
                      (let [tmp (.. ev -target -value)]
                        #_(println "focus: tmp is" tmp)
                        (set! (.. ev -target -value) "")
                        (set! (.. ev -target -value) tmp)))
           :onKeyDown
           (fn [ev]
             (cond
               (= (.-key ev) "Enter")
               (do
                 (.preventDefault ev)
                 ;; TODO: Add block
                 (rf/dispatch [:new-paragraph container-id])
                 (println "ENTER!"))

               (= (.-key ev) "Backspace")
               (do
                 (println ">! '" (.. ev -target -textContent) "'")
                 (when (str/blank? (.. ev -target -textContent))
                   (println "TODO: Remove para!")
                   (.dir js/console (.. ev -target)))
                 nil)

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
  [:div (str "type: " (:juxt.site.alpha/type component))])

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

(defmethod render-entity "User" [container-id user]
  {:text (str "@" (:juxt.pass.alpha/username user))
   :_id (:crux.db/id user)})

(defmethod render-entity "Paragraph" [container-id component]
  [:> Block {:containerId container-id
             :id (:crux.db/id component)
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
                          (:content component))}]
             :save (fn [val]
                     (rf/dispatch [:save-paragraph (:crux.db/id component) val])
                     #_(println "Save! " (:crux.db/id component) " -> " val))}])

(defmethod render-entity "Checklist" [container-id component]
  [:div (pr-str (:crux.db/id component))]
  #_(for [child (:content component)]
    ^{:key (:crux.db/id child)}
    (render-entity container-id child)))

(defmethod render-entity "Task" [container-id component]
  [:div (tw ["flex" "m-2"])
   [:label (tw ["flex" "items-center"])
    [:input (tw ["form-checkbox"] {:type "checkbox" :checked (= (:status component) "DONE")})]
    [:span (tw ["ml-2"]) (str (:title component))]
    [:small (tw ["ml-2"])
     "("
     [:span (tw ["mr-2"]) (str "Priority: " (:priority component))]
     [:span (str "Deadline: " (:deadline component))]
     ")"]]])

(defn card [id]
  (let [data @(rf/subscribe [::sub/card id])]
    [:<>
     [:div (tw ["m-4"])
      (for [child (:content data)]
        ^{:key (:crux.db/id child)}
        [:div (tw ["border-2" "m-2" "p-2"])
         [:p (tw ["text-sm" "text-gray-200"]) (:crux.db/id child)]
         (render-segment id child)])]

     #_[:pre (tw ["w-auto" "whitespace-pre-wrap"]) (map :crux.db/id (:content data))]]))
