(ns main
  (:require [portfolio.react-18 :refer-macros [defscene]]
            [reagent.core :as r]
            [uix.core :refer [$ defui]]
            [uix.dom]
            ["react-native" :as rn]
            [shadow.lazy :as lazy]
            [shadow.cljs.modern :refer [js-await]]

            ["@shopify/react-native-skia/lib/module/web" :refer [WithSkiaWeb]]
            ["canvaskit-wasm/package.json" :refer [version]]))

(defscene react-native
  (r/as-element
   [:> rn/Text {} (str "Hello world!")
    ]
   ))


;; NOTE there was a problem with react-native-skia 0.1.210 (as I was prepping this example) so
;; downgraded to a familiar prior version. "package canvaskit-wasm had exports, but could not resolve ./package.json"


;; https://shadow-cljs.github.io/docs/UsersGuide.html#_loading_code_dynamically
(def loadable-canvas (lazy/loadable app/skia-canvas))

(defscene skia-canvas
  ($ WithSkiaWeb

       {:opts #js {:locateFile
                   #(str "https://cdn.jsdelivr.net/npm/canvaskit-wasm@" version "/bin/full/" %)}
        :get-component (fn []
                         (js-await [canvas (lazy/load loadable-canvas)]
                                   #js {:default (fn []
                                                   ($ canvas {}))}))}))

