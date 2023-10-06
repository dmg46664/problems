(ns standalone
  (:require [uix.dom]
            [uix.core :refer [$]]
            #_[reagent.core :as r]
            ["react-native" :as rn]

            [shadow.lazy :as lazy]
            [shadow.cljs.modern :refer [js-await]]

            ["@shopify/react-native-skia/lib/module/web" :refer [WithSkiaWeb]]
            ["canvaskit-wasm/package.json" :refer [version]]))

;; ======================================
;; :init-fn defined in shadow-cljs.edn
;; ======================================

(def root
  (uix.dom/create-root (js/document.getElementById "canvas")))

#_
(defn init []
  (uix.dom/render-root ($ rn/Text "Test") root))

(def loadable-canvas (lazy/loadable app/skia-canvas))

(defn init []
  (uix.dom/render-root
   ($ WithSkiaWeb

      {:opts #js {:locateFile
                  #(str "https://cdn.jsdelivr.net/npm/canvaskit-wasm@" version "/bin/full/" %)}
       :get-component (fn []
                        (js-await [canvas (lazy/load loadable-canvas)]
                                  #js {:default (fn []
                                                  ($ canvas {}))}))})
   root))
