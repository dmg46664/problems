(ns shared
  (:require [cljs.test :refer [testing is deftest]]))

(deftest shared-test
  (testing "Int test"
    (is (= 1 2)))
  )


(defn -main []
  (cljs.test/run-tests)

 )
