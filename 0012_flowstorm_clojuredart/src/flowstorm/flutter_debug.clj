;; Connect to repl, and load namespace

(ns flowstorm.flutter-debug
  (:require [cljd.build])
  )

(comment

  ;; load debugger
  :dbg

  ;; We should catch when trying to compile (ignore outdated flutter packages)
  (cljd.build/-main "watch")

  ;; Check the Repl window if using Cider as terminal output redirected there.

  ;; Why doesn't this catch the clojure code compiling the file please_convert.cljd ??

  )
