(ns lazy-example.frontend.core
  (:require [goog.dom :as gdom]
            [reagent.core :as r]
            [co.poyo.lazy-load :refer [lazy-loading-image]]
            [ajax.core :as ajax]))

(defn dev-setup []
  (if goog.DEBUG
    (do (enable-console-print!)
        (println "dev mode"))
    (set! *print-fn* (fn [& _]))))

(def current-images (r/atom nil))
(def absolute (r/atom false))

(def h-offset (r/atom 0))
(def v-offset (r/atom 0))

(defn reset-current-images! []
  (reset! current-images
   (doall
    (for [n (range 30)
          :let [w (+ 100 (* n 20))
                h (+ 100 (* n 20))
                t (* n 200)
                l (* n 200)]]
      {:src (str "https://picsum.photos/" w "/" h)
       :t t
       :l l
       :w w
       :h h}))))

(defn lazy-test []
  [:div
   {:style {:position "relative"}}
   [:div
    [:button
     {:on-click
      (fn [ev]
        (.preventDefault ev)
        (reset-current-images!)
        (swap! absolute not))}
     (if @absolute
       "Test List"
       "Test Absolute")]]
   [:h3 "v-offset"]
   [:input {:type "number"
            :step "1"
            :value @v-offset
            :on-change
            (fn [ev]
              (reset! v-offset
                      (js/parseInt (.. ev -target -value) 10)))}]
   [:h3 "h-offset"]
   [:input {:type "number"
            :step "1"
            :value @h-offset
            :on-change
            (fn [ev]
              (reset! h-offset
                      (js/parseInt (.. ev -target -value) 10)))}]
   (doall
    (for [{:keys [w h t l src] :as i} @current-images]
      ^{:key i}
      [lazy-loading-image
       {:v-offset @v-offset
        :h-offset @h-offset
        :wrapper-style (if @absolute
                        {:width (str w "px")
                         :height (str h "px")
                         :position "absolute"
                         :top (str t "px")
                         :left (str l "px")}
                        {:display 'block
                         :width (str w "px")
                         :height (str w "px")
                         :margin "10px auto"})
        :image-style {:display "block"
                      :width "100%"
                      :height "100%"}
        :alt (str w "x" h " image")
        :src src}]))])

(defn mount []
  (r/render-component
   [lazy-test]
   (.getElementById js/document "app")))

(defn init []
  (dev-setup)
  (reset-current-images!)
  (mount))
