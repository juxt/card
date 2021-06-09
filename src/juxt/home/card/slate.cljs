;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.slate
  (:require
   [juxt.home.card.config :as config]
   [juxt.home.card.subscriptions :as sub]
   [re-frame.core :as rf]
   [tailwind-hiccup.core :refer [tw]]
   ["react" :as react :refer [createElement useCallback useEffect useMemo useState]]
   ["slate" :as slate :refer [createEditor Editor Transforms]]
   ["slate-react" :refer [Editable Slate withReact]]))

(defn CodeElement
  [props]
  (aset (.-attributes props) "className" "bg-yellow-100 p-2")
  (createElement "pre" (.-attributes props)
    (createElement "code" #js{}
                   (.-children props))))

(defn DefaultElement
  [props]
  ;;(aset (.-attributes props) "className" "bg-gray-100 p-2")
  (createElement "p" (.-attributes props)
                 (.-children props)))



(defn Block
  [block]
  (let [editor (useMemo #(withReact (createEditor)) #js [])
        [value setValue] (useState (.-content block))
        renderElement (useCallback
                       (fn [props]
                         (case (.-type (.-element props))
                           "code" (createElement CodeElement props)
                           (createElement DefaultElement props))))]

    (createElement
     Slate
     #js {:editor editor
          :value value
          :onChange #(setValue %)}

     (createElement
      Editable
      #js { ;;:className (clj->js (:class (tw ["bg-yellow-100"])))
           :renderElement renderElement
           :onKeyDown
           (fn [ev]
             (cond
               (= (.-key ev) "Enter")
               (do
                 (.preventDefault ev)
                 ;; TODO: Add block
                 (println "ENTER!"))

               (and (= (.-key ev) "`") (.-ctrlKey ev))
               (do
                 (.preventDefault ev)
                 (let [[match] (es6-iterator-seq
                                (.nodes Editor editor #js {:match (fn [n] (= (.-type n) "code"))}))]
                   (.setNodes Transforms
                              editor
                              #js {:type (if match "paragraph" "code")}
                              #js {:match (fn [n] (.isBlock Editor editor n))})))))}))))

(defmulti render-component :juxt.site.alpha/type)

(defn render-leaf [child]
  (cond
    (vector? child)
    (let [[type content] child]
      (case type
        "text" {:text content}
        {:text (str "(unknown:<" type ">)")}))
    (map? child)
    (render-component child)))

(defmethod render-component "User" [user]
  {:text (str "@" (:juxt.pass.alpha/username user))})

(defmethod render-component :default [component]
  [:div (str "type: " (:juxt.site.alpha/type component))])

(defmethod render-component "Paragraph" [component]
  [:> Block {:content
             [{:type "paragraph"
               :children (for [child (:content component)]
                           (render-leaf child))}]}])

(defmethod render-component "Checklist" [component]
  (for [child (:content component)]
    ^{:key (:crux.db/id child)}
    (render-component child)))

(defmethod render-component "Task" [component]
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
                     "/card/cards/section-containing-checklist-1"
                     #_"/card/cards/task-1")])]
    [:<>
     [:div (tw ["p-4" "m-4" "border-2"])

      (for [block (:content data)]
        ^{:key (:crux.db/id block)}
        (render-component block))]

     #_[:pre (tw ["w-auto" "whitespace-pre-wrap"]) (drop 1 (:content data))]]))
