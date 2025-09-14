^{:nextjournal.clerk/visibility #{:fold}}
(ns dimigi.search.stonkscreener
  (:gen-class)
  (:require
   #_[nextjournal.clerk :as clerk]
   [xtdb.api :as xt]
   [dimigi.extraction :refer [revenue-results-to-vegalite stk-node configure-live-db!]]
   [dimigi.exclude :refer [exclude]]
   [time-literals.read-write]
   [fastmath.stats :as stats]
   [scicloj.kindly.v4.kind :as kind]
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as dfun]
   [tech.v3.datatype.gradient :as dgrad])
  )

;; Commands
(comment

;; ONCE LOADED, RERUN THE FOLLOWING TO START
  (configure-live-db!)

  (+ 1 1))


;; 7s
(def all-stock-data (as->
                     '{:find
                       [(pull ?e [*]) ?s]
                       :where [[?e :type :qrt-revenue]
                               [?e :symbol ?s]]
                       :order-by [[?s :asc]]
                       } x
                      (xt/q (xt/db stk-node) x)
                      (map first x)
                     (tc/dataset x {:column-names [:symbol :value]})
                     )
  )
;; 11s


;; Tablecloth api https://scicloj.github.io/tablecloth/
;; Datatype.next arrays  https://www.youtube.com/watch?v=5u3_k_D5KSI
;; Conversation
;; https://clojurians.zulipchat.com/#narrow/stream/151924-data-science/topic/How.20to.20cumulate.20on.20an.20operation.3F


(defn moving-avg [rdr n]
 (let [shifted-seqs (->>
                     (range 1 n)
                     (map #(-> rdr (dfun/shift %)))
                     (concat [rdr]))
       args (concat
             [(fn [& rest]
                (/ (apply + rest) n))
              :float32] shifted-seqs)]
   (apply dtype/emap args)))

;;(moving-avg (range 10) 4) ;; => [0.0 0.25 0.75 1.5 2.5 3.5 4.5 5.5 6.5 7.5]


(defn db-revenue-companies
  "Get alphabetical records ordered by stocks."
  [tickers]
  (as->
   '{:find
     [(pull ?e [*]) ?s ?d]
        ;; https://docs.xtdb.com/language-reference/1.24.0/datalog-queries/#_collection_binding
     :in [[?batch ...]]
     :where [[?e :symbol ?batch]      ;; matching against :in batch seems to be slow!!! 
             [?e :symbol ?s]
             [?e :date ?d]
             [?e :type :qrt-revenue]]
     ;; TODO this was commented out, perhaps dates should not be strings? slow?
     ;; is this slow?
     :order-by [[?s :asc]
                [?d :asc]]} x
    (xt/q (xt/db stk-node) x tickers)
    (revenue-results-to-vegalite x)
    (tc/dataset x)))


(defn db-revenue-company [ticker]
  (as->
   '{:find
     [(pull ?e [*]) ?s ?d]
     :where [[?e :symbol :ticker]
             [?e :symbol ?s]
             [?e :date ?d]
             [?e :type :qrt-revenue]]
     :order-by [[?s :asc]
                [?d :asc]]}
   x
    (clojure.walk/postwalk-replace {:ticker ticker} x)

    (xt/q (xt/db stk-node) x)
    (revenue-results-to-vegalite x)
    (tc/dataset x)))


;; NOTE old method above

