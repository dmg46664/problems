(ns dimigi.logging.ifc
  (:require [dimigi.logging.core-jvm :as logging-jvm]))
;; See comments in logging-node

;; TODO For some reasons inconsistencies are not appearing in
;; poly check. Perhaps it's macros, no-op clojurescript code,
;; cljc vs clj or soemthing else that is cause this?

(defn configure-logging! [args]
  (logging-jvm/configure-logging! args)
  )
