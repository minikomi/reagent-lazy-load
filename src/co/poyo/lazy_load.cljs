(ns co.poyo.lazy-load
  (:require [reagent.core :as r]
            [goog.dom :as gdom]
            [goog.events.EventType :as event-type]
            [goog.events :as events]
            [goog.functions :as gfunc]))

(defonce lazy-els (atom {}))
(defonce lazy-loaded (r/atom #{}))
(defonce lazy-fn (atom nil))

(defn is-scrolled-into-view [el {:keys [v-offset h-offset]}]
  (let [rect      (.getBoundingClientRect el)
        el-top    (- (.-top rect) v-offset)
        el-height (+ (.-height rect) (* v-offset 2))
        el-left   (- (.-left rect) h-offset)
        el-width  (+ (.-width rect) (* h-offset 2))]
    (and (<= (- 0 el-height) el-top)
         (>= (.-innerHeight js/window) el-top)
         (<= (- 0 el-width h-offset) el-left)
         (>= (.-innerWidth js/window) el-left))))

(defn unregister-lazy-load [el-uuid]
  (swap! lazy-els dissoc el-uuid))

(defn check-offsets [props _]
  (doseq [[el-uuid [el props]] @lazy-els]
    (when (is-scrolled-into-view el props)
      (let [img-obj (js/Image.)]
        (set! (.-onload img-obj)
              (fn [_]
                (swap! lazy-loaded conj (:src props))))
        (set! (.-src img-obj) (:src props)))
      (unregister-lazy-load el-uuid))))

(defn register-lazy-load [el props]
  (when-not (@lazy-loaded (:src props))
    (if (is-scrolled-into-view el props)
      (swap! lazy-loaded conj (:src props))
      (swap! lazy-els assoc (random-uuid) [el props]))))

(defn register-from-props [this _]
  (register-lazy-load (r/dom-node this)
                      (r/props this)))

(defn lazy-loading-image [{:keys [wrapper-class src alt style]}]
  (r/create-class
   {:component-did-mount
    register-from-props
    :component-did-update
    register-from-props
    :component-will-unmount
    (fn [this _]
      (unregister-lazy-load (r/dom-node this)))
    :reagent-render
    (fn [{:keys [wrapper-style
                 wrapper-class
                 loader-style
                 loader-class
                 image-style
                 image-class
                 src
                 alt
                 h-offset
                 v-offset]}]
      [:span.lazy-wrapper
       {:class (if (get @lazy-loaded src)
                 "loaded"
                 "loading")
        :key ["wrapper" src]
        :style wrapper-style}
       (if-not (get @lazy-loaded src)
         [:span.loader {:style (or loader-style image-style)}]
         [:img.loaded {:src src :alt alt :style image-style}])])}))

(defn init-scroll-listener [f]
  (events/listen js/window event-type/RESIZE (gfunc/throttle f 300))
  (events/listen js/window event-type/SCROLL (gfunc/throttle f 300))
  (f))

(defonce initialize-lazy-load
  (init-scroll-listener check-offsets))
