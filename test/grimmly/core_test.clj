(ns grimmly.core-test
  (:require [clojure.test :refer :all]
            [grimmly.core :refer :all]))

(deftest test-adding-items
  (testing "Items can be added"
    (dosync (ref-set inventory []))
    (is (= [] @inventory))
    (update-inventory "bleh" "blah")
    (is (= [["bleh" "blah"]] @inventory))))

(deftest test-inventory-limit
  (testing "Items can be added up to the limit"
    (dosync (ref-set inventory []))
    (dotimes [n inventory-size] (update-inventory (str "key " n) (str n " is the value")))
    (is (= ["key 0" "0 is the value"] (first @inventory)))
    (is (= ["key 49" "49 is the value"] (last @inventory)))
    (update-inventory (str "key " 50) (str 50 " is the value"))
    (is (= ["key 1" "1 is the value"] (first @inventory)))
    (is (= ["key 50" "50 is the value"] (last @inventory)))))

(deftest test-fetch-record
  (testing "can fetch a named record"
    (dosync (ref-set inventory []))
    (dotimes [n inventory-size] (update-inventory (str "key " n) (str n " is the value")))
    (is (= "23 is the value" (fetch-record "key 23")))))

(deftest test-redirect-record
  (testing "404s when there is no record"
    (dosync (ref-set inventory []))
    (is (= {:status 404} (redirect-record {:uri "/blarney"}))))
  (testing "302s when there is a record"
    (dosync (ref-set inventory [["blooney" "www.blooney.com"]]))
    (let [resp (redirect-record {:uri "/blooney"})]
      (is (= 302 (:status resp)))
      (is (= {"Location" "www.blooney.com"} (:headers resp))))))
