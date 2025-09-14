(ns load-server
  (:require
   ["ws" :as WebSocket :refer [WebSocketServer]]
   [applied-science.js-interop :as j])
  )

;; launch server
(defn start-server! []
 (js/console.log "Launching server")
 (new WebSocketServer (clj->js {:port 4567}))
)
