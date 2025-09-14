
;; Connect to repl, and load namespace

(ns flowstorm.flutter-debug
  (:require [cljd.build])
  )


(defn clean-sym-meta
  "For removing {:tag PersistentVector} from the symbol meta of (def sym ...)"
  [def-exp]
  (let [[defx vecx & restx] def-exp
        clean-meta (dissoc (meta vecx) :tag)]
    (concat
       [defx (with-meta
               vecx
               clean-meta)]
       restx)))

(comment

  user/macro-form

  (clean-sym-meta user/x-boostrap-def)

  *e

  (name 'vec)

  ;; load debugger
  :dbg

  ;; We should catch when trying to compile (ignore outdated flutter packages)
  (cljd.build/-main "watch")

  ;; Check the Repl window if using Cider as terminal output redirected there.

  ;; Why doesn't this catch the clojure code compiling the file please_convert.cljd ??
  )
