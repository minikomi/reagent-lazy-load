(ns lazy-example.styles.core
  (:require
   [lazy-example.styles.top :as top]
   [garden.selectors :as gs]
   [garden.units :refer [em percent px]]))

(def combined
  [[:*
    {:box-sizing 'border-box}]
   [:body
    {:font 'sans-serif}]
   top/styles])
