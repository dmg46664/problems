(ns app
  (:require
   [uix.core :refer [$ defui] :as uix]
   ["@shopify/react-native-skia" :as sk]
   ["react-native" :as rn]
   ;;
   ))

(def size 256)
(def r (* size 0.33))

;; (def font-style #js {:fontFamily "FiraCode"
;;           :fontSize 14
;;           :fontStyle "normal"
;;           :fontWeight "normal"
;;           })

;; (def font (sk/matchFont font-style))

;; https://github.com/Shopify/react-native-skia/blob/main/package/src/renderer/__tests__/setup.tsx#L101

(defui skia-canvas []
  (let [font (sk/useFont "http://localhost:8082/FiraCode-Regular.ttf", 30)
        [state sets!] (uix/use-state 0)]
    ($ rn/View {:style #js {:flex 1
                            :justifyContent "center"}}
       ($ rn/Button {:title (str "Click me: " state) :on-press #(sets! (inc state))})
       ($ rn/Text {} (str "Font families: " (sk/listFontFamilies)))
       ($ sk/Canvas #_{:style #js {:flex 1}}
          ($ sk/Group {:blend-mode "multiply"}
             ($ sk/Circle {:cx r :cy r :r (+ state r) :color "red"})
             ($ sk/Circle {:cx (- size r) :cy r :r r :color "magenta"})
             ($ sk/Circle {:cx (/ size 2) :cy (- size r) :r r :color "yellow"}))
          ($ sk/Text {:x 0 :y 20 :text "Hello World!" :font font :color "black" #_#_:font-size 20})))))
