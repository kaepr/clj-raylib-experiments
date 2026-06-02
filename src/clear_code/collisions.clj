(ns clear-code.collisions
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

(defn rectangle [x y width height]
  {:x x
   :y y
   :width width
   :height height})

(defn rectangle? [x]
  (and (map? x)
       (contains? x :x)
       (contains? x :y)
       (contains? x :width)
       (contains? x :height)))

(defn empty-rectangle? [{:keys [width height]}]
  (or (nil? width)
      (nil? height)
      (zero? width)
      (zero? height)))

(defn init []
  (rc/init-window screen-width screen-height "Collisions")
  (ri/set-exit-key keys/escape))

(defn read-input []
  {:mouse-pos (ri/get-mouse-position)})

(defn start-state []
  (let []
    {:player-pos {:x 0 :y 0}
     :r1 (rectangle 0 0 100 200)
     :r2 (rectangle 800 500 200 300)
     :player-radius 50
     :obstacle-radius 30
     :obstacle-pos {:x 500 :y 400}}))

(defn update-state [state input]
  (let [{:keys [mouse-pos]} input
        {:keys [player-pos
                player-radius
                obstacle-pos
                obstacle-radius
                r1
                r2]}       state
        r1 (rectangle
            (ri/get-mouse-x)
            (ri/get-mouse-y)
            (:width r1)
            (:height r1))
        overlap-rec (rs/get-collision-rec r1 r2)
        collision? (and (rectangle? overlap-rec) (not (empty-rectangle? overlap-rec)))]
    (-> state
        (assoc :collision? collision?)
        (assoc :overlap-rec overlap-rec)
        (assoc :player-pos (ri/get-mouse-position))
        (assoc :r1 r1))))

(defn unload-assets [assets]
  ())

(defn cleanup-state [state]
  ())

(defn draw [{:keys [player-pos
                    player-radius
                    obstacle-pos
                    obstacle-radius
                    collision?
                    overlap-rec
                    r1
                    r2]}]
  (let []
    (rc/begin-drawing)
    (rc/clear-background colors/black)
    (rs/draw-circle-v player-pos player-radius colors/white)
    (rs/draw-circle-v obstacle-pos obstacle-radius colors/white)
    (rs/draw-rectangle-rec r1 colors/blue)
    (rs/draw-rectangle-rec r2 colors/green)
    (when collision?
      (rs/draw-rectangle-rec overlap-rec colors/red))
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
