(defproject grimmly "0.1.1"
  :description "A URL shortener and weechat plugin"
  :url "http://github.com/oholiab/grimmly"
  :license {:name "BSD 3 Clause"
            :url "https://opensource.org/licenses/BSD-3-Clause"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [environ "1.0.1"]
                 [pandect "0.5.4"]
                 [http-kit "2.1.18"]]
  :main ^:skip-aot grimmly.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
