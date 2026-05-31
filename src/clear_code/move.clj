(ns clear-code.move
  (:require [raylib.core :as rc]
            [raylib.shapes :as rs]
            [raylib.textures :as rt]
            [raylib.text :as rtext]
            [raylib.colors :as colors]))

(defonce state-atom (atom nil))

(def screen-width 1920)
(def screen-height 1080)

(defn init []
  (rc/init-window 1920 1080 "Move"))

(defn load-assets []
  (let [ship (rt/load-texture "assets/start/basics/assets/spaceship.png")]
    {:ship ship}))

(defn start-state []
  (let [ship-pos {:x 0 :y 0}
        ship-direction {:x 0 :y 0}
        ship-speed 100]
    {:assets (load-assets)
     :ship-pos ship-pos
     :ship-direction ship-direction
     :ship-speed ship-speed}))

(defn bounce-ship [ship-pos ship-direction]
  (let [{:keys [x y]} ship-pos]
    (cond-> ship-direction
      (>= y (- screen-height 40))
      (assoc :y -1)

      (>= x (- screen-width 100))
      (assoc :x -1)

      (<= y 0)
      (assoc :y 1)

      (<= x 0)
      (assoc :x 1))))

(defn update-state [state]
  (let [{:keys [ship-pos ship-speed ship-direction]} state
        delta-time (rc/get-frame-time)
        ship-direction (bounce-ship ship-pos ship-direction)
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
  (let [{:keys [ship]} assets]
    (rt/unload-texture ship)))

(defn cleanup-state [state]
  (unload-assets (:assets state)))

(defn draw [{:keys [assets ship-pos]}]
  (let [{:keys [ship]} assets]
    (rc/begin-drawing)
    (rc/clear-background colors/gray)
    (rt/draw-texture-v ship ship-pos colors/white)
    (rtext/draw-fps 0 0)
    (rc/end-drawing)))

(defn tick []
  (swap! state-atom update-state)
  (draw @state-atom))

(defn start []
  (init)
  (reset! state-atom (start-state))
  (try
    (loop []
      (when-not (rc/window-should-close?)
        (tick)
        (recur)))
    (finally
      (cleanup-state @state-atom)
      (rc/close-window))))

(comment

  @state-atom

  (swap! state-atom assoc :ship-direction {:x 1 :y -1})

  (swap! state-atom assoc :ship-speed 1000)

  ())
