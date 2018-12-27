(defproject attempt-one "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.6.1"]
                 [ring-json-response "0.2.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.3.2"]
                 [org.clojure/java.jdbc "0.7.7"]]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler attempt-one.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
