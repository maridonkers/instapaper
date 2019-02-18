(defproject instapaper "0.1.0-SNAPSHOT"
  :description "Tool to remove duplicate links (and their surrounding <li> tags) from Instapaper HTML export."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "0.4.1"]
                 [hickory "0.7.1"]]

  :source-paths ["src"]
  :main org.photonsphere.instapaper
  )