;; 2 vs 4 year
(defn two-vs-4year? [series]
  (let [values (->> series
                    (map :value))
        sd (->> values (stats/stats-map) :SD)
        years6 (->> values
                    ;;(partition 2 1)
                    ;;(map reverse)
                    ;;(map #(apply - %))
                    (reverse) ;;recent first
                    (take (* 6 4)) ;;6 years, 4 quarters
                    )]
    (if (not= (count years6) 24)
      {:message "less than 6 years"
       :div -1 ;;rank bottom
       }
      (->>
       years6
       (split-at 8) ;; last 2 years
       ;;(map (comp :SD stats/stats-map))
       (map #(reduce + %))
       ;;(map + [0 (* 1 sd)]) ;; increase left-hs by ~sd
       (zipmap [:last2-years :prev4-years])
       ((fn [x]
          (assoc x :div (if (zero? (:prev4-years x))
                          1000123
                          (/ (:last2-years x)
                             (:prev4-years x))

                          )
                 :series series
                 )))))))

(def last-2-years-min-consideration-revenue 12000000)

(defn exclude-missing-recent [data years]
  (tc/select-rows data (fn [row]
                    (.isAfter (:date row)
                              (.minusMonths (java.time.LocalDate/now) (* years 12))))))


;; Two vs 4 year differential
(def stocks-2-vs-4
  (if (tc/empty-ds? all-stock-data)
    #{}
    (-> all-stock-data
        (exclude-missing-recent 6.5)
        (tc/group-by [:symbol :exchange])
        (tc/groups->map)
        (->> (map (fn [x]
                    [(some-> x first)
                     (two-vs-4year? (->
                                     (second x)
                                     ;; Not necessary, two-vs-4 reverses it
                                     ;;(tc/order-by [:date] [:desc])
                                     (tc/rows  :as-maps)))]))
             ;; non sensical result divide by zero
             (filter #(not (and
                            (some-> % second :last2-years (= 0))
                            (some-> % second :prev4-years (= 0)))))

;; "REFR" has so little revenue that it is clipped on Macrotrends.
             (filter #(some-> % second :last2-years (> last-2-years-min-consideration-revenue)))

             (sort-by (comp :div second))
             (reverse)

             ;;(filter #(-> % first :symbol (= "EVGN")))
             ;;(map first)
             ;;(into #{})
             )))
  ;;
  )


;; --------------------
;; NOTE Only need to rerun the following after changing 'exclude
;; --------------------


;;^{:nextjournal.clerk/visibility #{:hide}}


(def records-2-vs-4-excluded
  (->> stocks-2-vs-4
       (map first)
       (filter (comp not exclude :symbol))
       (take 10)
       (map :symbol)
       (db-revenue-companies)))
;; 9s

;; Some more useful queries
(comment

  (count exclude)

  (def distinct-stocks
    (-> all-stock-data
         (tc/group-by [:symbol :exchange])
         #_
         (tc/groups->map)
         #_#_
         (map :symbol)
         (distinct)
         ))

  (def distinct-values
    (->> records-2-vs-4-excluded
         (map :value)
         (distinct)
         )
    )

  (def individual-stocks
    (->>
     '{:find
       [(pull ?e [*])]
       :where [[?e :symbol #{"A"}]
               [?e :type :qrt-revenue]]
       }
     (xt/q (xt/db stk-node))
     (revenue-results-to-vegalite)
     ))
  )




(defn ensure-group-by-ticker
  "https://github.com/scicloj/tablecloth/issues/68"
  [records]
  (let
   [stocks records]
    (if (tc/empty-ds? stocks)
      {}
      (-> stocks
          (tc/group-by [:symbol :exchange])
          tc/groups->map))))


(defn gen-series [column [ticker group]]
  {:type "line"
   :name (str (:exchange ticker) ":" (:symbol ticker)
              (if (= column :value) ""
                  (str " " column)))
   :dimensions ["Date" "Value"]
   :data (-> group
             (tc/select-columns [:date column])
             (tc/rows)
             (as-> y (map (fn [[d v]] [(str d)
                                       (/ v 1000)])
                          y)))
   :encode {:x "Date"
            :y "Value"}})

(defn draw-echarts [series-data]
  (kind/echarts
   {:legend {:orient "vertical"
             :left 150}
    :title {:text "Stocks"},
    :xAxis {:type "time"}
    :yAxis {;;:type "log"
            :name "Revenue in 1000s"}
    :tooltip {;;:trigger "axis"
              :axisPointer {:type "cross"}
                ;;:position "left"
              }
    :series series-data}))

(defn draw-echarts-default [charts-data]
  (->>
   charts-data
   (map (partial gen-series :value))
   (draw-echarts)))


(def all-stock-with-moving-avgs
  (->
   all-stock-data
   ensure-group-by-ticker ;; need to group before calculating moving averages.
   (->> (map (fn [[t g]]
               [t (-> g
                      (tc/add-column :ma4 (-> g :value (moving-avg 4)))
                      (tc/add-column :ma8 (-> g :value (moving-avg 8))))])))
   ;; TODO unforunately not able to tc/ungroup
   (->> (into {}))))


;; ========================================
;; Charts
;; ========================================


(-> records-2-vs-4-excluded
    ensure-group-by-ticker
    draw-echarts-default)



(defn mv-avg-cross? [ma4 ma8]
  (->
   (dfun/- ma4 ma8)
;;     (dfun/< ma8 ma4)
   
   (dfun/signum)
   (dgrad/diff1d)
   (reverse) ;; TODO  https://github.com/cnuernber/dtype-next/issues/120
   (->> (map-indexed vector))
   (->> (take 12)) ;; quarters
   (->> (filter (comp not zero? second)))
   (->> (map (fn [[a b]] {:period a
                         :direction b})))
   ))

(defn contains-revenue-after-2024? [data]
  (-> data
      (tc/select-rows
       (fn [x] (-> x :date (.isAfter #time/date "2024-01-01"))))
      (tc/->array :date)
      seq
      ))

(def symbol-excluded? (comp not (set exclude) :symbol first))

(comment

  (-> all-stock-with-moving-avgs
      (get {:symbol "DSCM", :exchange :NASDAQ})
      )

  ;; CHANGE IN TREND

  (->>
   all-stock-with-moving-avgs
   (filter symbol-excluded?)
   (filter (comp contains-revenue-after-2024? second))
   ;; TODO do this earlier and ungroup
   (map (fn [[t {:keys [ma4 ma8]}]]
          [t (mv-avg-cross? ma4 ma8)]))
   (filter
    (fn [[_ cross]]
      (and
       (some-> cross count (= 1))
       (some-> cross first :period (as-> x (> 5 x 3)))
       (some-> cross first :direction (> 0)))))

   ;; Got the answers at this point
   (map first)
   (take 10)
   (select-keys all-stock-with-moving-avgs)
   draw-echarts-default)

;;
  )

(comment

  ;; moving average for one stock
  (->
   all-stock-with-moving-avgs
   (get
    {:symbol "T", :exchange :NYSE} ;;
    )
   ensure-group-by-ticker ;; gen-series expects grouping.
   (as-> x
         (concat
          (map (partial gen-series :value) x)
          (map (partial gen-series :ma4) x)
          (map (partial gen-series :ma8) x)))
   (draw-echarts))

;;
  )



(-> ["CVNA" "NU" "PTON" "CDLX" "DOCU" "BUR" "HUMA" "ANET" "BALY"]
    db-revenue-companies
    ensure-group-by-ticker
    draw-echarts-default
    )

(comment
  ;; Just to see
  (-> "MED"
      db-revenue-company
      ensure-group-by-ticker
      draw-echarts-default)
  ;; TODO Energy companies to start investigating.
  (-> ["CVE" #_"LNG"]
      db-revenue-companies
      (tc/rows)
      ensure-group-by-ticker
      #_draw-echarts)
  ;; TODO polish CDR
  (-> ["GRWG" "APPS" "CELH" "BLFS" "SNAP"]
      db-revenue-companies
      ensure-group-by-ticker
      draw-echarts-default)
  (-> ["ALEC"]
      db-revenue-companies
      ensure-group-by-ticker
      draw-echarts-default)
  ;; Interesting but not biting
  (-> ["RVP"]
      db-revenue-companies
      ensure-group-by-ticker
      draw-echarts-default))

