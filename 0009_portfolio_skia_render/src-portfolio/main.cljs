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

;; See setting up skia web
;; https://github.com/Shopify/react-native-skia/issues/927
;; https://github.com/PEZ/rn-rf-shadow
;; https://retool.com/blog/how-to-make-your-react-native-apps-work-on-the-web/
;; https://necolas.github.io/react-native-web/docs/?path=%2Fdocs%2Foverview-getting-started--page
;; https://necolas.github.io/react-native-web/docs/setup/

;; Is the reason that it is looking into internals?
;; https://github.com/necolas/react-native-web/issues/2147
;; Can't be, or this wouldn't work with non clojure demos.


;; TODO Problems https://github.com/Shopify/react-native-skia/issues/1871

(defscene react-native
  (r/as-element
   [:> rn/Text {} (str "Hello world: ")
    ]
   ))


;; NOTE there was a problem with react-native-skia 0.1.210 (as I was prepping this example) so
;; downgraded to a familiar prior version. "package canvaskit-wasm had exports, but could not resolve ./package.json"


;; https://shadow-cljs.github.io/docs/UsersGuide.html#_loading_code_dynamically
(def loadable-canvas (lazy/loadable app/skia-canvas))

(defscene skia-canvas
  #_ 
  (r/as-element
   [:> rn/Text {} (str "Hello world: ")])

  ($ WithSkiaWeb

       {:opts #js {:locateFile
                   #(str "https://cdn.jsdelivr.net/npm/canvaskit-wasm@" version "/bin/full/" %)}
        :get-component (fn []
                         (js-await [canvas (lazy/load loadable-canvas)]
                                   #js {:default (fn []
                                                   ($ canvas {}))}))}))

