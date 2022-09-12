(ns hello
  (:require
   ["chalk$default" :as chalk])
  )

(defn print-hello []
  (js/console.log (chalk/green "Hello!")))



