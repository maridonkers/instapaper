(defproject instapaper "0.1.0-SNAPSHOT"
  :description "Tool to remove duplicate links (and their surrounding <li> tags) from Instapaper HTML export."
  :url "https://photonsphere.org/posts-output/2019-02-18-instapaper-export/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [hickory "0.7.1"]]

  :source-paths ["src"]
  :main org.photonsphere.instapaper

  :profiles {:uberjar {:target-path "target/uberjar"
                       :uberjar-name "instapaper.jar"
                       :aot :all}})
