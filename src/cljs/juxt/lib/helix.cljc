(ns juxt.lib.helix
  #?(:clj (:require [helix.core :as helix]))
  #?(:cljs (:require-macros [juxt.lib.helix])))

#?(:clj
   (defmacro defnc [type params & body]
     (let [[docstring params body] (if (string? params)
                                     [params (first body) (rest body)]
                                     [nil params body])
           opts? (map? (first body)) ;; whether an opts map was passed in
           opts (if opts?
                  (first body)
                  {})
           body (if opts?
                  (rest body)
                  body)
           ;; feature flags to enable by default
           default-opts {:helix/features {:fast-refresh true
                                          :define-factory true
                                          :check-invalid-hooks-usage true}}]
       `(helix.core/defnc ~type ~@(when docstring [docstring]) ~params
          ;; we use `merge` here to allow individual consumers to override feature
          ;; flags in special cases
          ~(merge default-opts opts)
          ~@body))))
