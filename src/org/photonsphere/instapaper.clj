;; DISCLAIMER: THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
;; CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
;; INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
;; MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
;; DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
;; BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
;; OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
;; PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
;; PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
;; OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
;; (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
;; USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
;; DAMAGE.

(ns org.photonsphere.instapaper
  
  "Tool to remove duplicate links (and their surrounding <li> tags) from Instapaper HTML export.

  Snippets taken from:
  * https://ravi.pckl.me/short/functional-xml-editing-using-zippers-in-clojure/
  * https://stackoverflow.com/questions/43377532/editing-an-html-page-with-clojures-zip-maps-and-hickory 

  Twitter: @maridonkers | Google+: +MariDonkers | GitHub: maridonkers"
  
  (:require [clojure.string :as str]
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
        (let [new-loc (editor loc)]
          (if (not= (zip/node new-loc) (zip/node loc))
            (recur (zip/next new-loc) links)))
        (recur (zip/next loc) links)))))

(defn remove-duplicate-hyperlinks
  "Removes duplicate hyperlinks from html input (with their surrounding <li> tags)."
  [html-in]
  (-> (prune-duplicates-from-html-tree (-> html-in
                                           hkc/parse 
                                           hkc/as-hickory 
                                           hkz/hickory-zip)
                                       is-hyperlink-duplicate?
                                       delete-node)
      hkr/hickory-to-html))

(defn -main [& args]
  (if (not= 2 (count args))
    (println "Usage: lein run input-file-name output-file-name.")
    (let [input-fname (first args)
          output-fname (second args)
          
          html (slurp input-fname)
          html-without-duplicates (remove-duplicate-hyperlinks html)]
      
      (spit output-fname
            html-without-duplicates))))
