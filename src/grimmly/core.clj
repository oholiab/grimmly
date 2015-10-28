(ns grimmly.core
  (:gen-class))

(require '[org.httpkit.server :as serv]
         '[clojure.string :as string]
         '[pandect.algo.sha1 :as alg])

(def inventory (ref (array-map)))
(def inventory-size 5)

(defn update-inventory
  "Adds a mapping to the inventory and rotates out old members if the size limit
  has been reached"
  [k, v]
  (dosync  
    (ref-set inventory (if (= inventory-size (count @inventory)) 
                         ;(assoc (drop-last @inventory) k v) 
                         (assoc (apply array-map (flatten (rest @inventory))) k v)
                         (assoc @inventory k v)))))

(defn shakey
  "Return a short sha from passed in string"
  [s]
  (string/join "" (take 5 (alg/sha1 s))))

(defn req-to-body-sha
  "Turns the uri from a req into a sha"
  [req]
  (shakey (string/join "" (rest (str (-> req :body))))))

(defn add-record
  "Adds record to the inventory"
  [req]
  (println req)
  (update-inventory (req-to-body-sha req) (apply str (map char (.bytes (-> req :body)))))
  (println @inventory)
  {:status  200
   :headers {"Content-Type" "text/css"}
   :body (req-to-body-sha req)})

(defn redirect-record
  "Redirects to the given record or 404s"
  [req]
  (println req)
  {:status 302
   :headers {"Location" "https://grimmwa.re"}})

(defn reply 
  "Just do a simple http response"
  [req]
  (let [method (-> req :request-method)]
    (println method)
    (case method
      :post (add-record req)
      :get  (redirect-record req)
      (redirect-record "404"))))


(defn -main
  "Start up the app and keep it going"
  [& args]
  (println "Starting up...")
  (serv/run-server reply {:port 8080}))
