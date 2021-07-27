;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.kanban
  (:require
   [reagent.core :as r]
   [tailwind-hiccup.core :refer [tw]]
   ["react-beautiful-dnd" :refer [DragDropContext Draggable Droppable]]
   [re-frame.core :as rf]))

(def drag-drop-context (r/adapt-react-class DragDropContext))
(def droppable (r/adapt-react-class Droppable))
(def draggable (r/adapt-react-class Draggable))

(defn on-drag-end [result]
  (println
   [:set-attribute (.-draggableId result) :juxt.card.alpha/status (.. result -destination -droppableId)])
  (rf/dispatch [:set-attribute (.-draggableId result) :juxt.card.alpha/status (.. result -destination -droppableId)]))

(defn kanban [items-grouped-by-status]
  [:div (tw ["flex" "m-4"])
   [drag-drop-context
    {:on-drag-end on-drag-end}
    (for [[status items] items-grouped-by-status
          :when status]
      ^{:key status}
      [:div (tw ["flex" "flex-col" #_"bg-yellow-100" "border-2"])
       [:h2 status]
       [:div
        [droppable {:droppable-id status
                    :key status}
         (fn [provided _]
           (r/as-element
            [:div (merge
                   {:ref (.-innerRef provided)}
                   (js->clj (.-droppableProps provided)))

             (for [[{id :crux.db/id
                     content :juxt.card.alpha/content
                     :as props} index] (map #(vector %1 %2) items (range))]
               [draggable
                {:key id
                 :draggable-id id
                 :index index}
                (fn [provided snapshot]
                  (r/as-element [:div (tw ["border-2" "my-2"]
                                          (merge
                                           {:ref (.-innerRef provided)
                                            ;;:class (when (.-isDragging snapshot) "column__card--dragged")
                                            }
                                           (js->clj (.-draggableProps provided))
                                           ))
                                 [:div id]
                                 [:div (js->clj (.-dragHandleProps provided))
                                  "DRAG ME!"]
                                 [:p (pr-str content)]]))])
             (.-placeholder provided)]))]]])]])
