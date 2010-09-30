(ns planet
  (:gen-class)
  (:use prolefeed)
  (:require [clojure.contrib.string :as str :only split])
  (:require [net.cgrand.enlive-html :as html])
  (:import [java.util Date]))

;; (defmacro def-let
;;   "like let, but binds the expressions globally."
;;   [bindings & more]
;;   (let [let-expr (macroexpand `(let ~bindings))
;; 	names-values (partition 2 (second let-expr))
;; 	defs   (map #(cons 'def %) names-values)]
;;     (concat (list 'do) defs more)))


(html/deftemplate page "template.html" [articles]
  [:section.entry] (html/clone-for [{:keys [title contents description link publishedDate updatedDate author name uri]} articles]
			      [:aside :a.title] (html/content title)
			      [:aside :a.title] (html/set-attr :href link)
			      [:span.date] (html/content (str (or publishedDate updatedDate)))
			      [:span.blog-name] (html/content name)
			      [:article] (html/html-content (or (first contents) (str description "<br><a href='" uri "'>" uri "</a>")))))

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


(defn -main  [& args]
  (let [blogs [
		   {:name "Lau B. Jensen", :url "http://feeds.feedburner.com/bestinclass-the-blog"}
		   {:name "Christophe Grand", :url "http://feeds.feedburner.com/ClojureAndMe"}
		   {:name "Baishampayan Ghose", :url "http://freegeek.in/blog/tag/clojure/feed/"}
		   {:name "Rich Hickey", :url "http://clojure.blogspot.com/feeds/posts/default"}
		   {:name "Brian Carper", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=9540fdd6e19ed684f664b06b77967d75&_render=rss"}
		   {:name "Travis Whitton", :url "http://travis-whitton.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "David Edgar Liebke", :url "http://incanter-blog.org/feed/"}
		   {:name "Stuart Sierra", :url "http://feeds2.feedburner.com/StuartSierra"}
		   {:name "Phil Hagelberg", :url "http://technomancy.us/feed/atom.xml"}
		   {:name "Michael Fogus", :url "http://blog.fogus.me/tag/clojure/feed/atom/"}
		   {:name "Amit Rathore", :url "http://s-expressions.com/tag/clojure/feed/"}
		   {:name "Shantanu Kumar", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=cf1831abf6eae4a599aa41fbc230b11c&_render=rss"}
		   {:name "Cosmin Stejerean", :url "http://onclojure.wordpress.com/feed/atom/"}
		   {:name "Karl Krukow", :url "http://blog.higher-order.net/category/clojure/feed/"}
		   {:name "Timothy Pratley", :url "http://timothypratley.blogspot.com/feeds/posts/default"}
		   {:name "Nurullah Akkaya", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=4e1982719b79c21c815dc8b5e6789800&_render=rss"}
		   {:name "Mike Meyer", :url "http://blog.mired.org/feeds/posts/default/-/clojure"}
		   {:name "Chris Houser", :url "http://blog.n01se.net/?cat=14&feed=rss2&author=3"}
		   {:name "Vincent Foley", :url "http://gnuvince.wordpress.com/tag/clojure/feed/"}
		   {:name "Clojure Study Group DC", :url "http://clojurestudydc.wordpress.com/feed/"}
		   {:name "Eric Lavigne", :url "http://ericlavigne.wordpress.com/feed/"}
		   {:name "Jason Wolfe", :url "http://feeds.feedburner.com/w01fe"}
		   {:name "Chas Emerick", :url "http://cemerick.com/category/clojure/feed/"}
		   {:name "Jake McCrary", :url "http://jakemccrary.com/blog/tag/clojure/feed/"}
		   {:name "BugSpy", :url "http://bugspy.net/tag/clojure/?format=rss"}
		   {:name "Nicolas Buduroi", :url "http://whollyweirdwyrd.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "Alex Osborne", :url "http://meshy.org/atom.xml"}
		   {:name "Toni Batchelli", :url "http://feeds.feedburner.com/disclojure"}
		   {:name "Sean Devlin (vimeo)", :url "http://vimeo.com/channels/fulldisclojure/videos/rss"}
		   {:name "Ian Phillips", :url "http://ianp.org/tag/clojure/feed/"}
		   {:name "Zef Hemel", :url "http://zef.me/tag/clojure/feed"}
		   {:name "Kototama", :url "http://inclojurewetrust.blogspot.com/feeds/posts/default"}
		   {:name "Meikel Brandmeyer", :url "http://kotka.de/blog/index.rss"}
		   {:name "Alex Ott", :url "http://alexott.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "Paul Stadig", :url "http://paul.stadig.name/feeds/posts/default/-/clojure"}
		   {:name "Netzhansa", :url "http://netzhansa.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "Writing/Coding", :url "http://writingcoding.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "Thoughts on Programming", :url "http://programming-puzzler.blogspot.com/feeds/posts/default"}
		   {:name "Michael Harrison", :url "http://www.michaelharrison.ws/weblog/?cat=26&feed=rss2"}
		   {:name "Stephen Bach", :url "http://items.sjbach.com/feed"}
		   {:name "Bradford Cross", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=bb5649628a7f97318cb84845387521ef&_render=rss"}
		   {:name "Phil Hagelberg", :url "http://technomancy.us/feed/atom.xml"}
		   {:name "LispCast", :url "http://feeds2.feedburner.com/LispCast"}
		   {:name "Bill Clementson", :url "http://bc.tech.coop/blog/rss.xml"}
		   {:name "Ramakrishnan Muthukrishnan", :url "http://www.zerobeat.in/category/lisp/feed/"}
		   {:name "Tom Hicks", :url "http://tohono.blogspot.com/feeds/posts/default/-/Clojure"}
		   {:name "Michael Kohl", :url "http://citizen428.net/archives/tag/clojure/feed"}
		   {:name "Object Commando", :url "http://www.objectcommando.com/blog/category/clojure/feed/"}
		   {:name "Paul Legato", :url "http://www.paullegato.com/blog/tag/clojure/feed/"}
		   {:name ":wq - blog", :url "http://feeds.feedburner.com/wq-clojure"}
		   {:name "Michael Schneider", :url "http://mischneider.net/?cat=25&feed=rss2"}
		   {:name "David Rupp", :url "http://davidrupp.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "Kétain Dubé!", :url "http://ketain.blogspot.com/feeds/posts/default/-/Clojure"}
		   {:name "Danish Clojure Users Group", :url "http://clojure.higher-order.net/feed/"}
		   {:name "Hugo Duncan", :url "http://hugoduncan.org/tag/clojure.atom"}
		   {:name "Form plus Logic", :url "http://formpluslogic.blogspot.com/feeds/posts/default"}
		   {:name "Musings of a Lispnik", :url "http://blog.danieljanus.pl/clojure/index.rss"}
		   {:name "Viksit Gaur", :url "http://www.viksit.com/taxonomy/term/45/0/feed"}
		   {:name "Mark Watson", :url "http://blog.markwatson.com/feeds/posts/default/-/Clojure"}
		   {:name "Clojure@LJ", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=a0161f6185ca965613735b6b06f2e3ef&_render=rss"}
		   {:name "Nathan Marz", :url "http://community.livejournal.com/clojure/data/atom"}
		   {:name "Debasish Ghosh", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=180af42010b57c605f7b7475b311ec32&_render=rss"}
		   {:name "Jay Fields", :url "http://debasishg.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "Ethan Fast", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=e35d6881f8ced55bed527c7a501fad8d&_render=rss"}
		   {:name "Mark McGranaghan", :url "http://blog.jayfields.com/feeds/posts/default/-/clojure"}
		   {:name "Zach Tellman", :url "http://blog.ethanjfast.com/category/clojure/feed/"}
		   {:name "wmacgyver", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=b940a51bb4440a4817478df38a4bfc12&_render=rss"}
		   {:name "Allen Rohner", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=2859b085bb0148ae9095e616a6de354f&_render=rss"}
		   {:name "miau.biz", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=26da0ac0c1dfbda95eb27a1b4c49be89&_render=rss"}
		   {:name "Clive Tong", :url "http://arohner.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "Stephan Schmidt", :url "http://blog.miau.biz/feeds/posts/default/-/clojure"}
		   {:name "Greg Osuri", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=014b2da708cba86630e63374ffa4d2c7&_render=rss"}
		   {:name "Clojure at InfoQ", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=eb3a37b540911df844dc7807ed1f5cb2&_render=rss"}
		   {:name "Fight Code Entropy", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=c32d9e8ef66b86e970c4aafed23edc5f&_render=rss"}
		   {:name "lice!", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=be83c0b5b0d92b259682cb8021e14d2a&_render=rss"}
		   {:name "hackers-with-attitude", :url "http://fightcodeentropy.com/?cat=6&feed=rss2"}
		   {:name "Mark Reid", :url "http://blog.licenser.net/category/clojure.atom"}
		   {:name "Deep Blue Lambda", :url "http://www.hackers-with-attitude.com/feeds/posts/default/-/Clojure"}
		   {:name "Roland Sadowski", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=f7204ecce7ec8bc20c74661ec8e7d1a4&_render=rss"}
		   {:name "Atomic Spin", :url "http://www.deepbluelambda.org/programming/clojure/index.rss"}
		   {:name "LShift Ltd.", :url "http://www.haltingproblem.net/weblog/tags/tech/clojure/feed/"}
		   {:name "Keep IT Simply Simple", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=4f8569a77c5ae170e4afd516f9543f32&_render=rss"}
		   {:name "Stefan Tilkov", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=79aef1deb9444e26d8d52d8f09bf4b67&_render=rss"}
		   {:name "ProDevTips", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=1c9b63737a8994f788918e978eb023c1&_render=rss"}
		   {:name "Patrick Stein", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=dabeb77a16f672f564fd503bd663555b&_render=rss"}
		   {:name "Clojure Jobs", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=ed581e0d677e30a30351ffa9c62233e9&_render=rss"}
		   {:name "The Weak Reference", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=b502b5f586e3cf34b6cb55420e489fd9&_render=rss"}
		   {:name "Siddhartha Reddy", :url "http://lispjobs.wordpress.com/category/clojure/feed/"}
		   {:name "Clojurous", :url "http://weakreference.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "Paavo Anselmi Parkkinen", :url "http://www.sids.in/blog/feed/?tag=clojure"}
		   {:name "Thomas Kjeldahl Nilsson", :url "http://clojurous.Posterous.com/rss.xml"}
		   {:name "Dmitriy Kropivnitskiy", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=b48edd214140e18dab212c3ea7237bb9&_render=rss"}
		   {:name "Compojure on GAE", :url "http://messynotebook.com/?cat=30&feed=rss2"}
		   {:name "Bugsplat", :url "http://blog.mitechki.net/feeds/posts/default/-/clojure"}
		   {:name "Pramode C.E", :url "http://feeds.feedburner.com/CompojureOnGAE"}
		   {:name "Tim Riddell", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=773e30278903f6b516ec6a3159842452&_render=rss"}
		   {:name "Carlo Sciolla", :url "http://pramode.net/clojure/feed/atom.xml"}
		   {:name "Imagine27", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=820f20fd619c7d616501fc35cd840520&_render=rss"}
		   {:name "The Occasional Blogger", :url "http://www.skuro.tk/category/developer/clojure/feed/"}
		   {:name "Infinity: Easier Than You Think. Infinitely.", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=8f6aa3dc4efdaf19b78f4afa9ba05fb3&_render=rss"}
		   {:name "code.h(oe)kje", :url "http://www.glenstampoultzis.net/blog/category/clojure/feed/"}
		   {:name "Alex Miller", :url "http://toinfinity.wordpress.com/category/clojure/feed/"}
		   {:name "Edgar Gonçalves", :url "http://joost.zeekat.nl/category/clojure/feed/"}
		   {:name "Chad Braun-Duin", :url "http://tech.puredanger.com/category/clojure/feed/"}
		   {:name "This Window: Close It", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=38f6da7f647b56d1f2fe846e55f7443c&_render=rss"}
		   {:name "Object Mentor", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=5c90f4ba11373ad351e956fc400d2fd4&_render=rss"}
		   {:name "Sustainable Code", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=07906a90788326999ae45455523d50f2&_render=rss"}
		   {:name "Asymmetrical View", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=a95334ceb5e0fb26ecf791f82dab500d&_render=rss"}
		   {:name "Will Farr", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=30609a351165bee1ff87cfdb2f1f1cc9&_render=rss"}
		   {:name "Hubert Iwaniuk", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=1a0cc5a5b729058f58307f00a728dc72&_render=rss"}
		   {:name "Codeabout", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=3bdb998f877daebf5e6c9f860e5bef4b&_render=rss"}
		   {:name "CCRi", :url "http://codemeself.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "Epistemological Engineering", :url "http://codeabout.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "Exploration Through Example", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=303b0813cd2a127a465fe2b104b78ecf&_render=rss"}
		   {:name "Read-Eval-Puke", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=5165f98663367ef66efa5fc3d5d40c10&_render=rss"}
		   {:name "Framegen Site Generator", :url "http://www.exampler.com/blog/category/clojure/feed/"}
		   {:name "Devender Gollapally", :url "http://read-eval-puke.blogspot.com/feeds/posts/default"}
		   {:name "Javalobby", :url "http://framegen.wordpress.com/category/clojure/feed/"}
		   {:name "\"It's Actors All The Way Down\"", :url "http://devender.wordpress.com/tag/clojure/feed/"}
		   {:name "Sam Newman", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=f8b56ef73099743fdfe6336dd7cd0d5c&_render=rss"}
		   {:name "Jon Bristow", :url "http://www.dalnefre.com/wp/tag/clojure/feed/"}
		   {:name "Saaien Tist", :url "http://www.magpiebrain.com/category/development/languages-frameworks/clojure-languages-frameworks-development/feed/"}
		   {:name "\"An Architect's View\"", :url "http://jondotcomdotorg.net/tag/clojure/feed/"}
		   {:name "Greg Slepak", :url "http://saaientist.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "David McNeil", :url "http://corfield.org/blog/feeds/rss.cfm/category/clojure"}
		   {:name "?eljko Zirikovi?", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=e8e3866611996c2766b5a105c68b6c1b&_render=rss"}
		   {:name "David Nolen", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=406b0e34ad0f738bf785b160287fbf28&_render=rss"}
		   {:name "So much to do, so little time", :url "http://revolucionlibrary.wordpress.com/tag/clojure/feed/"}
		   {:name "Zen and the Art of Programming", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=b55991297db4be15998c4a92a711a3a9&_render=rss"}
		   {:name "Squirrel", :url "http://blog.rguha.net/?tag=clojure&feed=rss2"}
		   {:name "Ethan Herdrick", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=811937d4e54714433e995b82d5b7be53&_render=rss"}
		   {:name "Jeroen Vloothuis", :url "http://squirrel.pl/blog/tag/clojure/feed/"}
		   {:name "RubyLearning Blog", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=a3322ead4699fdfd38b55c9443a74287&_render=rss"}
		   {:name "Two Guys Arguing", :url "http://jeroenvloothuis.blogspot.com/feeds/posts/default/-/clojure"}
		   {:name "Martin Clausen", :url "http://rubylearning.com/blog/tag/clojure/feed/"}
		   {:name "On Clojure", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=5fde276fa708d340f625fb5a72feab94&_render=rss"}
		   {:name "Gavin McGovern", :url "http://www.spyfoos.com/index.php/tag/clojure/feed/"}
		   {:name "Ative at Work", :url "http://onclojure.com/feed/"}
		   {:name "Sharing at Work", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=873d34495de5a0e1086b8a6ebac98b31&_render=rss"}
		   {:name "William Groppe", :url "http://community.ative.dk/blogs/ative/rss.aspx?Tags=clojure&AndTags=1"}
		   {:name "Sean Devlin", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=9c0caa21a64acf838e4b38033aaff0bc&_render=rss"}
		   {:name "X-combinator", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=c23b3d80907dfd11a7375ae154c9ed0e&_render=rss"}
		   {:name "\"John's Coding Reflections\"", :url "http://fulldisclojure.blogspot.com/feeds/posts/default"}
		   {:name "Jeff Foster", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=796ee0b7c5d764dcf498b0419d0c2feb&_render=rss"}
		   {:name "Siva Jagadeesan", :url "http://metaljoe.wordpress.com/category/clojure/feed/"}
		   {:name "\"Remco van 't Veer\"", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=893ce799a0335da4ca871dc7c3ddb5c3&_render=rss"}
		   {:name "The Clean Coder", :url "http://techbehindtech.com/category/clojure/feed/"}
		   {:name "Alvaro Videla", :url "http://blog.remvee.net/category/clojure/rss.xml"}
		   {:name "Isaac Hodes", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=6d75628a16dcb59b7d5cbb113d5d6948&_render=rss"}
		   {:name "Learning Clojure", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=2dfe6914540e12f77eea04df1d57f5b3&_render=rss"}
		   {:name "Jeff Tucker", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=e2a5049019f608275c1dbd7bbe1ac529&_render=rss"}
		   {:name "Jukka Zitting", :url "http://learnclojure.blogspot.com/feeds/posts/default"}
		   {:name "Jieren Chen", :url "http://blog.trydionel.com/tag/clojure/feed/"}
		   {:name "opus artificem probat", :url "http://jukkaz.wordpress.com/tag/clojure/feed/"}
		   {:name "David Cabana", :url "http://www.jierenchen.com/feeds/posts/default/-/clojure"}
		   {:name "Bruce Durling", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=13f695b744f07195df9d4121406f4362&_render=rss"}
		   {:name "Raynes", :url "http://erl.nfshost.com/category/languages/clojure/feed/"}
		   {:name "Morphling", :url "http://otfrom.wordpress.com/tag/clojure/feed/"}
		   {:name "Stuff Aria Likes", :url "http://blog.acidrayne.net/?feed=rss2"}
		   {:name "pseudofish", :url "http://morphling.wordpress.com/category/clojure/feed/"}
		   {:name "Travis Vachon", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=ccf2f5acc3da2242ae06edbf92be95a9&_render=rss"}
		   {:name "Cyrus Harmon", :url "http://pipes.yahoo.com/pipes/pipe.run?_id=7cccb94a47e451a4b463741b9791d0fb&_render=rss"}		   ]
	    posts               (pmap #(get-feed %) blogs)
	    entries             (apply concat (for [p (remove (fn [s] (nil? s)) posts)]
						(let [name (:name p)]
						  (for [e (:entries p)] (merge e {:name name})))))
	    sorted-entries      (sort-by
				 #(- (. (Date.) getTime) (. (or (:publishedDate %) (:updatedDate %)) getTime))
				 (remove (fn [s] (nil? (or (:publishedDate s) (:updatedDate s)))) entries))
	    partitioned-entries (partition 20 sorted-entries)]
    (loop [i 0]
      (if (>= i (count partitioned-entries))
	i
	(do
	  (println (str "printing page " i))
	  (spit (str "site/page" i ".html") (apply str (page (nth partitioned-entries i))))
	  (recur (inc i)))))))  



