(ns org.photonsphere.instapaper
  
  "Tool to remove duplicate links (and their surrounding <li> tags) from Instapaper HTML export.

  Snippets taken from:
  * https://ravi.pckl.me/short/functional-xml-editing-using-zippers-in-clojure/
  * https://stackoverflow.com/questions/43377532/editing-an-html-page-with-clojures-zip-maps-and-hickory 

  Twitter: @maridonkers | Google+: +MariDonkers | GitHub: maridonkers"
  
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [clojure.zip :as zip]
            [hickory.core :as hkc]
            [hickory.render :as hkr]
            [hickory.zip :as hkz])
  
  (:gen-class))

(defn is-hyperlink-duplicate?
  "Return true if the node at location is an anchor with a href, whose
  parent is a <li> tag and it's a duplicate."
  [loc links]
  (let [node (zip/node loc)
        parent-loc (zip/up loc)
        is-anchor? (= (:tag node) :a)
        is-li-parent? (if parent-loc
                        (= (:tag (zip/node parent-loc)) :li)
                        false)
        href (get-in node [:attrs :href])
        has-href? (not (str/blank? href))
        is-duplicate? (get @links href false)]
    
    (when (and is-li-parent? is-anchor? has-href? (not is-duplicate?))
      (swap! links assoc href true))

    (and is-li-parent? is-anchor? has-href? is-duplicate?)))

(defn delete-node
  "Deletes matched location from the tree (with its surrounding <li> tag)."
  [loc]
  (zip/remove (zip/up loc)))

(defn prune-duplicates-from-html-tree
  "Take a zipper, a function that matches a pattern in the tree,
   and a function that edits the current location in the tree. Examine the tree
   nodes in depth-first order, determine whether the matcher matches, and if so
   apply the editor."
  [zipper matcher editor]
  (loop [loc zipper
         links (atom {})]
    (if (zip/end? loc)
      (zip/root loc)
      (if-let [matched (matcher loc links)]
        (let [new-loc (editor loc) #_(zip/edit loc editor)]
          (if (not= (zip/node new-loc) (zip/node loc))
            (recur (zip/next new-loc) links)))
        (recur (zip/next loc) links)))))

(defn remove-duplicate-hyperlinks
  "Removes duplicate hyperlinks from html input (with their surrounding <li> tags)."
  [html-in]
  (-> (prune-duplicates-from-html-tree (hkz/hickory-zip (hkc/as-hickory (hkc/parse html-in)))
                      is-hyperlink-duplicate?
                      delete-node #_hilight-node)
      hkr/hickory-to-html))

(defn -main [& args]
  (if (not= 2 (count args))
    (println "Usage: lein run instapaper <input-file-name> <output-file-name>.")
    (let [input-fname (first args)
          output-fname (second args)
          
          html (slurp input-fname)
          html-without-duplicates (remove-duplicate-hyperlinks html)]
      
      (spit output-fname
            html-without-duplicates))))
