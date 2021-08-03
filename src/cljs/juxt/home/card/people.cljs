;; Copyright © 2021, JUXT LTD.

(ns juxt.home.card.people
  (:require
   ["/juxt/card/people" :refer (People)])
  )

(def profile
  {:name "Alex Davies"
   :imageUrl
   "https://images.unsplash.com/photo-1463453091185-61582044d556?ixlib=rb-=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=8&w=1024&h=1024&q=80'"
   :coverImageUrl
   "https://images.unsplash.com/photo-1444628838545-ac4016a5418a?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80'"
   :about "<p>Tincidunt quam neque in cursus viverra orci, dapibus nec tristique. Nullam ut sit dolor consectetur urna, dui cras nec sed. Cursus risus congue arcu aenean posuere aliquam.</p>
    <p>Et vivamus lorem pulvinar nascetur non. Pulvinar a sed platea rhoncus ac mauris amet. Urna, sem pretium sit pretium urna, senectus vitae. Scelerisque fermentum, cursus felis dui suspendisse velit pharetra. Augue et duis cursus maecenas eget quam lectus. Accumsan vitae nascetur pharetra rhoncus praesent dictum risus suspendisse."

   :fields {:Phone "(555) 123-4567",
            :Email "ricardocooper@example.com",
            :Title "Senior Front-End Developer",
            :Team "Product Development",
            :Location "San Francisco",
            :Sits "Oasis, 4th floor",
            :Salary "$145,000",
            :Birthday "June 8, 1990"
            }})

(defn people []
  [:p "PEOPLE"]
  [:> People {:profile profile}])
