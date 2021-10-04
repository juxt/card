(ns juxt.home.card.common
  (:require
   [helix.dom :as d]
   [react-toastify :refer [toast]]
   [react-router-dom :refer  [useLocation]]
   [cljs-bean.core :refer [->clj]]
   [juxt.home.card.config :as config]
   [juxt.lib.helix :refer [defnc]]))

(defn use-query-params
  []
  (->clj
   (js/Object.fromEntries
    (js/URLSearchParams. (.-search (useLocation))))))

(defn error!
  [msg]
  (.error toast msg))

(defn success!
  [msg]
  (.success toast msg))

(defn toast!
  [promise-fn pending-msg success-msg error-msg]
  (.promise toast promise-fn
            #js {:pending pending-msg
                 :success success-msg
                 :error error-msg}))

(defnc hook-info
  [{:keys [hook]}]
  (d/div
   (when (:isLoading hook)
     (d/p "Loading..."))
   (when (:isError hook)
     (js/console.log (:error hook))
     (d/p "Error fetching"))))
