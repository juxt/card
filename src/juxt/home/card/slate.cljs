;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.slate
  (:require
   [goog.object :as gobj]
   [reagent.core :as reagent]
   ["react" :as react :refer [createElement useEffect useMemo useState]]
   ["slate" :as slate :refer [createEditor]]
   ["slate-react" :refer [Slate Editable withReact]]
   [tailwind-hiccup.core :refer [tw]]))

#_(defn editor []
  (let [val (reagent/atom
             [#js {"type" "paragraph"
                   "children" #js [#js {"text" "A line of text in a paragraph."}]}])
        editor (withReact (createEditor))
        on-change-fn (fn [change-or-editor]
                       (let [new-value (.-value change-or-editor)]
                         (println "new-value:" new-value)
                         ;;(some-> @this-atom reagent/force-update)
                         change-or-editor
                         nil))]
    [:> Slate {:value @val
               :editor editor
               :on-change on-change-fn}
     [:> Editable]]))

(defn App
  []
  (let [editor (useMemo #(withReact (createEditor))
                        #js [])
                                        ; Add the initial value when setting up our state.
        #_[value setValue]
        #_(useState
           #js [#js {:type "paragraph"
                     :children
                     #js [#js {:text "A line of text in a paragraph."}]}])]
    #_(createElement Slate
                     #js {:editor editor
                          :value value
                          :onChange #(setValue %)}
                     (createElement Editable #js{}))))

#_(defn app []
  [:<>
   [:div (tw ["bg-red-200"]) "Hello World!"]
   #_[editor (tw ["bg-yellow-200"] {:name "malcolm"})]])


(reagent.dom/render
   [App]
   (js/document.getElementById "app"))
