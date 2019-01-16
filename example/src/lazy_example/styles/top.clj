(ns lazy-example.styles.top
  (:require [garden.selectors :as gs]
            [garden.color :as gc]
            [garden.units :refer [px percent em]]))

(def styles
  [:&.top
   {:text-align 'center
    :width (percent 100)}
   [:h1 {:background 'blue
         :color 'white
         :font-size (px 32)}]
   [:.loading
    {:border [[(px 1) 'solid "#ccc"]]
     :background-position 'center
     :background-repeat 'no-repeat
     :background-image "url(https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/0.16.1/images/loader-large.gif)"}]
   ])
