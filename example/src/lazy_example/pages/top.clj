(ns lazy-example.pages.top
  (:require [lazy-example.pages.layout :as layout :refer [defdiv]]))

(defdiv top-header
  [:h1 "Lazy Load Example"]
  [:div#app
   [:h2 "Loading."]])

(defn template [args]
  (layout/base-template
   [:div#top-inner
    top-header]
   (dissoc args :template)))
