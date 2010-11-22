(defproject planet "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.2.0"]
		 [org.clojure/clojure-contrib "1.2.0"]
		 [compojure "0.4.1"]
		 [ring/ring-jetty-adapter "0.2.3"]
		 [ring/ring-devel "0.2.3"]
		 [enlive "1.0.0-SNAPSHOT"]
		 [prolefeed "0.1-SNAPSHOT"]
		 [http.async.client "0.2.0"]
		 [clj-tagsoup "0.1.2"]]
  :dev-dependencies [[swank-clojure "1.2.1"]]
  :main planet
)