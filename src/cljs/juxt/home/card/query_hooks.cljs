(ns juxt.home.card.query-hooks
  (:require
   [juxt.home.card.config :as config]
   [cljs-bean.core :refer [->clj ->js]]
   [react-query :refer [useQuery useQueryClient]]))

(defn fetch
  ([url] (fetch url {}))
  ([url {:keys [method] :or {method "GET"}}]
   (let [controller ^js (new js/window.AbortController)
         signal (.-signal controller)
         promise (.fetch
                  js/window url
                  #js {:method method
                       :signal signal
                       :mode "cors"
                       :timeout 5000
                       :credentials "include"})]
     (set! (.-cancel promise) (fn cancelFetch [] (.abort controller)))
     (.then promise #(.json %)))))

(defn use-query
  "Defaults to wrapping useQuery hook in ->clj so the result can be destructured.
  Pass {clj: false} in the options if using this from JS land"
  ([id fetcher] (use-query id fetcher nil))
  ([id fetcher {:keys [query-opts clj] :or {clj true}}]
   (let [hook (useQuery id fetcher (if clj (->js query-opts) query-opts))]
     (if clj (->clj hook) hook))))

(def initialUser {:id "loading"
                  :email "Loading..."
                  :fields {}
                  :name "Loading..."
                  :imageUrl "https://www.gravatar.com/avatar?d=mp"})

(defn assoc-image
  [^js user]
  (->js (assoc (->clj user)
               :imageUrl (str (.-id user)
                              "/slack/"
                              (.-username user)
                              ".jpg"))))

(defn use-self
  []
  (use-query "self"
             #(-> (fetch (str config/site-api-origin "/_site/user"))
                  (.then assoc-image))
             {:query-opts {:placeholderData initialUser
                           ;; user data is cached until page is refreshed
                           :staleTime js/Infinity}}))
