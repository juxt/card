(ns juxt.home.card.mutations
  (:require
   [juxt.home.card.query-hooks :refer [fetch]]
   [react-query :refer [useMutation]]))

(defn delete
  [id]
  (useMutation)
  (fetch id {:method "DELETE"}))
