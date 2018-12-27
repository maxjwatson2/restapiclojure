(ns attempt-one.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.java.jdbc :refer :all]
            [ring.util.response :refer :all #_[response]]
            [ring.middleware.json-response :refer :all #_[wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))


(use 'ring.middleware.json-response
     'ring.util.response)

;;Customers should have 5 fields ID, Name, location, customer profile and misc.

#_(
    POST http://localhost:3000/cust?name="Sam Walters"&profile="Bad back problems"&misc="Hates apples"&zip=14324
    POST http://localhost:3000/cust?name=Jon&zip=12345&proflie="nothing"&misc="Nothing"
    POST http://localhost:3000/train?name="Bill Hill"&profile="Bad back problems"&zip=93125
 ;;   GET http://localhost:3000/cust/1?
    PUT http://localhost:3000/cust?:id=0&name="guy"&zip=43423&proflie="Bad diet"&misc="Enjoys ham"
    DELETE http://localhost:3000/cust?:id=0
    PUT http://localhost:3000/trainCust?custID=1&trainerID=1
    GET http://localhost:3000/trainCust?trainerID=1

    Example postman commands.




    )


(def db-spec {:dbtype "mysql"
              :dbname "messagedb"
              :user "mwatson"
              :password "Testmysqlpassword1"
              :host "localhost"
              :port "3333"
              })

(def customers (atom {}))

(def trainers (atom {}))

(def customerCount (atom 0))

(def trainerCount (atom 0))

(defn frontPage [x](str "Hello, "x))

(defn get-name [the-map] ;; This level of abstraction isn't really needed at the moment but at some point in time it maybe, for example if two people have the same name.
  (:name the-map)
  )

(defn get-zip [the-map]
  (:zip the-map)
  )

(defn get-profile [the-map]
  (:profile the-map)
  )

(defn get-misc [the-map]
  (:misc the-map)
  )


(defn make-new-customer-list [the-map]
  []
  )


(defn get-trainers-within-range [customer specs]
  ;; This will do a pull from SQL and filter by range and perhaps other specifications which will be sent as specs.
  )

(defn getTrainerForCustomer [customer criteria]);; finds a trainer for a specific customer. following certian criteria.


(defn make-trainers-string [trainer-map]
  {:name (get-name trainer-map) :zip (get-zip trainer-map) :profile (get-profile trainer-map) :custMap (make-new-customer-list trainer-map)}
   )

(defn addTrainer [trainer]
  (swap! trainerCount inc)
  (reset! trainers (swap! trainers #(merge-with merge % {@trainerCount (make-trainers-string trainer)})) )
  (println "ADD A TRAINER")
  (println @trainers)
  )

(defn updateTrainer [trainer-id updated-map]
  (reset! @trainers (assoc @trainers trainer-id (make-trainers-string updated-map))))

(defn removeTrainer [trainer-id]
  (reset! @trainers (dissoc @trainers trainer-id)))

(defn getTrainerById [trainer-id]
  (@trainers trainer-id)
  )

(defn make-customer-string [customer-map]
  {:name (get-name customer-map) :zip (get-zip customer-map) :profile (get-profile customer-map) :misc (get-misc customer-map)})

(defn addCustomer [customer]
  (println "THIS IS ADD CUSTOMER")
  (println @customers)
  (swap! customerCount inc)
  (reset! customers (swap! customers #(merge-with merge % {@customerCount (make-customer-string customer)})))
  )

(defn getCustomerById [id]
  (println "get customer by id")
  (println (@customers (Integer/parseInt (:id id))))
  (wrap-json-response (response (@customers (Integer/parseInt (:id id)))))
  )


(defn updateCustomer [customer-id updated-map]
  (reset! customers (assoc @customers customer-id (make-customer-string updated-map)))
  (println "update customer")
  (println @customers)
  )

(defn removeCustomer [customer-id]
  (reset! customers (dissoc @customers customer-id))
  (println "remove customer")
  (println @customers)
  )

(defn giveCustomer [customer-id trainer-id]
  (println "In give customer")
  (let [updated-trainer (assoc (@trainers (Integer/parseInt trainer-id)) :custMap (conj (:custMap (@trainers (Integer/parseInt trainer-id))) (Integer/parseInt customer-id)))]
    (println updated-trainer)
  (reset! trainers (assoc @trainers (Integer/parseInt trainer-id) updated-trainer ))
  ))

(defn getTrainersCustomers [trainer-id]
  (let [customer-keys (:custMap (@trainers (Integer/parseInt trainer-id)))]
    (let [all-customers (loop [customer-map {}]
    (if (> (count customer-keys) (count customer-map) )
      (recur (assoc customer-map (customer-keys (count customer-map)) (@customers (customer-keys (count customer-map)))))
     customer-map))]
      (println all-customers)
      (wrap-json-response (response all-customers))
      )))


(defroutes app-routes
           (GET "/" [] (frontPage "Steven"))
           ;; Customer routes
           (GET "/cust/:id" [id :as request] (getCustomerById (:params request)))
             ;;(frontPage request)
              ;;(wrap-json-response)
           (POST "/cust" [param :as request] (addCustomer (:params request)))
           (PUT "/cust"[param :as request] (updateCustomer ((:params request) ":id" ) (:params request)))
           (DELETE "/cust"[params :as request] (removeCustomer (:id (:params request))))
           ;;Trainer routes

           (GET "/train" [param :as request] (getTrainerById (:id (:params request))))
           (POST "/train" [param :as request] (addTrainer (:params request)))
           (PUT "/train"[param :as request] (updateTrainer (:id(:params request)) (:params request)))
           (DELETE "/train"[params :as request] (removeTrainer (:id (:params request))))

           (PUT "/trainCust" [params :as request] (giveCustomer (:custID (:params request)) (:trainerID (:params request))))
           (GET "/trainCust" [params :as request] (getTrainersCustomers (:trainerID (:params request))))

           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))
