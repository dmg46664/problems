(ns dimigi.search.tech-debt
  (:require
   [dimigi.extraction :refer [stk-node]]
   [dimigi.search.stonkscreener :refer [all-stock-data db-records-from]]
   [dimigi.exclude :refer [exclude]]))



;; Descending ticker
^{:nextjournal.clerk/visibility #{:hide}}
(def desc-ticker-avg
  (as->
   '{:find
     [?s (avg ?v)]
     :keys [symbol qrt-rev-avg]
     :where [[?e :symbol ?s]
             [?e :type :qrt-revenue]
             [?e :value ?v]]
     :order-by [[(avg ?v) :desc]]} x
    (xt/q (xt/db stk-node) x)
    (tc/dataset x {:dataset-name "Average Quarterly Revenue descending"})))
;; 5s

;; At least four years data
(def min-four-years-data
  (->>
   '{:find
     [?s (min ?d) (max ?d)]
     :where [[?e :symbol ?s]
             [?e :type :qrt-revenue]
             [?e :date ?d]]}

   (xt/q (xt/db stk-node))
   (filter #(.isBefore (second %)
                       (some-> (java.time.LocalDate/now)
                               (.minusYears 4))))
   (filter #(.isAfter (nth % 2)
                      (some-> (java.time.LocalDate/now)
                              (.minusYears 1))))
   (map first)
   (into #{})))

;; fn to help filter tickers
(defn second-half-rate-gt? [series]
  (let [values (->> series
                    (map :value))
        sd (->> values (stats/stats-map) :SD)]
    (->> values
         (partition 2 1) ;; second argument is how each set steps through.
         (map reverse)
         (map #(apply - %)) ;; difference
         (#(split-at (some-> % count (/ 2)) %)) ;; This works for arbitrary rate
         (map #(reduce + %))
         (map + [(* 3 sd) 0]) ;; increase left-hs by ~sd
         #_(apply <)
         ;;
))) ;; TODO can express as a ratio


;; Check if first half delta is greater than second half.
(def stocks-ticking-up
  (if (tc/empty-ds? all-stock-data)
    #{}
    (-> all-stock-data
        (tc/group-by :symbol)
        (tc/groups->map)
        (->> (map (fn [x]
                    [(some-> x first)
                     (second-half-rate-gt? (tc/rows (second x) :as-maps))]))

             #_#_#_(filter second)
                 (map first)
               (into #{})
             (take 1))))

;;
  )





;; Sudden uptick stoks
^{:nextjournal.clerk/visibility #{:hide}}
(def records-of-uptick-stocks1
  (let [tickers (->>
                 (tc/rows desc-ticker-avg :as-maps)
                 (map :symbol) 
                 (filter (comp not exclude))
                 (filter #(and 1
                             (stocks-ticking-up %)
                             ))
                 (filter min-four-years-data)
                 (take 10)
                   )]
    (db-records-from (map :symbol tickers))
    )
  )
