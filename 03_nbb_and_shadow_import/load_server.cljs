(ns load-server
  (:require
   ["ws$default" :as WebSocket]
   ["ws" :refer [WebSocketServer]]
   [applied-science.js-interop :as j])
  )

;; NBB
(def WebSocketServer (j/get WebSocket :WebSocketServer))

;; launch server
(defn start-server! []
 (js/console.log "Launching server")
 (new WebSocketServer (clj->js {:port 4567}))
)
