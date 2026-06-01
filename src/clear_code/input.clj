(ns clear-code.input
  (:require [raylib.core :as rc]
            [raylib.shapes :as rs]
            [raylib.textures :as rt]
            [raylib.text :as rtext]
            [raylib.mouse :as mouse]
            [raylib.keys :as keys]
            [raylib.input :as ri]
            [raylib.colors :as colors]))

(defonce state (atom nil))

(def screen-width 1920)
(def screen-height 1080)

(defn init []
  (rc/init-window screen-width screen-height "Input")
  (ri/set-exit-key keys/escape))

(defn normalize-vector-2 [{:keys [x y] :as v}]
  (let [length (Math/sqrt (+ (* x x) (* y y)))]
    (if (zero? length)
      v
      {:x (float (/ x length))
       :y (float (/ y length))})))

(defn load-assets []
  (let [ship-texture (rt/load-texture "assets/start/basics/assets/spaceship.png")]
    {:ship-texture ship-texture}))

(defn read-input []
  {:mouse-pos (ri/get-mouse-position)
   :left-click? (ri/mouse-button-pressed? mouse/left)
   :right? (ri/key-down? keys/right)
   :left? (ri/key-down? keys/left)
   :up? (ri/key-down? keys/up)
   :down? (ri/key-down? keys/down)})

(defn start-state []
  (let [ship-pos {:x 0 :y 0}
        ship-direction {:x 0 :y 0}
        ship-speed 800]
    {:assets (load-assets)
     :ship-pos ship-pos
     :ship-direction ship-direction
     :ship-speed ship-speed}))

(defn update-state [state input]
  (let [{:keys [mouse-pos right? left? up? down?]} input
        {:keys [ship-pos ship-speed ship-direction]} state
        ship-direction {:x (- (if right? 1 0)
                              (if left? 1 0))
                        :y (- (if down? 1 0)
                              (if up? 1 0))}
        ship-direction (normalize-vector-2 ship-direction)
        delta-time (rc/get-frame-time)
        ship-pos-x (+ (:x ship-pos)
                      (* (:x ship-direction) ship-speed delta-time))
        ship-pos-y (+ (:y ship-pos)
                      (* (:y ship-direction) ship-speed delta-time))
        ship-pos {:x ship-pos-x
                  :y ship-pos-y}]
    (-> state
        (assoc :ship-direction ship-direction)
        (assoc :ship-pos ship-pos))))

(defn unload-assets [assets]
  (let [{:keys [ship-texture]} assets]
    (rt/unload-texture ship-texture)))

(defn cleanup-state [state]
  (unload-assets (:assets state)))

(defn draw [{:keys [assets ship-pos]}]
  (let [{:keys [ship-texture]} assets]
    (rc/begin-drawing)
    (rc/clear-background colors/black)
    (rt/draw-texture-v ship-texture ship-pos colors/white)
    (rtext/draw-fps 0 0)
    (rc/end-drawing)))

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
      (cleanup-state @state)
      (rc/close-window))))

(comment

  @state

  (swap! state assoc :ship-direction {:x 1 :y -1})

  (swap! state assoc :ship-speed 1000)

  ())
