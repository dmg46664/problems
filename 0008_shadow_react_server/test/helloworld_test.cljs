(ns helloworld-test
  (:require
   [cljs.test :as t :include-macros true :refer-macros [deftest is testing]]
   ["react-dom/server" :refer [renderToString] :as rserver]
   [uix.core :refer [defui $]]
   ))

;; yarn shadow-cljs -A:test watch test

(deftest style-property-names-are-camel-cased
  (is (re-find #"<div style=\"text-align:center(;?)\">foo</div>"
               (renderToString ($ :div {:style {:text-align "center"}} "foo")))))
