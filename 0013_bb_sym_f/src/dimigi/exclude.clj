(ns dimigi.exclude
  (:require [tick.core :as t]))

(comment
  (require '[clojure.tools.deps.alpha.repl :refer [add-libs]])
  (+ 1 1)
  )

(def exclude
  (->
   {(t/date "2024-11-27")
    [["GERN" :NASDAQ :medical] ;; https://www.youtube.com/watch?v=vjHtks2ajq4
     ]

    (t/date "2024-10-18")
    [["CMTL" :NASDAQ :satellite :telecoms] ;; https://www.youtube.com/watch?v=X5N9y6llRmY
     ["ATS" :NYSE :#B :manufacture :automation] ;; https://www.youtube.com/watch?v=vsGCSqmoFQs
     ["T" :NYSE :#B] ;; AT&T!! Fibre?  https://www.youtube.com/watch?v=22LNY3rzNCs
     ]
    (t/date "2024-08-05")
    [["NVDA" :NASDAQ] ;; No explanation needed
     ["MBIN" :NASDAQ :#A :bank] ;; TODO  https://www.youtube.com/watch?v=M0WhugjDhvE (Not Farmers merchant bankcorps)
     ["SHBI" :NASDAQ :bank :#B] ;; TODO interesting?
     ["RKOR" :NASDAQ :analyits "Road safety"]]

    (t/date "2024-07-27")
    [["ADMA" :NASDAQ] ;; https://www.youtube.com/watch?v=H-5o3ZtTfs0
     ["FGF" :NASDAQ] ;; reinsurance
     ["AXIL" :AMEX :#B] ;; Axil ear protection https://www.youtube.com/watch?v=7s5VuIkM4FI
     ["INSP" :NYSE :#A :medical] ;; https://www.youtube.com/watch?v=Sw2ml_4EKgA
     ["LMFA" :NASDAQ :charity]
     ["WBD" :NASDAQ :media :? :#B] ;; TODO Lots of revenue, but no profit?? https://www.youtube.com/watch?v=5zNvr9TMAG4
     ["DISCB" :NASDAQ "same as above"]
     ["DISCK" :NASDAQ "same"]
     ["NFE" :NASDAQ :energy]
     ["PR" :NYSE :energy] ;; TODO Flagged as NASDAQ bug wrong.
     ["DSGR" :NASDAQ :#B :distribution] ;; https://www.youtube.com/watch?v=kbfO8MEQ28M
     ["ACMR" :NASDAQ :silicon] ;; https://www.youtube.com/watch?v=tc0SGEXPIdw
     ["LSAK" :NASDAQ :software] ;; south african software.
     ]
    (t/date "2024-06-26")
    [["STR" :NYSE :royalty] ;; 
     ["FAT" :NASDAQ :fraud] ;; This is educational. Think about BALY https://www.youtube.com/watch?v=1N6V8E4jscA
     ["CCB" :NASDAQ :#A] ;; https://www.youtube.com/watch?v=4XZ8yPlluTM
     ]

    (t/date "2024-06-21")
    [["ALTM" :NYSE :lithium]
     ["AYX" :NYSE] ;; delisted https://www.macrotrends.net/stocks/delisted/AYX/Alteryx/net-worth
     ["BXMT" :NYSE] ;; Carson block short sell  https://www.tradingview.com/symbols/NYSE-BXMT/
     ["AEM" :NYSE :mine] ;; gold mining
     ["ANET" :NYSE :#A] ;; :-(
     ["BLX" :NYSE :bitcoin]
     ["CNX" :NYSE :energy :coal] ;;TODO  https://investors.cnx.com/news-releases/2024/05-15-2024-114532918
     ;; https://www.youtube.com/watch?v=WLA3kYYYmBU
     ["BALY" :NYSE :casino :#A]
     ["GTLS" :NYSE :#A] ;; fund https://www.youtube.com/watch?v=zv9buJvdbwM
     ;; ceo https://www.youtube.com/watch?v=UGg8ZfVHaYU
     ;; Image of product https://chartcity.chartindustries.com/
     ;; -----
     ["ABR" :NYSE :reit :#A] ;;Why is it a reit? loan origination https://www.youtube.com/watch?v=OIfTdNxm46s
     ["BBAR" :NYSE :bank] ;; Argentine bank that we say in Spain!
     ["APO" :NYSE :investment :#A] ;;Very interesting company  https://www.youtube.com/watch?v=HaQEH7xe3bM
     ]

    (t/date "2024-03-07")
    [["POOL" :NASDAQ :retail "Swimming pools"]
     ["KMX" :NYSE "Carmax" :vehicle]
     ["PIXY" :NASDAQ :gig :services :staffing "Staffing"]
     ["DHI" :NYSE  :construction :housing] ;; https://www.youtube.com/watch?v=QK7pRK1NMTY
     ]

    (t/date "2024-02-27")
    [["SAR" :NYSE :fund :fin :recession-risk] ;; https://www.youtube.com/watch?v=j1OeXBL-KJU
     ["MCRI" :NASDAQ :#C :casino :hotel] ;; TODO Do earnings call ;; https://www.youtube.com/watch?v=YlK9rsiTa5w
     ["CALX" :NYSE :#A :cloud :broadband] ;; TODO earnings call  ;; https://www.youtube.com/watch?v=GtYnnTyegV0
     ["WGO" :NYSE :#B :vehicle "Winnebagos!!!"] ;; TODO earnings call
     ["LBRT" :NYSE :energy "Second biggest natural gas provider"] ;; https://youtu.be/oNKAZsPpCcw
     ["COLB" :NASDAQ :bank]
     ["WBS" :NYSE :bank]
     ["PLD" :NYSE :#B :solar :reit :logistics :growth :value "Prologis"] ;; mad money https://www.youtube.com/watch?v=4QSiSGU_vVY
     ;; https://www.youtube.com/watch?v=_Of78OqVOus Good talk about difference in growth metrics.
     ["SCHW" :NYSE :finance "Charles shwab"] ;; No introduction needed. Established.
     ["STLD" :NASDAQ :#B :materials :resource "Dynamic Steel" :update "Getting into ALU from STL"] ;; Great story https://www.youtube.com/watch?v=jaV4-0BhJKQ (see comments on link)
     ;; conference call https://www.youtube.com/watch?v=hCiCZZG-PFY
     ]

    (t/date "2023-09-10")
    [["NOG" :NYSE :energy "Montana oil"]
     ["NOG" :AMEX :energy "Montana oil"]
     ["SPRO" :NASDAQ :health] ;; https://www.tradingview.com/symbols/NASDAQ-SPRO/ https://www.youtube.com/watch?v=YdGO_02WG3Q
     ["USEG" :NASDAQ :energy "oil"]
     ["DGLY" :NASDAQ  "security cameras"]
     ["ICLR" :NASDAQ "Icon pharmaceutical"] ;; Interesting, but ultimately a consultancy.
     ]
    (t/date "2023-08-29")
    [["YNDX" :NASDAQ :search] ;; https://www.reuters.com/business/media-telecom/yandex-granted-nasdaq-lifeline-subject-russia-restructuring-2023-06-08/
     ["FRD" :AMEX :steel] ;; https://www.youtube.com/watch?v=q_HoewG9dsA
     ]

    (t/date "2023-08-26")
    [["TSLA" :NASDAQ :vehicle] ;; No extra commentary needed...
     ["LNG" :AMEX :#B :energy] ;; If we're going to investigate energy, maybe start here.
     ["CVE" :NYSE :energy "Canadian energy company"]
     ["ZYXI" :NASDAQ :medical "pain management"]
     ["EPM" :NYSE :energy]
     ["EGY" :NYSE :energy]
     ["CVNA" :NYSE :car] ;; It actually came up! :-)
     ["RC" :NYSE :reit :#A] ;; https://www.youtube.com/watch?v=4LgWyKIBtDE Asset increase not quite as impressive.
     ["CLFD" :NASDAQ :energy]
     ["MTDR" :NYSE :resource :energy] ;;couldn't find anything really about the company https://youtu.be/paOhANA9CrQ?t=333
     ["OKTA" :NASDAQ :#A] ;; https://www.youtube.com/watch?v=wVrRobClDEc
     ]

    (t/date "2023-08-16")
    [["MGNI" :NASDAQ :advertising :tech] ;; https://wsw.com/webcast/needham131/mgni/2239885
     ["BOXL" :NASDAQ :education]
     ["AULT" :NASDAQ :defense :micro]]

    (t/date "2023-08-14")
    [["ESEA" :NASDAQ :shipping :#A "8% dividend 2 P/E!!!"] ;; https://www.youtube.com/watch?v=P_NkLUi6ykY
     ;; sector  https://www.youtube.com/watch?v=r7c2duYBnok
     ["SACH" :AMEX :realestate :#A "loans to real estate investors"] ;; https://www.tradingview.com/symbols/AMEX-SACH/financials-overview/ https://www.youtube.com/watch?v=liQ3xeO0NN0
     ["VERI" :NASDAQ :ai] ;; https://www.youtube.com/watch?v=9Gwc0o6ZQxw
     ["SWN" :NYSE :energy]
     ["SQM" :NYSE :chemical "world's biggest lithium producer"] ;; https://www.youtube.com/watch?v=uvFMNROGiVk  https://www.tradingview.com/symbols/NYSE-SQM/
     ]
    (t/date "2023-08-08")
    [["OPHC" :NASDAQ :#A "jewish regional florida bank"] ;; https://www.youtube.com/watch?v=xrDPNo5fCJQ
     ["IIPR" :NYSE :reit :#B "reit marijuana"] ;; great dividend https://www.youtube.com/watch?v=JjdTV8DsWhs
     ["BEEM" :NASDAQ "solar ev charging ?"] ;; https://www.youtube.com/watch?v=i2hUTAmqGCY
     ["CSSE" :NASDAQ "video content"] ;; https://www.youtube.com/watch?v=-A0KCZQ1RPk
     ["FCNCA" :NASDAQ] ;; Buys silicon valley bank https://www.youtube.com/watch?v=4sywn3EzT7k
     ["PLAG" :AMEX "consumer porcelain"]
     ["ZYME" :NASDAQ :bio "cancer discovery"]
     ["BLNK" :NASDAQ "ev charging network"] ;; net losses growing.
     ["TOON" :AMEX] ;; or NYSE. BAD!!!! https://www.youtube.com/watch?v=GAV_BuQR_YA
     ["FTFT" :NASDAQ :crypto "e-commerce"] ;; not interested
     ["PLUG" :NASDAQ :tech "hydrogen power"] ;; https://www.youtube.com/watch?v=Hklv5pddMbE
     ["REPX" :AMEX :energy "independent exploration"]
     ["VTNR" :NASDAQ :energy] ;;https://www.youtube.com/watch?v=yfgFCeiB0Zg

     ["DRS" :NYSE] ;; double?
     ["DRS" :NASDAQ :#? "military aviation"] ; ;https://www.youtube.com/watch?v=pu1aZWjSVp4
     ]

    (t/date "2023-07-17")
    [["SKY" :NYSE "manufactured homes like huf houses"] ;; https://seekingalpha.com/article/4610030-pros-and-cons-investing-skyline-champion
     ["JEF" :NYSE "Jefferies!! rating agency" :vol :fin]]

    (t/date "2023-02-18")
    [["FANG" :NAS :energy] ;; https://www.youtube.com/watch?v=7So55nEkK24
     ]

    (t/date "2022-10-16")
    [["RVP" :AMEX :med "Cool retractable syringe, but "]
     ["CDNA" :NAS :bio "medical for transplant patients"]
     ["OIG" :NAS :#?] ;; https://www.youtube.com/watch?v=jhrcz-ELhDY
     ["ENPH" :NAS "solar, battery, ev chargers"]
     ["ETSY" :NAS]]

    (t/date "2022-09-26")
    [["SNAP" :NAS]
     ;; https://www.statista.com/statistics/545967/snapchat-app-dau/
     ;; https://www.youtube.com/watch?v=SR0629jsqKE
     ;; https://www.youtube.com/watch?v=MwKL3tLPyuA
     ;; https://www.youtube.com/watch?v=Z9CQuPcop7c
     ["ZG" :NAS "Same as Z !!!!"]
     ["AVEO" :NAS :bio]
     ["RGNX" :NAS :bio :vol]
     ["FLUX" :NAS :#? :small "industrial lithium batteries, improving margins"]
     ["ALNY" :NAS "big pharma"]
     ["BCRX" :NAS :bio :burn "burning cash"]
     ["QDEL" :NAS :med :vol "volatile health diagnostics"]
     ["MELI" :NAS :#A "online commerce in latin america"]
     ["Z" :NAS :real-est :#A]
     ["GNUS" :NAS "marketing company"]
     ["PW" :AMEX :#?]
     ["CELH" :NAS :#A "energy drink"]
     ["BLFS" :NAS :med "freezing applications?"]
     ["REFR" :NAS :small "Too small"]
     ["APPS" :NAS :tech :profit "Conversions platform, pivotted into advertising"]
     ;; 2022-09-26T18:39:13  https://www.youtube.com/watch?v=rAQr7jAxT1k
     ;; https://www.youtube.com/watch?v=s5DMo9PphwM
     ;; acquisition talk https://www.youtube.com/watch?v=bjgPSbkGeLc
     ["RKDA" :NAS :chem :agr :decline]
     ["CLXT" :NAS :food :decline "plant based soy food"]
     ["TPHS" :AMEX "New york real estate, multi family"]
     ["LFMD" :NAS "Teledoc rival"]
     ["PRPH" :NAS :covid :diag]
     ["GRWG" :NAS :equip :agr :#A :growth :decline "unfortunate loss in the last quarter, but amazing growth"]
     ["NOTV" :NAS :health "revenue impressive. margins not so much."]
     ["GHSI" :NAS :bio "Terrible margins"]
     ["BIOC" :NAS :bio "Spinal fluid cancer detection. Competes with liquid biopsies?"]
     ["AUPH" :NAS :bio "autoimmune"]
     ["PRTA" :NAS :bio "alzheimers"]
     ["DVAX" :NAS :bio :#C "Revenue from adjuvant for hepatitis vaccines"]
     ;; https://www.fool.com/earnings/call-transcripts/2022/08/06/dynavax-technologies-dvax-q2-2022-earnings-call-tr/
     ["ASTI" :NAS "solar tech"]
     ["UAVS" :AMEX "drone company, quite unprofitable, even though margins improving"]
     ["NVAX" :NAS "Trading at 1x revenue"]
     ["RIOT" :NAS "crypto"]]

    (t/date "2022-08-01") ;; some of these were 8 months ago, but whatever.
    [;; Energy & minerals
     "MT"
     "AES" ;; interesting https://www.youtube.com/watch?v=szyxhFpf0HI
     ;; https://en.wikipedia.org/wiki/AES_Corporation
     "XOM" ;;exxon mobil
     "EOG"
     "EQT"
     "CTRA"
     "CPE" ;; growth but negative income in 2020 ???
     "CIVI"

     ;; Materials & Manufacturing
     "AVNT" ;;NOTE  https://www.tradingview.com/symbols/NYSE-AVNT/
     "WMS" ;; Don't get it  https://investors.ads-pipe.com/ https://www.tradingview.com/symbols/NYSE-WMS/
     "APD"
     "AVY"
     "ALB"
     "PSX";;phillips
     "DQ" ;;chinese

     "DAN";; automotive
     "GM"
     "F"

     ;; Infrastructure
     "BIP" ;; impressive for the sector

     ;; Medical & health
     "NVTA"
     "NVAX" ;;nasdaq, trading at 1x revenue.

     ;; Growth & tech stocks
     "SQ"
     "SHOP"
     "COUP"

     ;; semiconductors
     "ONTO"

     ;; Insurance
     "AON"
     "SLF"
     "CI" ;; ? Not looking into it yet.
     "KNSL" ;; Amazing growth!

     ;; Financials
     "OPY"
     "KKR" ;; large growth https://www.kkr.com/kkr-today
     "BX" ;;Blackstone

     ;; Crypt

;; Property
     "AXR"
     "PLYM"

     ;;Unconvinced
     "L" ;; Jim cramer coverage https://www.youtube.com/watch?v=LFQKpPzyB6M
     "TV" ;;spike, then negaive. Seems once off.

     ;; For now TODO
     "ABT" ;; abbotts (too large)

     ;; NOTE Interesting!!!
     "ARCO" ;; https://www.tradingview.com/symbols/NYSE-ARCO/
     "IHC" ;; Neonatal ventilators https://www.tradingview.com/symbols/LSE-IHC/

     "TDOC"
     "CLF" ;; Steel producer, but jump in rev is insane! https://www.tradingview.com/chart/?symbol=NYSE%3ACLF
     "CRK" ;; natural gas https://www.tradingview.com/chart/?symbol=NYSE%3ACRK
     "MED" ;;medfast
     "GLOB"]

    (t/date "2022-03-05")
    [["ANTM" "Biggest insurance"]]}
   (vals)
   (->> (apply concat))
   (->> (map #(if (string? %) % (first %))))
   (->> (into #{}))))
