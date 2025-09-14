(ns dimigi.extraction-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [dimigi.query.stonkscreener :refer :all]
            [cljc.java-time.local-date :as ld]
            [time-literals.read-write]
            [xtdb.api :as xt]
            [dimigi.extraction :refer [prepare-xt-ticker-records submit-tx-and-await! extract-stock-tickers global-exchanges]]
            [taoensso.timbre :as t]
            [com.rpl.specter :as s]
            )
  )

(defn create-db-fixture [test-run]
  ;; Setup: define bindings, create state, etc.
  (def mem-test-node (xt/start-node {}))

  (test-run) ;; Run the relevant tests for the fixture (see `use-fixtures`)

  ;; Tear-down: reset state to a known value
  (.close mem-test-node)
  )

(use-fixtures :each create-db-fixture)

(deftest test-extract-revenue-records
  (testing "failed extract, missing the 'var chartData =' "
    (is (thrown? clojure.lang.ExceptionInfo
                 (dimigi.extraction/extract-revenue-records
                   "PTON"
                  "[{\"date\":\"2021-09-30\",\"v1\":4.069,\"v2\":0.805,\"v3\":6.2}]
                  \n
                  \t\t\"balloonText\": \"<table style='font-size:14px;'><tr><td>$[[value]]B</td></tr></table>\"
                  \n
                  \t\t\"balloonText\": \"<table style='font-size:14px;'><tr><td>$[[value]]B</td></tr></table>\""
                  )
                 )
    )
  )

  (testing "failed extract, billion marker missing"
    (is (thrown? clojure.lang.ExceptionInfo
                 (dimigi.extraction/extract-revenue-records
                   "PTON"
                   "var chartData = [{\"date\":\"2021-09-30\",\"v1\":4.069,\"v2\":0.805,\"v3\":6.2}]
                   \n
                   \t\t\"balloonText\": \"<table style='font-size:14px;'><tr><td>$[[value]]</td></tr></table>\"
                   \n
                   \t\t\"balloonText\": \"<table style='font-size:14px;'><tr><td>$[[value]]</td></tr></table>\""
                   )
                 )
        )
    )
)

(def expected-extract-and-save-data-for-PEG
  #{[{:date #time/date "2010-06-30",
      :value 2361000000N,
      :exchange :NYSE
      :type :qrt-revenue,
      :symbol "PEG",
      :xt/id "NYSE:PEG-2010-06-30"}]
    [{:exists true, :exchange :NYSE :symbol "PEG", :xt/id "NYSE:PEG-metadata"}]
    [{:date #time/date "2010-03-31",
      :value 3573000000N,
      :exchange :NYSE
      :type :qrt-revenue,
      :symbol "PEG",
      :xt/id "NYSE:PEG-2010-03-31"}]
    [{:date #time/date "2009-12-31",
      :value 2515000000N,
      :exchange :NYSE
      :type :qrt-revenue,
      :symbol "PEG",
      :xt/id "NYSE:PEG-2009-12-31"}]
    [{:exists false, :exchange :NYSE :reason :nil-no-html-at-all, :symbol "TTT", :xt/id "NYSE:TTT-metadata"}]
    [{:exists false, :exchange :NYSE :reason :nil-no-html-at-all, :symbol "QQQ", :xt/id "NYSE:QQQ-metadata"}]}
  )

(def mock-tickers-file "test/dimigi/testshortstocklist.html") ;;(slurp-ADVFN-NYSE-listings-by-letter!)

(def mock-PEG-revenue-file "test/dimigi/test-short-PEG-ticker.html") ;;(slurp-macrotrends-website-html!)

; When we download a list of tickers from ADVN we need to store that list in XTDB with both the ticker symbols
; and ideally the state of how we downloaded them from Macrotrends. We could save it like the following
;(submit {id :A :list [["abc" 456] ["avc" nil]]})
; however that would mean constantly updating this list, even as we iterate through each ticker.

