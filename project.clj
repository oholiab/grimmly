(defproject grimmly "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [pandect "0.5.4"]
                 [http-kit "2.1.18"]]
  :main ^:skip-aot grimmly.core
  :target-path "target/%s"
  :plugins [[cider/cider-nrepl "0.9.1"]]
  :profiles {:uberjar {:aot :all}})
