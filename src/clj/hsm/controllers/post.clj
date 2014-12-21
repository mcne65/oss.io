(ns hsm.controllers.post
  (:require [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [cheshire.core :refer :all]
            [ring.util.response :as resp]
            [hsm.actions :as actions]
            [hsm.pipe.event :as event-pipe]
            [hsm.utils :as utils :refer [json-resp host-of body-of whois]]))

(defn create-link
  [[db event-chan] request] 
  (log/warn request)
  (let [host  (host-of request)
        body (body-of request)
        user (whois request)
        link-data (utils/mapkeyw body)]
    (actions/create-link db link-data user)
    (event-pipe/create-link event-chan (apply merge {:user user} link-data))
    (json-resp { :ok body })))

(defn upvote-link
  [[db event-chan] request]
  (let [host  (host-of request)
        body (body-of request)
        link-id (BigInteger. (get-in request [:route-params :id]))
        user (whois request)]
        (actions/upvote-post db link-id user)
        (event-pipe/upvote-link event-chan {:user user :id link-id})
        (json-resp {:ok 1})
        ))

(defn show-link
  [[db event-chan] request]
  (let [host  (host-of request)
        body (body-of request)
        link-id (BigInteger. (get-in request [:route-params :id]))
        user (whois request)]
    (json-resp (actions/get-link db link-id user))))


(defn list-links
  [[db event-chan] request]
  (let [host  (host-of request)
    body (body-of request)
    time-filter (get-in request [:route-params :date])
    user (whois request)]
    (json-resp (actions/list-links db time-filter user))
    ))