; Instead we first save the list as is
;(submit {:id :A :list ["abc" "avc" "art"]})
; and then save a metadata item for each symbol regardless of whether or not we find records with the reason for the failure if not-exists.
;(save {:id "abc-metadata" :exists false :symbol "abc" :reason :???})
;
(deftest test-xtdb-with-extract-and-save-by-letter!
  (testing "test-extract-and-save-by-letter! and then check in XTDB mem-node"
    (is (= expected-extract-and-save-data-for-PEG
           (do
             (dimigi.extraction/extract-and-save-by-letter! mem-test-node
                                                            :NYSE
                                                            (fn [_letter] (->> (slurp mock-tickers-file) (extract-stock-tickers (-> global-exchanges :NYSE :reg-expr))))
                                                            (fn [ticker] (case ticker "PEG" (slurp mock-PEG-revenue-file) nil))
                                                            {:production false}
                                                            "P" ;;letter to extract
                                                            )
             ;;Query the results
             (xt/q (xt/db mem-test-node)
                   '{:find
                     [(pull ?e [*])]
                     :where [[?e :symbol #{"PEG" "QQQ" "TTT"}]]})
             
             )
           ))
    )
  
  (testing "with duplication test-extract-and-save-by-letter! and then check in XTDB mem-node"
    (is (= expected-extract-and-save-data-for-PEG
           (do
             (dimigi.extraction/extract-and-save-by-letter! mem-test-node
                                                            :NYSE
                                                            (fn [_letter] (->> (slurp mock-tickers-file) (extract-stock-tickers (-> global-exchanges :NYSE :reg-expr))))
                                                            (fn [ticker] (case ticker "PEG" (slurp mock-PEG-revenue-file) nil))
                                                            {:production false}
                                                            "P" ;;letter to extract
                                                            )

             (dimigi.extraction/extract-and-save-by-letter! mem-test-node
                                                            :NYSE
                                                            (fn [_letter] (->> (slurp mock-tickers-file) (extract-stock-tickers (-> global-exchanges :NYSE :reg-expr))))
                                                            (fn [ticker] (case ticker "PEG" (slurp mock-PEG-revenue-file) nil))
                                                            {:production false}
                                                            "P" ;;letter to extract 
                                                            )
             
             ;;Query the results
             (xt/q (xt/db mem-test-node)
                   '{:find
                     [(pull ?e [*])]
                     :where [[?e :symbol #{"PEG" "QQQ" "TTT"}]]}))))) 
  )

(deftest test-extract-and-prepare-by-letter
  (testing "test some of the output of xtdb preparation before saving"
    (is (= [[[:xtdb.api/put {:xt/id "NYSE:P", :exchange :NYSE :list '("PEG" "QQQ" "TTT")}]]
            [:xtdb.api/put
              {:date #time/date "2009-12-31",
               :value 2515000000N,
               :type :qrt-revenue,
               :exchange :NYSE
               :symbol "PEG",
               :xt/id "NYSE:PEG-2009-12-31"}]
            [:xtdb.api/put {:xt/id "NYSE:PEG-metadata", :exchange :NYSE :exists true, :symbol "PEG"}]
            [[:xtdb.api/put {:exists false, :exchange :NYSE :reason :nil-no-html-at-all, :symbol "QQQ", :xt/id "NYSE:QQQ-metadata"}]]]
           (as->
            (dimigi.extraction/extract-and-prepare-by-letter
             :NYSE
             (fn [_letter] (->> mock-tickers-file slurp (extract-stock-tickers (-> global-exchanges :NYSE :reg-expr))))
             (fn [ticker] (case ticker "PEG" (slurp mock-PEG-revenue-file) nil))
             {:production false}
             "P") x
             [(-> x (nth 0));ticker-list
              (-> x (nth 1) (nth 0));revenue record
              (-> x (nth 1) (nth 3));existing ticker - metadata
              (-> x (nth 2));non-existing-ticker - metadata
              ]
             )
           ))))

(deftest test-extract-stock-tickers
  (testing "are the stock symbols there"
    (is (= ["PX" "PAR" "PBF"]
          (->>
            (slurp "test/dimigi/stocklist.html")
            (extract-stock-tickers (-> global-exchanges :NYSE :reg-expr))
            (take 3)
            )
           ))
    ))
