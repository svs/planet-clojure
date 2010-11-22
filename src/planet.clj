(ns planet
  (:gen-class)
  (:use prolefeed)
  (:require [clojure.contrib.string :as str :only split])
  (:require [clojure.contrib.json :as json :only read-json])
  (:require [net.cgrand.enlive-html :as html])
  (:require [http.async.client :as c])
  (:import [java.util Date]))

(defmacro def-let
  "like let, but binds the expressions globally."
  [bindings & more]
  (let [let-expr (macroexpand `(let ~bindings))
	names-values (partition 2 (second let-expr))
	defs   (map #(cons 'def %) names-values)]
    (concat (list 'do) defs more)))


(html/deftemplate page "template.html" [articles]
  [:section.entry] (html/clone-for [{:keys [title contents description link publishedDate updatedDate author name uri]} (:entries articles)]
			      [:aside :a.title] (html/content title)
			      [:aside :a.title] (html/set-attr :href link)
			      [:span.date] (html/content (str (or publishedDate updatedDate)))
			      [:span.blog-name] (html/content name)
			      [:article] (html/html-content (or (first contents) (str description "<br><a href='" uri "'>" uri "</a>"))))
  [:span.total_pages] (html/content (str (:total_pages articles)))
  [:a.previous_page] (html/do->
		 (if (> (:page articles) 0)
		   (do 
		     (html/content  "Previous")
		     (html/set-attr :href (str "page" (dec (:page articles)) ".html")))
		   (html/substitute "")))
  [:a.next_page] (html/do->
		 (if (> (:total_pages articles) (inc (:page articles)))
		   (do 
		     (html/content  "Next")
		     (html/set-attr :href (str "page" (inc (:page articles)) ".html")))
		   (html/substitute "")))
  [:span.page_number] (html/content (str (inc (:page articles)))))

			       



(defn get-feed
  [blog]
  (do
    (println (str "getting " (:url blog) "..."))
    (try
      (do 
	(let [r (merge {:name (:name blog)} (prolefeed/fetch (:url blog)))]
	  (println (str "Done "  "."))
	r))
      (catch Exception ex "e"
	     (do
	       (.printStackTrace ex))))))


(defn get-all-feeds
  [blog-urls]
  (let [agents (map #(agent %) blog-urls)]
  (doseq [a agents] (send-off a get-feed))
  (apply await-for 5000 agents)
  (doall (map #(deref %) agents))))


(defn -main  [& args]
  (def-let [blogs                (partition 2 (take 6 (json/read-json (slurp "src/blogs.json"))))
	    posts                (apply concat (for [b blogs] (get-all-feeds b)))
	    entries              (apply concat (for [p (remove (fn [s] (nil? s)) posts)]
						(let [name (:name p)]
						  (for [e (:entries p)] (merge e {:name name})))))
	    sorted-entries       (sort-by
				  #(- (. (Date.) getTime) (. (or (:publishedDate %) (:updatedDate %)) getTime))
				  (remove (fn [s] (nil? (or (:publishedDate s) (:updatedDate s)))) entries))
	    partitioned-entries  (partition 20 sorted-entries)]
    (loop [i 0]
      (let [total_pages (count partitioned-entries)]
	(if (>= i total_pages)
	  i
	  (do
	    (println (str "printing page " i))
	    (spit (str "site/page" i ".html") (apply str (page {:entries (nth partitioned-entries i) :page i :total_pages total_pages})))
	    (recur (inc i))))))))



