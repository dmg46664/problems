(ns promesa-zoo
  (:require
   [applied-science.js-interop :as j]
   [promesa.core :as p]
   [nbb.core :refer [await]]))


;; This is a contrived example of a very typical layered use case of async functions called with
;; some tolerance and logging.

;; I wrote the following to try and figure this out
;; for some database calls, but thought to do it as a contrived example first. I thought it would be good to post it publicly for comment,
;; to help anyone in the same bind, and have for future reference. It's enough to get me going, but is partially incomplete.

;; The use case is:
;; - Visit the zoo call
;; - Check all cat cages are closed (a series of async calls)
;; - If lion cage open call lion tamer (async)
;; - propogate error up to visit zoo call and return zoo closed for maintenance, logging appropriately.

;; I started with:

(defn check-lion-cage []
  (p/rejected (ex-info "lion is out, run!" {:fail :lion-cage})))


(defn check-cat-cages []
  (p/let [
          _ (check-lion-cage) ;;TODO call lion-tamer if call fails. TODO Log failure.
          ;; other checks.
          ]
    {:status 200} ;; not used due to error propogation, but need something in case this fn
                  ;; is called elsewhere/debug.
    )
  )

(defn visit-zoo []
  (p/let [
          _ (check-cat-cages) ;; TODO Should fail with "Zoo closed for maintenance."
          ;; other checks...
          ]
    {:status 200
     :message "welcome to the zoo"}
    ))

;; Presumably I use p/catch, however it's not obvious from the documentation of p/catch
;; whether the error is propogated or not. I.e. if the last line is an error is the
;; returning promise rejected? I note that, not in the user guide, but in the api guide
;; it states a resolved promise is returned, but this stuff is strange enough that a
;; newcomer has to think the corner cases in the javascript model and then through the
;; promesa model, whilest none of this stuff is intuitive (coming from a
;; Java try/catch/finally way of thinking).

;; I came up with the following:

(defn check-cat-cages []
  (->
   (p/let [
           _ (check-lion-cage) 
           ;; other checks.
           ]
     {:status 200} ;; not used due to error propogation, but presumably need something in case used differently.
     )
   (p/catch (fn [error]
              (js/console.log (str error)) ;; log error? Or perhaps this should bubble up to visit-zoo for logging.
              ;; Makes sense to log in line in the case where a recovery is made.
              (case (some-> error
                            (j/get :data nil)
                            :fail)
                ;;TODO call lion-tamer here. Not going to fix this, but presumably if we wanted to recover and call the rest
                ;; of check-cat-cages the catch should happen in check-lion-cages.
                :lion-cage (throw error) ;;

                ;; I guess there is not much value in wrapping this unless there is extra info.
                (throw (ex-info "Unexpected failure" {} error))
                )))
   )
  )

(defn visit-zoo []
  (->
   (p/let [
           _ (check-cat-cages)
           ;; other checks...
           ]
     {:status 200
      :message "welcome to the zoo"}
     )
   (p/catch (fn [error]
              ;; This is not needed but it's here just to demonstrate that you can put a
              ;; promise in the catch handler, without landing up in a babushka promise
              ;; situation (i.e. it's unpacked)
              (p/resolved
               {:status 503
                :message "Zoo closed for maintenance"})
              ))
   ))


(comment
  (await (visit-zoo))
  (await (check-cat-cages))
  (await (check-lion-cage))
  )
