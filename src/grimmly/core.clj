(ns grimmly.core
  (:gen-class))

(require '[org.httpkit.server :as serv]
         '[pandect.algo.sha1 :as alg]
         '[environ.core :refer [env]])

(def inventory (ref (array-map)))

; Filter out unneeded environment variables for security
(def propnames
  '(
    :ip
    :port
    :buffsize
    ))

(defn getkeyvalue [hashmap key]
  (let [value (get hashmap key nil)]
    (if (nil? value) {} {key value})))

(def properties (apply merge (map #(getkeyvalue env %) propnames)))

; Set default properties
(def defaults
  {:ip "127.0.0.1",
   :port 8080,
   :bufsize 10})

(def inventory-size (:buffsize (merge defaults properties)))

; The code
(defn update-inventory
  "Adds a mapping to the inventory and rotates out old members if the size limit
  has been reached"
  [k, v]
  (prn (str "Adding " k "=" v))
  (dosync  
    (ref-set inventory (if (= inventory-size (count @inventory)) 
                         (assoc (apply array-map (flatten (rest @inventory))) k v)
                         (assoc @inventory k v)))))

(defn shakey
  "Return a short sha from passed in string"
  [s]
  (apply str (take 5 (alg/sha1 s))))

(defn req-body-to-str
  "Takes the stupid httpkit message body format and turns it to a string"
  [req]
  (apply str (map char (.bytes (-> req :body)))))

(defn req-to-body-sha
  "Turns the uri from a req into a sha"
  [req]
  (shakey (req-body-to-str req)))

(defn add-record
  "Adds record to the inventory"
  [req]
  (update-inventory (req-to-body-sha req) (req-body-to-str req))
  {:status  200
   :headers {"Content-Type" "text/css"}
   :body (req-to-body-sha req)})

(defn truncate-uri
  "Remove leading / from string"
  [s]
  (apply str (rest s)))

(defn redirect-record
  "Redirects to the given record or 404s"
  [req]
  (prn (str "Request for " (-> req :uri)))
  (let [resp (get @inventory (truncate-uri (-> req :uri)))]
    (if (nil? resp)
      {:status 404}
      {:status 302
       :headers {"Location" resp}})))

(defn reply 
  "Routes requests"
  [req]
  (let [method (-> req :request-method)]
    (case method
      :post (add-record req)
      :get  (redirect-record req)
      (redirect-record "404"))))

(defn -main
  "Start up the app and keep it going"
  [& args]
  (prn "App started.")
  (let [options (merge defaults properties)]
    (prn "Using options " options)
    (serv/run-server reply options)))
