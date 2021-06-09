;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.slate
  (:require
   ["react" :as react :refer [createElement useCallback useEffect useMemo useState]]
   ["slate" :as slate :refer [createEditor Editor Transforms]]
   ["slate-react" :refer [Editable Slate withReact]]
   [tailwind-hiccup.core :refer [tw]]))

(defn CodeElement
  [props]
  (aset (.-attributes props) "class" "bg-yellow-100 p-2")
  (createElement "pre" (.-attributes props)
    (createElement "code" #js{}
                   (.-children props))))

(defn DefaultElement
  [props]
  (aset (.-attributes props) "class" "bg-gray-100 p-2")
  (createElement "p" (.-attributes props)
                 (.-children props)))



(defn App
  []
  (let [editor (useMemo #(withReact (createEditor)) #js [])
        ;; Add the initial value when setting up our state.
        [value setValue]
        (useState
         #js [#js {:type "paragraph"
                   :children
                   #js [#js {:text ""}]}])

        renderElement
        (useCallback
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
      #js {;;:className (clj->js (:class (tw ["bg-yellow-100"])))
           :renderElement renderElement
           :onKeyDown
           (fn [ev]
             (cond
               (= (.-key ev) "Enter")
               (.preventDefault ev)

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

(defn app []
  [:<>
   [:div (tw ["p-4" "m-4" "border-2"])
    [:div (tw ["p-2"])
     [:> App]]
    [:div (tw ["p-2"])
     [:> App]]]])
