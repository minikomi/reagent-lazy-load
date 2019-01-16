(ns lazy-example.pages.layout
  (:require [clj-time.local :as local]
            [clojure.data.json :as json]
            [hiccup.page :as hp]))

;; common

(defn pad-number [n]
  (str (when (> 10 n) "0") n))

(defn json-blob [data]
  [:script {:type "text/json"} (json/write-str data)])

(defmacro defdiv [divname & divcontent]
  `(def ~divname
     [:div {:id ~(name divname)}
      (list ~@divcontent)]))

;; layout

(def title-str "Lazy Loading Example")

(defn make-title [params]
  [:title
   (str title-str
        (when-let [extra (:title params)]
          (str " | " extra)))])

(def description-str "Lazy Loading Example")

(defn html-meta [params]
  (list
   [:meta {:charset "UTF-8"}]
   [:meta {:name "description"
           :content (or (:description params) description-str)}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]))

(def default-css
  (list (hp/include-css "/css/styles.css")))

(def default-js
  (list (hp/include-js "/js/main.js")))

;; menu

(defn base-template
  ([content] (base-template content {}))
  ([content params]
   (hp/html5
    [:head
     (str "<!-- Rendered:" (local/local-now) " -->")
     (html-meta params)
     default-css
     (make-title params)]
    [:body {:class (:body-class params "default")}
     [:div#total-wrapper
      [:div#content content]
      [:div#footer]
      default-js]])))
