(ns app
  (:require
   [uix.core :refer [$ defui]]
   ["@shopify/react-native-skia" :as sk]
   ["react-native" :as rn]
   ;;
   ))

(def size 256)
(def r (* size 0.33))

(defui skia-canvas []
  ($ rn/View {:style #js {:flex 1
                          :justifyContent "center"}}
   ($ sk/Canvas #_ {:style #js {:flex 1}
                 }
      ($ sk/Group {:blender-mode "multiply"}
         ($ sk/Circle {:cx r :cy r :r r :color "cyan"})
         ($ sk/Circle {:cx (- size r) :cy r :r r :color "magenta"})
         ($ sk/Circle {:cx (/ size 2) :cy (- size r) :r r :color "yellow"})))))
