(ns clear-code.camera
  (:require [raylib.core :as rc]
            [raylib.shapes :as rs]
            [raylib.input :as input]
            [raylib.keys :as key]
            [raylib.colors :as colors]))

(defonce state (atom nil))

(def screen-width 1920)
(def screen-height 1080)

(def circle-colors
  [colors/red colors/green colors/blue colors/yellow colors/orange])

(defn bool->int [b]
  (if b 1 0))

(defn clamp [x low high]
  (max low (min high x)))

(defn rand-between [min max]
  (+ min (rand-int (inc (- max min)))))

(defn random-circle []
  {:pos {:x (rand-between -2000 2000)
         :y (rand-between -1000 1000)}
   :radius (rand-between 50 200)
   :color (rand-nth circle-colors)})

(defn init []
  (rc/init-window screen-width screen-height "Camera")
  (input/set-exit-key key/escape))

(defn normalize-vector-2 [{:keys [x y] :as v}]
  (let [length (Math/sqrt (+ (* x x) (* y y)))]
    (if (zero? length)
      v
      {:x (float (/ x length))
       :y (float (/ y length))})))

(defn read-input []
  {:right? (input/key-down? key/right)
   :left?  (input/key-down? key/left)
   :down?  (input/key-down? key/down)
   :up?    (input/key-down? key/up)
   :rotate-right? (input/key-down? key/s)
   :rotate-left?  (input/key-down? key/a)
   :zoom-in?      (input/key-down? key/w)
   :zoom-out?     (input/key-down? key/q)})

(defn movement-direction [input]
  (normalize-vector-2
   {:x (- (bool->int (:right? input))
          (bool->int (:left? input)))
    :y (- (bool->int (:down? input))
          (bool->int (:up? input)))}))

(defn update-player [state input dt]
  (let [{:keys [x y]} (movement-direction input)
        speed (:player-speed state)]
    (-> state
        (update-in [:player-pos :x] + (* x speed dt))
        (update-in [:player-pos :y] + (* y speed dt)))))

(defn update-camera [state input dt]
  (let [rotate-direction (- (bool->int (:rotate-right? input))
                            (bool->int (:rotate-left? input)))
        zoom-direction (- (bool->int (:zoom-in? input))
                          (bool->int (:zoom-out? input)))]
    (-> state
        (update-in [:camera :rotation] + (* rotate-direction dt 50))
        (update-in [:camera :zoom] #(+ % (* zoom-direction dt 2)))
        (update-in [:camera :zoom] clamp 0.2 2.0)
        (assoc-in [:camera :target] (:player-pos state)))))

(defn start-state []
  (let [player-pos {:x 0.0 :y 0.0}]
    {:player-pos player-pos
     :player-radius 50
     :player-speed 400
     :circles (vec (repeatedly 100 random-circle))
     :camera {:zoom 1.0
              :target player-pos
              :offset {:x (/ screen-width 2.0)
                       :y (/ screen-height 2.0)}
              :rotation 0.0}}))

(defn update-state [state input]
  (let [dt (rc/get-frame-time)]
    (-> state
        (update-player input dt)
        (update-camera input dt))))

(defn draw-circle [{:keys [pos radius color]}]
  (rs/draw-circle-v pos radius color))

(defn draw [{:keys [camera circles player-pos player-radius]}]
  (rc/begin-drawing)
  (rc/clear-background colors/white)

  (rc/begin-mode-2d camera)
  (doseq [circle circles]
    (draw-circle circle))
  (rs/draw-circle-v player-pos player-radius colors/black)
  (rc/end-mode-2d)
  (rc/end-drawing))

(defn tick []
  (let [input (read-input)
        state (swap! state update-state input)]
    (draw state)))

(defn start []
  (init)
  (reset! state (start-state))
  (try
    (loop []
      (when-not (rc/window-should-close?)
        (tick)
        (recur)))
    (finally
      (rc/close-window))))

(comment

  @state

  (swap! state assoc :ship-direction {:x 1 :y -1})

  (swap! state assoc :ship-speed 1000)

  ())
