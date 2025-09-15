(ns dimigi.extraction
  (:require
   [cheshire.core :as c]
   [xtdb.api :as xt]
   [cljc.java-time.local-date :as ld]
   [time-literals.read-write]
   [clojure.java.io :as io]
   [taoensso.timbre :as t]
   [taoensso.timbre.appenders.core :as appenders]
   [clojure.string :as cs]
   [babashka.cli :as bcli]
   )
  (:require [babashka.http-client :as http])
  )


;; This ensures we have an in-mem db for kaocha.
;; Re-def the real rocksdb node in the comment block below when you've
;; connected to the repl.
(defonce stk-node (xt/start-node {}))

(defn extract-revenue-records
  "Takes the html and ticker information and extracts the quarterly revenue from it
  ie:
  {:date #object[java.time.LocalDate 0x48c2aad5 \"2016-06-30\"], :value 32000000N, :type :qrt-revenue, :symbol \"AMTG\"}

  If the html is nil, doesn't include the word 'balloon' or see's that the quarterly 'v2' value is 'NULL' then
  a keyword for the reason it failed is returned.
  "
  [ticker html]
  (let [check-for-billion-and-throw (fn [lines]
                                      (when (as-> lines x
                                              (filter #(cs/includes? % "balloonText") x)
                                              (second x) ;;quarterly-section
                                              (cs/includes? x "[[value]]B")
                                              (not x))

                                        (throw (ex-info "Billion marker is missing from html" {:ticker ticker})))

                                      lines)
        billion 1000000000
        reason-map-fn (fn [reason] {:exists false :reason reason :symbol ticker})]

    (cond
     ;;If the ticker html has no usable information in it
      (nil? html) (reason-map-fn :nil-no-html-at-all)
      (not (cs/includes? html "balloon")) (reason-map-fn :html-invalid)
     ;;if macrotrend has a graph but the values are empty, in the html charData has these 'null's
      (cs/includes? html "var chartData = null") (reason-map-fn :graph-has-no-data)
      (cs/includes? html "\"v2\":null") (reason-map-fn :qrt-was-null)
      (cs/includes? html "\"v2\":NULL") (reason-map-fn :qrt-was-null) ;;TODO use re-match
     ;;otherwise extract revenue as normal
      :else (->
             html
             (cs/split-lines)
             (check-for-billion-and-throw)
             (->> (filter #(cs/includes? % "var chartData = ")))
             (first)
             (#(if (nil? %) (throw (ex-info "Cannot find: var chartData" {:ticker ticker})) %))
             (#(cs/replace % "var chartData = " ""))
             (#(c/parse-string % true))
             (->> (map (fn [record]
                         {:date (some-> record :date ld/parse)
                          :value (some-> record :v2 (* billion) bigint)
                          :type :qrt-revenue
                          :symbol ticker})))
             (doto (->
                    (->> (filter #(= (:symbol %) ticker))
                         (sort-by :date)
                         (map #(-> % :date .toString)))
                    (as-> records
                          (t/debug {:ticker ticker :first-date (first records) :last-date (last records)})))
               ))
      )))

(defn slurp-macrotrends-website-html! [ticker]
  (slurp (str "https://www.macrotrends.net/assets/php/fundamental_iframe.php?t=" ticker "&type=revenue&statement=income-statement&freq=Q")))

(def global-exchanges
  {:NYSE {:slurp-url "https://www.advfn.com/nyse/newyorkstockexchange.asp?companies="
          :reg-expr #"https\:\/\/www\.advfn\.com\/stock\-market\/NYSE\/([A-Z]*)\/stock\-price"}
   :NASDAQ {:slurp-url "https://www.advfn.com/nasdaq/nasdaq.asp?companies="
            :reg-expr #"https\:\/\/www\.advfn\.com\/stock\-market\/NASDAQ\/([A-Z]*)\/stock\-price"}
   :AMEX {:slurp-url "https://www.advfn.com/amex/americanstockexchange.asp?companies="
          :reg-expr #"https\:\/\/www\.advfn\.com\/stock\-market\/AMEX\/([A-Z]*)\/stock\-price"}})

(defn extract-stock-tickers [reg-expr html-data]
  (->> html-data
       (re-seq reg-expr)
       (map second)
       distinct))

(comment

  (http/get "https://www.advfn.com/nasdaq/nasdaq.asp?companies=Z"
             )
  )


(defn slurp-and-extract-ADVFN-listings-by-letter! [url regex letter]
(->>
   (str url letter)
   #_(slurp)


   (http/get)

   
      
      
       (extract-stock-tickers regex)))

(defn submit-tx-and-await!
  "(submit-tx) will return before the transaction is finalised. This method will
  only return after awaiting that the transaction is finalised.

  Guidance:
  for kaocha tests, this is necessary, otherwise xtdb will return empty query"
  [db-node txn-data]
  (t/trace {:db-node db-node :txn-data txn-data})
  (->> txn-data
       (xt/submit-tx db-node)
       ;;this was the old way we controlled for waiting for XTDB
       #_(::xt/tx-id)
       #_(xt/await-tx db-node)
       )

  #_(Thread/sleep 1000)
  (xt/sync db-node))


(defn prepare-xt-ticker-records
  [exchange ticker ticker-records]
  (let [has-failed (and (map? ticker-records) (= :reason (some-> ticker-records keys second)))
        prefix (str (-> exchange name) ":")]
    (if has-failed
      [[::xt/put
        (assoc ticker-records
               :xt/id (str prefix ticker "-metadata")
               :exchange exchange
               )]]
      (as-> ticker-records x
        (map #(assoc %
                     :xt/id (str prefix (:symbol %) "-" (some-> % :date ld/to-string))
                     :exchange exchange
                     ) x)
        (mapv #(vector ::xt/put %) x)
        (conj x [::xt/put
                 {:xt/id  (str prefix ticker "-metadata")
                  :exists true
                  :exchange exchange
                  :symbol ticker}])))))





(def root-path
  ;; Daniel MacOS rocksDB address
  "/Users/dmg46664/OneDrive/StonkScreenerShared/rocksdb/"

  ;; Julian Linux (On WSL Windows Laptop) rocksDB adress
  #_"/mnt/c/Users/jgers/OneDrive/StonkScreenerShared/rocksdb/"

  ;; Julian MacOS rocksDB address
  #_"/Users/juliangerson/Library/CloudStorage/OneDrive-Personal/StonkScreenerShared/rocksdb/"

   ;; Julian MacOS - Temp testing directory
  #_"/Users/juliangerson/projects/stonkscreener/ignored/tempStonkscreenerDB"

  ;; Julian Windows WSL - Temp testing directory
  #_"/mnt/c/Users/jgers/Documents/ProjectsCode/temp/tempStonkscreenerDB/")


(def rock-db-config {:xtdb/index-store {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                                                   :db-dir (str root-path "indexstore")}}
                     :xtdb/document-store {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                                                      :db-dir (str root-path "documentstore")}}
                     :xtdb/tx-log {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                                              :db-dir (str root-path "logstore")}}})

(defn extract-and-prepare-by-letter [exchange slurp-and-extract-fn slurp-ticker-records-fn config letter]
  (let [tickers-list (slurp-and-extract-fn letter)]
    (when (empty? tickers-list) (throw (ex-info
                                   "No results for tickers-list. Suggestion: Has the URL of the exchange regex changed?"
                                   {:exchange exchange :letter letter})))
    (-> (for [ticker tickers-list]
          (-> ticker
              (slurp-ticker-records-fn)
              (doto (as-> _ (when (:production config) (Thread/sleep 2000))))
              (->>
               (extract-revenue-records ticker)
               (prepare-xt-ticker-records exchange ticker))))
        (conj [[::xt/put {:xt/id (str (-> exchange name)
                                      (some-> letter cs/upper-case keyword))
                          :exchange exchange
                          :list  tickers-list}]]))))

(defn extract-and-save-by-letter! [db-node exchange slurp-and-extract-fn slurp-ticker-records-fn config letter]
  (-> (extract-and-prepare-by-letter exchange slurp-and-extract-fn slurp-ticker-records-fn config letter)
      (->> (mapv #(submit-tx-and-await! db-node %)))
      (doto (as-> _ (t/info (str "Finished saving to XTDB for letter: " letter " For Exchange: " exchange))))))

(defn revenue-results-to-vegalite
  "converts revenue record query results -> displayable vega lite"
  [results]
  (->>
   results
   (map first)
   (map #(assoc %
                :date (some-> % :date ld/to-string)
                :value (max 0 (:value %))))))


(defn configure-live-db! []
  #_{:clj-kondo/ignore [:inline-def]}
  (def stk-nodeg (xt/start-node dimigi.extraction/rock-db-config)))

(defn setup-extract-config! []
  ;;set Timbre file log appender
  (t/merge-config!
   {:appenders {:spit (appenders/spit-appender {:fname (str "log/" (ld/now) "-stonkscreener.log")})}})

  (when (= (some-> stk-node xt/status :xtdb.kv/kv-store) "xtdb.mem_kv.MemKv")
    (configure-live-db!)
    )
  
  (t/info "config setup completed.")
  )

(defn extract-macrotrends! [exchange config letter]
  (when (not stk-node)
    (throw (ex-message "stk-node hasn't been defined yet!")))
  (t/info (str "letter worked on is: " letter " from exchange: " exchange))
    ;;Extract by single letter only
  (extract-and-save-by-letter!
   stk-node
   exchange
   #(slurp-and-extract-ADVFN-listings-by-letter!
     (-> global-exchanges exchange :slurp-url)
     (-> global-exchanges exchange :reg-expr)
     %)
   slurp-macrotrends-website-html!
   config
   letter)
  )

(defn char-range [start end]
  (let [start-char (if (string? start)
                     (->> start .toString .toUpperCase char-array first)
                     start)
        end-char (if (string? end)
                   (->> end .toString .toUpperCase char-array first)
                   end)]
    (map char (range (int start-char) (inc (int end-char))))
    )
  )

(defn extract-macrotrends-range! [exch-list config alphabet-range]
  (t/info (str ":xtdb.kv/kv-store is: " (some-> stk-node xt/status :xtdb.kv/kv-store)))
  (t/info (str "Root-Path set is: " root-path))
  (t/info "Extracting all ticker-data by letter process is starting...please wait...")
  (when (false? (->> exch-list
                     (map #(contains? global-exchanges %))
                     (every? true?)))
    (throw (ex-info "exchanges-list provided contains Exchanges that don't exist in global variable: " {:global-variable-exchanges (keys global-exchanges) :exchanges-list-provided exch-list})))
  (doseq [exchange exch-list]
    (doseq
     [letter (->> alphabet-range
                  (map #(->> %
                             .toString
                             .toUpperCase)))]
      (extract-macrotends! exchange config letter)
      (Thread/sleep 7000) ;7 seconds
      )
    )
  )

(defn exec-all-exchanges-all-letters! [& _args]
  (setup-extract-config!)
  (t/set-level! :debug)

  ;; example...
  (extract-macrotrends-range! [:NYSE :AMEX :NASDAQ] {:production true} (char-range \A \Z))
  (t/info "exec-all-exchanges-all-letters! has now finished."))

(defn -main
  {:org.babashka/cli {:coerce {:exchanges []
                               :char-range []}
                      :require [:exchanges :char-range]}}
  [& args]

  (let [[{exchanges :exchanges
          input-l :char-range}] args
        letters (char-range (first input-l) (last input-l))]
    (cond
      (empty? exchanges) (throw (ex-message "You didn't add any exchanges."))
      (empty? letters) (throw (ex-message "You didn't add any letter/letters")))
    #_(setup-extract-config!)
    (t/set-level! :debug)
    #_(extract-macrotrends-range!
     (map #(keyword %) exchanges)
     {:production true}
     letters)
    (t/info "The -main fn has now finished.")))

;;This is where you would run the main slurps. Be careful of MacroTrends
(comment 
  
  (do
    (setup-extract-config!)
    (t/set-level! :debug)
    (extract-macrotrends-range!  [:NYSE])
     {}
     ["letters"]))
  
  (setup-extract-config!)
  
  ;;Set Debug to on
  (t/set-level! :debug)
  ;;Set Info to on
  (t/set-level! :info)
  
  ;;Run this to extract and save a single letter from macrotrends
  (extract-macrotrends! :NASDAQ {:production true} "Z")

  ;;Run this to extract and save B to Z from macrotrends
  (extract-macrotrends-range! [:NYSE :AMEX :NASDAQ] {:production true} (char-range \B \Z))

  (let [tickers-list (slurp-and-extract-ADVFN-listings-by-letter!
                      (-> global-exchanges :NYSE :slurp-url)
                      (-> global-exchanges :NYSE :reg-expr)
                      "H")]
    (->> tickers-list
         #_
         (filter #{"PHIN"})))

  )

;;Debug Area:
(comment
  (let [ticker "IBEX"
        slurp-ticker-records-fn slurp-macrotrends-website-html!]
    (->> ticker
         (slurp-ticker-records-fn)
         (extract-revenue-records ticker)
         (prepare-xt-ticker-records :NASDAQ ticker)
         #_(submit-tx-and-await! stk-node))) 

  (iterate inc 5)
  (+ 3 3)

  )

(comment

  (http/get "https://www.advfn.com/nasdaq/nasdaq.asp?companies=Z"
            )

  #_
  
  (-> (sh "curl" "--silent"
          "--no-buffer" "--unix-socket"
          ;; "/var/run/docker.sock"
          "https://www.advfn.com/nasdaq/nasdaq.asp?companies=Z"

  ;;QUERY XTDB SECTION

  (let [query-result (xt/q (xt/db stk-node)
                           '{:find
                             [?e]
                             :where [[?e :xt/id ?everything]]})]

    #_(.close stk-node)
    query-result)

  (xt/q (xt/db stk-node)
        '{:find
          [(pull ?e [*])]
          :where [[?e :list]]})

  (xt/q (xt/db stk-node)
        '{:find
          [(pull ?e [*])]
          :where [[?e :xt/id "ALE-metadata"]]})

  (xt/q (xt/db stk-node)
        '{:find
          [(pull ?e [*])]
          :where [[?e :exists true]]})

  (xt/q (xt/db stk-node)
        '{:find
          [(pull ?e [*])]
          :where [[?e :symbol "ZETA"]
                  [?e :type :qrt-revenue]]})

  (xt/q (xt/db stk-node)
        '{:find
          [(pull ?e [*])]
          :where [[?e :exchange :NASDAQ]
                  [?e :type :qrt-revenue]]})

  (xt/q (xt/db stk-node)
        '{:find
          [(pull ?e [*])]
          :where [[?e :xt/id #{"BLDR-2022-06-30"}]]})

  (xt/q (xt/db stk-node)
        '{:find
          [(pull ?e [*])]
          :where [[?e :xt/id #{"PAR-2021-03-31"
                               "PAR-2021-06-30"}]]}))
