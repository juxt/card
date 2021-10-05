;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.main
  (:require
   [helix.core :as helix :refer [$]]
   [juxt.lib.helix :refer [defnc]]
   [juxt.home.card.events.views :as events]
   [juxt.home.card.people.views :as people]
   [helix.hooks :as hooks]
   [helix.dom :as d]
   [cljs-bean.core :refer [->clj ->js]]
   [react-toastify :refer [ToastContainer]]
   [react-query :refer [QueryClient QueryClientProvider]]
   [react-router-dom :refer [BrowserRouter Routes Route]]
   ["react-query/devtools" :refer [ReactQueryDevtools]]
   ["/juxt/card/stories/Navbar" :refer [NavBar]]
   [juxt.home.card.query-hooks :refer [use-self]]
   [react-dom :as rdom]))

(def query-client (QueryClient. {:defaultOptions
                                 {:queries
                                  {:staleTime 5000}}}))

(defnc home
  []
  (d/div "home screen"))

(def navbar-pages
  "Top level pages that will appear in the navbar. Other pages are defined
  directly in the 'pages' component"
  [{:path "/"
    :name "Home"
    :element ($ home)}
   {:path "events"
    :name "Events"
    :element ($ events/view)}
   {:path "people"
    :name "People"
    :element ($ people/view)}])

(defnc pages
  []
  ($ Routes
     (for [page-props navbar-pages]
       ($ Route {:& page-props
                 :key (:path page-props)}))
     ($ Route {:path "*" :element (d/h1 "Page not found")})))

(defnc app
  []
  (let [{:keys [data isError]} (use-self)]
    (cond
      isError
      (d/p {:class "text-red-500"} "Error loading user... Are you logged into site?")
      :else
      ($ BrowserRouter
         (d/main
          {:class "app-container"}
          ($ NavBar {:navigation (->js navbar-pages)
                     :logo "https://home.juxt.site/x-on-dark.svg"
                     :user (->js data)})
          ($ pages)
          (d/div {:class "p-4 lg:p-10"}
                 ($ ReactQueryDevtools {:initialIsOpen true})))))))

(rdom/render
 ($ QueryClientProvider
    {:client query-client}
    ($ app)
    ($ ToastContainer {:position "bottom-center"
                       :pauseOnFocusLoss false
                       :autoClose 1500}))
 (js/document.getElementById "app"))
