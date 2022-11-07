(ns dimigi.logging.ifc
  #?(:cljs
     (:require [dimigi.logging.core-node :as logging-node]
               ))
  ;; Forces requiring namespaces to :include-macros
  #?(:cljs
     (:require-macros [dimigi.logging.ifc]))
  )

(defn print-route [& args]
  #?(:cljs (apply js/console.log args))
  )

(defmacro error [& args]
  `(print-route ~@args)
  )

(defmacro info [& args]
  `(print-route ~@args)
  )

(defmacro debug [& args]
  `(print-route ~@args)
  )


;; This file is really two separate interfaces.
;; The previous macros are for node logging when timbre is not available.
;; The following is for when timbre is.

(defn configure-logging! [args]
  #?(:cljs (logging-node/configure-logging! args)
     :clj (identity args)
     )
  )

;; The problem is that it's difficult to delegate macros to timbre
;; as you get a namespace error where timbre magically appears without being required.

;; It's not necessary to split this yet as if we use timbre (as per test-client)
;; then we call configure-logging! otherwise we don't and call the above.
