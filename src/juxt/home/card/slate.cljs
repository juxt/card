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
  (aset (.-attributes props) "className" "bg-gray-300 p-2 m-2")
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
                 (rf/dispatch [:new-paragraph (str config/site-api-origin "/card/cards/section-containing-checklist-1")])
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
                              #js {:match (fn [n] (.isBlock Editor editor n))})))

               ))}))))

(defmulti render-entity :juxt.site.alpha/type)

(defmethod render-entity :default [component]
  [:div (str "type: " (:juxt.site.alpha/type component))])

;; A segment is an individual component of a linear sequence making up a
;; paragraph.
(defn render-segment [child]
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
      (render-entity child)
      {:key (:crux.db/id child)})))

(defmethod render-entity "User" [user]
  {:text (str "@" (:juxt.pass.alpha/username user))
   :_id (:crux.db/id user)})

(defmethod render-entity "Paragraph" [component]
  [:> Block {:id (:crux.db/id component)
             :content
             [{:type "paragraph"
               :id (:crux.db/id component)
               :children (map-indexed
                          (fn [ix child]
                            ^{:key ix}
                            (assoc (render-segment child) :_ix ix))
                          (:content component))}]
             :save (fn [val]
                     (rf/dispatch [:save-paragraph (:crux.db/id component) val])
                     #_(println "Save! " (:crux.db/id component) " -> " val))}])

(defmethod render-entity "Checklist" [component]
  [:div (pr-str (:crux.db/id component))]
  #_(for [child (:content component)]
    ^{:key (:crux.db/id child)}
    (render-entity child)))

(defmethod render-entity "Task" [component]
  [:div (tw ["flex" "m-2"])
   [:label (tw ["flex" "items-center"])
    [:input (tw ["form-checkbox"] {:type "checkbox" :checked (= (:status component) "DONE")})]
    [:span (tw ["ml-2"]) (str (:title component))]
    [:small (tw ["ml-2"])
     "("
     [:span (tw ["mr-2"]) (str "Priority: " (:priority component))]
     [:span (str "Deadline: " (:deadline component))]
     ")"]]])

(defn card []
  (let [data @(rf/subscribe
               [::sub/card
                (str config/site-api-origin
                     "/card/cards/section-containing-checklist-1")])]
    [:<>
     [:div (tw ["p-4" "m-4" "border-2"])

      (for [child (:content data)]
        (render-segment child))]

     #_[:pre (tw ["w-auto" "whitespace-pre-wrap"]) (map :crux.db/id (:content data))]]))
