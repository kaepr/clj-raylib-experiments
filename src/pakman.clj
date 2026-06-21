(ns pakman
  (:require [raylib.core :as raylib]
            [raylib.shapes :as shapes]
            [raylib.nrepl :as nrepl]
            [raylib.textures :as textures]
            [raylib.audio :as audio]
            [raylib.input :as input]
            [raylib.keys :as keys]
            [odoyle.rules :as o]
            [raylib.colors :as colors]))

(def screen-width 1280)
(def screen-height 720)

(defonce state (atom nil))

(defn load-assets []
  {})

(defn unload-assets! [_])

(def tile-size 60)

(def player-speed 100)

(defn distance [{x1 :x y1 :y} {x2 :x y2 :y}]
  (Math/sqrt (+ (* (- x2 x1) (- x2 x1))
                (* (- y2 y1) (- y2 y1)))))

(defn move-towards [{x :x y :y :as position} target max-distance]
  (let [d (distance position target)]
    (if (or (zero? d) (<= d max-distance))
      target
      (let [t (/ max-distance d)]
        {:x (+ x (* (- (:x target) x) t))
         :y (+ y (* (- (:y target) y) t))}))))

(defn grid []
  [[:wall :wall :wall :wall :wall :wall :wall]
   [:wall :power :pellet :pellet :pellet :power :wall]
   [:wall :pellet :wall :path :wall :pellet :wall]
   [:wall :pellet :pellet :player :path :pellet :wall]
   [:wall :pellet :wall :path :wall :pellet :wall]
   [:wall :power :pellet :ghost :pellet :power :wall]
   [:wall :wall :wall :wall :wall :wall :wall]])

(def direction->offset
  {:left [-1 0]
   :right  [1 0]
   :up [0 -1]
   :down [0 1]})

(def walktable-tiles
  #{:path :pellet :power :player :ghost})

(defn add-tile [[x y] [dx dy]]
  [(+ x dx) (+ y dy)])

(defn tile-at [grid [col row]]
  (get-in grid [row col]))

(defn walkable? [grid tile]
  (contains? walktable-tiles (tile-at grid tile)))

(defn move-tile [tile direction]
  (if-let [offset (direction->offset direction)]
    (add-tile tile offset)
    tile))

(defn find-tile [grid target]
  (first
   (for [[row tiles] (map-indexed vector grid)
         [col tile] (map-indexed vector tiles)
         :when (= tile target)]
     [col row])))

(defn clean-grid [grid]
  (mapv (fn [row]
          (mapv #(case %
                   :player :path
                   :ghost :path
                   %)
                row))
        grid))

(defn tile-center [[col row]]
  {:x (+ (* col tile-size) (/ tile-size 2))
   :y (+ (* row tile-size) (/ tile-size 2))})

(defn player-tile [session]
  (:tile (first (o/query-all session ::player))))

(def rules
  (o/ruleset
   {::game
    [:what
     [::game ::grid grid]]
    ::player
    [:what
     [::player ::tile tile]
     [::player ::position position]
     [::player ::target target]
     [::player ::direction direction]
     [::player ::moving? moving?]]
    ::start-player-move
    [:what
     [::input ::move-command command]
     [::player ::tile tile {:then false}]
     [::player ::moving? moving? {:then false}]
     [::game ::grid grid {:then false}]
     :then
     (when-not moving?
       (let [{:keys [direction]} command
             next-tile (move-tile tile direction)]
         (when (walkable? grid next-tile)
           (o/insert! ::player ::direction direction)
           (o/insert! ::player ::target (tile-center next-tile))
           (o/insert! ::player ::moving? true))))]
    ::advance-player
    [:what
     [::time ::delta delta-time]
     [::player ::position position {:then false}]
     [::player ::target target {:then false}]
     [::player ::direction direction {:then false}]
     [::player ::moving? moving? {:then false}]
     [::player ::tile tile {:then false}]
     :then
     (when moving?
       (let [max-distance (* player-speed delta-time)
             new-position (move-towards position target max-distance)]
         (o/insert! ::player ::position new-position)
         (when (= new-position target)
           (o/insert! ::player ::tile (move-tile tile direction))
           (o/insert! ::player ::moving? false))))]}))

