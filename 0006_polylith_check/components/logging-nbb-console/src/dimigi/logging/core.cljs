(ns dimigi.logging.core
  (:require
   [applied-science.js-interop :as j])
  )


(defn print-error
  ([err]
   (print-error nil err)
   )
  ([message _err]
   ;; removed
   (identity message)
  )
  )

(defn noop [args]
  ;; noop
  )

