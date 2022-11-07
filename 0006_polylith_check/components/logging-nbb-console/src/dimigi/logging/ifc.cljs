(ns dimigi.logging.ifc
  ;; Forces requiring namespaces to :include-macros
  (:require [dimigi.logging.core :refer [print-error]])
  #?(:cljs
     (:require-macros [dimigi.logging.ifc]
                      ))
  )

(defn print-route [& args]
  )

(defmacro error [& args]
  `(print-error ~@args)
  )

(defmacro info [& args]
  `(js/console.log ~@args)
  )

(defmacro debug [& args]
  `(js/console.log ~@args)
  )

(defn configure-logging! [args]
  nil
  )