(defn initial-session []
  (let [raw-grid (grid)
        player-tile (find-tile raw-grid :player)
        player-position (tile-center player-tile)]
    (-> (reduce o/add-rule (o/->session) rules)
        (o/insert ::game ::grid (clean-grid raw-grid))
        (o/insert ::player ::tile player-tile)
        (o/insert ::player ::position player-position)
        (o/insert ::player ::target player-position)
        (o/insert ::player ::direction nil)
        (o/insert ::player ::moving? false)
        o/fire-rules)))

(defn initial-state [assets]
  {:assets assets
   :frame 0
   :session (initial-session)})

(defn reset-game! []
  (swap! state assoc :session (initial-session)))

;; User Input
(defn read-input []
  {:direction
   (cond
     (input/key-down? keys/left) :left
     (input/key-down? keys/right) :right
     (input/key-down? keys/up) :up
     (input/key-down? keys/down) :down)})

(defn update-state [state input delta-time]
  (let [frame (inc (:frame state))]
    (-> state
        (assoc :frame frame)
        (update state :session
                (fn [session]
                  (cond-> session
                    true (o/insert ::time ::delta delta-time)
                    (:direction input) (o/insert ::input ::move-command
                                                 {:direction (:direction input)
                                                  :frame frame})
                    true o/fire-rules))))))

(defn handle-events! [old-state new-state input]
  ())

;; draw

(defn game-grid [session]
  (-> session
      (o/query-all ::game)
      first
      :grid))

(defn tile-pos [col row]
  {:x (* col tile-size)
   :y (* row tile-size)})

(defn draw-tile [tile col row]
  (let [{:keys [x y]} (tile-pos col row)
        center {:x (+ x (/ tile-size 2))
                :y (+ y (/ tile-size 2))}]
    (shapes/draw-rectangle x y tile-size tile-size colors/darkblue)
    (case tile
      :wall (shapes/draw-rectangle
             (+ x 2) (+ y 2)
             (- tile-size 4) (- tile-size 4)
             colors/blue)
      :pellet (shapes/draw-circle-v center 3 colors/yellow)
      :power (shapes/draw-circle-v center 8 colors/orange)
      nil)))

(defn draw-grid [grid]
  (doseq [[row tiles] (map-indexed vector grid)
          [col tile] (map-indexed vector tiles)]
    (draw-tile tile col row)))

(defn player [session]
  (first (o/query-all session ::player)))

(defn draw [state]
  (let [session (:session state)
        grid (game-grid session)
        player (player session)]
    (raylib/begin-drawing)
    (raylib/clear-background colors/black)
    (draw-grid grid)
    (shapes/draw-circle-v
     (:position player)
     20
     colors/yellow)
    (raylib/end-drawing)))

;; Frame and lifecycle

(defn run-frame! []
  (let [input (read-input)
        delta-time (raylib/get-frame-time)
        old-state @state
        new-state (swap! state update-state input delta-time)]
    (handle-events! old-state new-state input)
    (draw new-state)))

(defn init! []
  (raylib/init-window screen-width screen-height "Pakman")
  (raylib/set-target-fps 60))

(defn start []
  (init!)
  (try
    (let [assets (load-assets)]
      (reset! state (initial-state assets))

      (try
        (loop []
          (when-not (raylib/window-should-close?)
            (run-frame!)
            (recur)))
        (finally
          (unload-assets! assets)
          (reset! state nil))))
    (finally
      (raylib/close-window))))

(defn -main [& args]
  (nrepl/start {:port 7888})
  (start))
