(ns pakman
  (:require [odoyle.rules :as o]
            [raylib.colors :as colors]
            [raylib.core :as raylib]
            [raylib.input :as input]
            [raylib.keys :as keys]
            [raylib.nrepl :as nrepl]
            [raylib.shapes :as shapes]
            [raylib.text :as text]))

(defonce portal (atom nil))

(defn open-portal! []
  (require '[portal.api :as p])
  (let [p-open (requiring-resolve 'portal.api/open)
        p-submit (requiring-resolve 'portal.api/submit)]
    (when-not @portal
      (reset! portal (p-open))
      (add-tap p-submit)))
  :portal/opened)

(defn close-portal! []
  (let [p-close (requiring-resolve 'portal.api/close)
        p-submit (requiring-resolve 'portal.api/submit)]
    (remove-tap p-submit)
    (when @portal
      (p-close)
      (reset! portal nil)))
  :portal/closed)

(def screen-width 1280)
(def screen-height 720)

(def board-x 260)
(def board-y 36)
(def tile-size 24)

(def player-speed 125.0)
(def enemy-speed 96.0)
(def frightened-seconds 8.0)

(defonce state (atom nil))

(def grid
  [[:wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall]
   [:wall :power :pellet :pellet :pellet :pellet :pellet :pellet :wall :pellet :wall :pellet :pellet :pellet :pellet :pellet :pellet :power :wall]
   [:wall :pellet :wall :wall :pellet :wall :wall :pellet :wall :pellet :wall :pellet :wall :wall :pellet :wall :wall :pellet :wall]
   [:wall :pellet :wall :wall :pellet :wall :wall :pellet :pellet :pellet :pellet :pellet :wall :wall :pellet :wall :wall :pellet :wall]
   [:wall :pellet :pellet :pellet :pellet :pellet :pellet :pellet :wall :wall :wall :pellet :pellet :pellet :pellet :pellet :pellet :pellet :wall]
   [:wall :pellet :wall :wall :pellet :wall :pellet :pellet :pellet :wall :pellet :pellet :pellet :wall :pellet :wall :wall :pellet :wall]
   [:wall :pellet :pellet :pellet :pellet :wall :wall :wall :path :wall :path :wall :wall :wall :pellet :pellet :pellet :pellet :wall]
   [:wall :wall :wall :wall :pellet :wall :path :path :path :path :path :path :path :wall :pellet :wall :wall :wall :wall]
   [:path :path :path :wall :pellet :wall :path :wall :wall :door :wall :wall :path :wall :pellet :wall :path :path :path]
   [:wall :wall :wall :wall :pellet :path :path :wall :ghost :ghost :ghost :wall :path :path :pellet :wall :wall :wall :wall]
   [:path :path :path :path :pellet :wall :path :wall :wall :wall :wall :wall :path :wall :pellet :path :path :path :path]
   [:wall :wall :wall :wall :pellet :wall :path :path :path :path :path :path :path :wall :pellet :wall :wall :wall :wall]
   [:wall :pellet :pellet :pellet :pellet :wall :wall :wall :path :wall :path :wall :wall :wall :pellet :pellet :pellet :pellet :wall]
   [:wall :pellet :wall :wall :pellet :wall :pellet :pellet :pellet :wall :pellet :pellet :pellet :wall :pellet :wall :wall :pellet :wall]
   [:wall :power :pellet :wall :pellet :pellet :pellet :pellet :wall :player :wall :pellet :pellet :pellet :pellet :wall :pellet :power :wall]
   [:wall :wall :pellet :wall :pellet :wall :wall :pellet :pellet :pellet :pellet :pellet :wall :wall :pellet :wall :pellet :wall :wall]
   [:wall :pellet :pellet :pellet :pellet :wall :wall :pellet :wall :wall :wall :pellet :wall :wall :pellet :pellet :pellet :pellet :wall]
   [:wall :pellet :wall :wall :wall :wall :pellet :pellet :pellet :wall :pellet :pellet :pellet :wall :wall :wall :wall :pellet :wall]
   [:wall :pellet :pellet :pellet :pellet :pellet :pellet :wall :pellet :pellet :pellet :wall :pellet :pellet :pellet :pellet :pellet :pellet :wall]
   [:wall :pellet :wall :wall :pellet :wall :pellet :wall :wall :wall :wall :wall :pellet :wall :pellet :wall :wall :pellet :wall]
   [:wall :power :pellet :pellet :pellet :wall :pellet :pellet :pellet :pellet :pellet :pellet :pellet :wall :pellet :pellet :pellet :power :wall]
   [:wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall :wall]])

(def walkable-tiles #{:path :pellet :power :player :ghost :door})

(def directions [:left :right :up :down])

(def direction->offset
  {:left [-1 0]
   :right [1 0]
   :up [0 -1]
   :down [0 1]})

(def opposite-direction
  {:left :right
   :right :left
   :up :down
   :down :up})

(def ghost-specs
  {:blinky
   {:label "Blinky"
    :color {:r 255 :g 48 :b 48 :a 255}
    :start-tile [9 8]
    :start-direction :left
    :scatter-target [18 -2]}

   :pinky
   {:label "Pinky"
    :color {:r 255 :g 130 :b 210 :a 255}
    :start-tile [8 10]
    :start-direction :left
    :scatter-target [0 -2]}

   :inky
   {:label "Inky"
    :color {:r 60 :g 220 :b 255 :a 255}
    :start-tile [9 10]
    :start-direction :right
    :scatter-target [18 24]}

   :clyde
   {:label "Clyde"
    :color {:r 255 :g 170 :b 45 :a 255}
    :start-tile [10 10]
    :start-direction :right
    :scatter-target [0 24]}})

(defn abs [x]
  (if (neg? x) (- x) x))

(defn direction-offset [direction]
  (get direction->offset direction [0 0]))

(defn add-tile [[col row] [dx dy]]
  [(+ col dx) (+ row dy)])

(defn next-tile [tile direction]
  (add-tile tile (direction-offset direction)))

(defn tile-at [grid [col row]]
  (get-in grid [row col] :wall))

(defn walkable? [grid tile]
  (contains? walkable-tiles (tile-at grid tile)))

(defn find-tile [grid wanted]
  (first
   (for [[row tiles] (map-indexed vector grid)
         [col tile] (map-indexed vector tiles)
         :when (= wanted tile)]
     [col row])))

(defn find-tiles [grid wanted]
  (vec
   (for [[row tiles] (map-indexed vector grid)
         [col tile] (map-indexed vector tiles)
         :when (= wanted tile)]
     [col row])))

(defn clean-grid [grid]
  (mapv
   (fn [row]
     (mapv
      (fn [tile]
        (if (contains? #{:player :ghost :pellet :power} tile)
          :path
          tile))
      row))
   grid))

(defn pellet-tiles [grid]
  (for [[row tiles] (map-indexed vector grid)
        [col tile] (map-indexed vector tiles)
        :when (contains? #{:pellet :power} tile)]
    {:tile [col row]
     :kind tile}))

(defn tile-pos [col row]
  {:x (+ board-x (* col tile-size))
   :y (+ board-y (* row tile-size))})

(defn tile-center [col row]
  {:x (+ board-x (* col tile-size) (/ tile-size 2))
   :y (+ board-y (* row tile-size) (/ tile-size 2))})

(defn distance [a b]
  (let [dx (- (:x b) (:x a))
        dy (- (:y b) (:y a))]
    (Math/sqrt (+ (* dx dx) (* dy dy)))))

(defn move-towards [position target max-distance]
  (let [d (distance position target)]
    (if (or (zero? d)
            (<= d max-distance))
      target
      (let [t (/ max-distance d)
            dx (- (:x target) (:x position))
            dy (- (:y target) (:y position))]
        {:x (+ (:x position) (* dx t))
         :y (+ (:y position) (* dy t))}))))

(defn tile-distance [[ax ay] [bx by]]
  (+ (abs (- ax bx))
     (abs (- ay by))))

(defn valid-direction? [grid tile direction]
  (walkable? grid (next-tile tile direction)))

(defn legal-directions [grid tile current-direction]
  (let [reverse-direction (opposite-direction current-direction)
        choices (filter #(valid-direction? grid tile %) directions)
        without-reverse (remove #(= reverse-direction %) choices)]
    (vec (or (seq without-reverse)
             choices))))

(defn best-direction-towards [grid tile current-direction target-tile]
  (let [choices (legal-directions grid tile current-direction)]
    (or (first
         (sort-by
          #(tile-distance (next-tile tile %) target-tile)
          choices))
        current-direction)))

(defn choose-direction [grid tile current-direction requested-direction]
  (cond
    (and requested-direction
         (valid-direction? grid tile requested-direction))
    requested-direction

    (and current-direction
         (valid-direction? grid tile current-direction))
    current-direction

    :else
    nil))

(defn ahead-tile [tile direction n]
  (let [[col row] tile
        [dx dy] (direction-offset direction)]
    [(+ col (* dx n))
     (+ row (* dy n))]))

(defn mover-by-id [movers id]
  (some #(when (= (:id %) id) %) movers))

(defn ghost-target-tile [ghost player blinky game]
  (let [ghost-type (:ghost-type ghost)
        player-tile (:tile player)
        player-direction (:direction player)
        ghost-tile (:tile ghost)
        scatter-target (get-in ghost-specs [ghost-type :scatter-target])]

    (cond
      (= (:mode game) :frightened)
      scatter-target

      (= ghost-type :blinky)
      player-tile

      (= ghost-type :pinky)
      (ahead-tile player-tile player-direction 4)

      (= ghost-type :inky)
      (let [pivot (ahead-tile player-tile player-direction 2)
            blinky-tile (:tile blinky player-tile)
            vx (- (first pivot) (first blinky-tile))
            vy (- (second pivot) (second blinky-tile))]
        [(+ (first pivot) vx)
         (+ (second pivot) vy)])

      (= ghost-type :clyde)
      (if (> (tile-distance ghost-tile player-tile) 8)
        player-tile
        scatter-target)

      :else
      player-tile)))

(defn choose-ghost-direction [grid ghost movers game]
  (let [player (mover-by-id movers :player)
        blinky (mover-by-id movers :blinky)
        target (ghost-target-tile ghost player blinky game)]
    (best-direction-towards grid
                            (:tile ghost)
                            (:direction ghost)
                            target)))

(def rules
  (o/ruleset
   {::game
    [:what
     [::game ::grid grid]
     [::game ::score score]
     [::game ::status status]
     [::game ::mode mode]
     [::game ::frightened-time frightened-time]
     [::game ::pellets-left pellets-left]]

    ::mover
    [:what
     [id ::kind kind]
     [id ::tile tile]
     [id ::position position]
     [id ::direction direction]
     [id ::requested-direction requested-direction]
     [id ::speed speed]
     [id ::alive? alive?]
     [id ::ghost-type ghost-type]]

    ::pellet
    [:what
     [id ::pellet? true]
     [id ::tile tile]
     [id ::kind kind]]

    ::eat-pellet
    [:what
     [::game ::score score]
     [::game ::pellets-left pellets-left]
     [player-id ::kind :player]
     [player-id ::tile tile]
     [player-id ::alive? true]
     [pellet-id ::pellet? true]
     [pellet-id ::tile tile]
     [pellet-id ::kind kind]
     :then
     (o/insert! pellet-id ::pellet? false)
     (o/insert! ::game ::score (+ score (if (= kind :power) 50 10)))
     (o/insert! ::game ::pellets-left (dec pellets-left))
     (when (= kind :power)
       (o/insert! ::game ::mode :frightened)
       (o/insert! ::game ::frightened-time frightened-seconds))]

    ::player-enemy-collision
    [:what
     [::game ::score score]
     [::game ::status :playing]
     [::game ::mode mode]
     [player-id ::kind :player]
     [player-id ::tile tile]
     [player-id ::alive? true]
     [enemy-id ::kind :enemy]
     [enemy-id ::tile tile]
     [enemy-id ::alive? true]
     :then
     (if (= mode :frightened)
       (do
         (o/insert! enemy-id ::alive? false)
         (o/insert! ::game ::score (+ score 200)))
       (do
         (o/insert! player-id ::alive? false)
         (o/insert! ::game ::status :game-over)))]

    ::win
    [:what
     [::game ::status :playing]
     [::game ::pellets-left 0]
     :then
     (o/insert! ::game ::status :win)]}))

(defn add-rules [session]
  (reduce o/add-rule session rules))

(defn insert-mover [session mover]
  (let [id (:id mover)]
    (-> session
        (o/insert id ::kind (:kind mover))
        (o/insert id ::tile (:tile mover))
        (o/insert id ::position (:position mover))
        (o/insert id ::direction (:direction mover))
        (o/insert id ::requested-direction (:requested-direction mover))
        (o/insert id ::speed (:speed mover))
        (o/insert id ::alive? (:alive? mover))
        (o/insert id ::ghost-type (:ghost-type mover)))))

(defn insert-pellet [session pellet]
  (let [id [:pellet (:tile pellet)]]
    (-> session
        (o/insert id ::pellet? true)
        (o/insert id ::tile (:tile pellet))
        (o/insert id ::kind (:kind pellet)))))

(defn ghost-movers []
  (mapv
   (fn [[ghost-type spec]]
     {:id ghost-type
      :kind :enemy
      :ghost-type ghost-type
      :tile (:start-tile spec)
      :position (apply tile-center (:start-tile spec))
      :direction (:start-direction spec)
      :requested-direction (:start-direction spec)
      :speed enemy-speed
      :alive? true})
   ghost-specs))

(defn initial-session []
  (let [base-grid (clean-grid grid)
        pellets (vec (pellet-tiles grid))
        player-start (find-tile grid :player)
        player {:id :player
                :kind :player
                :ghost-type nil
                :tile player-start
                :position (apply tile-center player-start)
                :direction nil
                :requested-direction nil
                :speed player-speed
                :alive? true}]
    (-> (o/->session)
        add-rules
        (o/insert ::game ::grid base-grid)
        (o/insert ::game ::score 0)
        (o/insert ::game ::status :playing)
        (o/insert ::game ::mode :normal)
        (o/insert ::game ::frightened-time 0.0)
        (o/insert ::game ::pellets-left (count pellets))
        (as-> session
              (reduce insert-mover session (cons player (ghost-movers))))
        (as-> session
              (reduce insert-pellet session pellets))
        o/fire-rules)))

(defn load-assets []
  {})

(defn unload-assets! [_assets]
  nil)

(defn initial-state []
  {:assets (load-assets)
   :frame 0
   :session (initial-session)})

(defn reset-game! []
  (reset! state (initial-state)))

(defn query-one-result [session rule-id]
  (-> session
      (o/query-all rule-id)
      first
      :result))

(defn game-state [session]
  (first (o/query-all session ::game)))

(defn movers [session]
  (vec (o/query-all session ::mover)))

(defn pellets [session]
  (vec (o/query-all session ::pellet)))

(defn player [session]
  (mover-by-id (movers session) :player))

(defn read-input []
  {:direction (cond
                (input/key-down? keys/left) :left
                (input/key-down? keys/right) :right
                (input/key-down? keys/up) :up
                (input/key-down? keys/down) :down
                :else nil)
   :restart? (input/key-pressed? keys/r)})

(defn centered-on-tile? [mover]
  (= (:position mover)
     (apply tile-center (:tile mover))))

(defn advance-mover [grid mover all-movers game dt]
  (if-not (:alive? mover)
    mover
    (let [max-distance (* (:speed mover) dt)
          centered? (centered-on-tile? mover)
          chosen-direction (if centered?
                             (case (:kind mover)
                               :player
                               (choose-direction grid
                                                 (:tile mover)
                                                 (:direction mover)
                                                 (:requested-direction mover))

                               :enemy
                               (choose-ghost-direction grid mover all-movers game)

                               (:direction mover))
                             (:direction mover))]
      (if-not chosen-direction
        mover
        (let [target-tile (next-tile (:tile mover) chosen-direction)
              target-position (apply tile-center target-tile)
              position (if (valid-direction? grid (:tile mover) chosen-direction)
                         (move-towards (:position mover) target-position max-distance)
                         (:position mover))
              reached? (= position target-position)]
          (assoc mover
                 :direction chosen-direction
                 :position position
                 :tile (if reached? target-tile (:tile mover))))))))

(defn insert-mover-facts [session mover]
  (let [id (:id mover)]
    (-> session
        (o/insert id ::tile (:tile mover))
        (o/insert id ::position (:position mover))
        (o/insert id ::direction (:direction mover))
        (o/insert id ::requested-direction (:requested-direction mover))
        (o/insert id ::alive? (:alive? mover)))))

(defn update-frightened-mode [session dt]
  (let [game (game-state session)]
    (if (= (:mode game) :frightened)
      (let [time-left (max 0.0 (- (:frightened-time game) dt))]
        (cond-> (o/insert session ::game ::frightened-time time-left)
          (zero? time-left)
          (-> (o/insert ::game ::mode :normal))))
      session)))

(defn update-state [state input]
  (if (:restart? input)
    (initial-state)
    (let [dt (raylib/get-frame-time)
          session (:session state)
          session (cond-> session
                    (:direction input)
                    (o/insert :player ::requested-direction (:direction input)))
          session (o/fire-rules session)
          session (update-frightened-mode session dt)
          game (game-state session)
          session (if (= (:status game) :playing)
                    (let [grid (:grid game)
                          all-movers (movers session)
                          moved-movers (mapv #(advance-mover grid % all-movers game dt)
                                             all-movers)]
                      (-> (reduce insert-mover-facts session moved-movers)
                          o/fire-rules))
                    session)]
      (-> state
          (update :frame inc)
          (assoc :session session)))))

(defn draw-tile [col row tile]
  (let [{:keys [x y]} (tile-pos col row)]
    (case tile
      :wall
      (shapes/draw-rectangle x y tile-size tile-size colors/blue)

      :door
      (shapes/draw-rectangle x (+ y 10) tile-size 4 colors/white)

      (shapes/draw-rectangle x y tile-size tile-size colors/black))))

(defn draw-grid [grid]
  (doseq [[row tiles] (map-indexed vector grid)
          [col tile] (map-indexed vector tiles)]
    (draw-tile col row tile)))

(defn draw-pellet [pellet]
  (let [{:keys [x y]} (apply tile-center (:tile pellet))
        radius (if (= (:kind pellet) :power) 6 2)]
    (shapes/draw-circle x y radius colors/yellow)))

(defn ghost-color [mover game]
  (cond
    (= (:mode game) :frightened)
    {:r 40 :g 80 :b 255 :a 255}

    :else
    (get-in ghost-specs [(:ghost-type mover) :color]
            colors/red)))

(defn draw-mover [mover game]
  (when (:alive? mover)
    (let [{:keys [x y]} (:position mover)
          color (case (:kind mover)
                  :player colors/yellow
                  :enemy (ghost-color mover game)
                  colors/white)]
      (shapes/draw-circle-v {:x x :y y} 10 color))))

(defn draw-ui [game]
  (text/draw-text "PAKMAN" 28 36 32 colors/yellow)
  (text/draw-text (str "Score: " (:score game)) 28 92 24 colors/white)
  (text/draw-text (str "Pellets: " (:pellets-left game)) 28 126 20 colors/white)
  (text/draw-text (str "Mode: " (name (:mode game))) 28 160 20 colors/white)

  (when (= (:mode game) :frightened)
    (text/draw-text (str "Fright: " (int (Math/ceil (:frightened-time game))))
                    28 194 20 colors/skyblue))

  (text/draw-text "R restart" 28 640 20 colors/gray)

  (case (:status game)
    :game-over
    (text/draw-text "GAME OVER" 510 330 48 colors/red)

    :win
    (text/draw-text "YOU WIN" 540 330 48 colors/yellow)

    nil))

(defn draw [state]
  (let [session (:session state)
        game (game-state session)]
    (raylib/begin-drawing)
    (raylib/clear-background colors/black)

    (draw-ui game)
    (draw-grid (:grid game))

    (doseq [pellet (pellets session)]
      (draw-pellet pellet))

    (doseq [mover (movers session)]
      (draw-mover mover game))

    (text/draw-fps 10 690)

    (raylib/end-drawing)))

(defn run-frame! []
  (swap! state update-state (read-input))
  (draw @state))

(defn init! []
  (raylib/init-window screen-width screen-height "Pakman")
  (raylib/set-target-fps 60)
  (reset-game!))

(defn start []
  (nrepl/start {:port 7888})
  (init!)
  (try
    (while (not (raylib/window-should-close?))
      (run-frame!))
    (finally
      (unload-assets! (:assets @state))
      (raylib/close-window))))

(defn debug-snapshot [state]
  (let [session (:session state)
        game (game-state session)]
    {:frame (:frame state)
     :game (dissoc game :grid)
     :player (player session)
     :movers (movers session)
     :pellet-count (count (pellets session))}))

(defn -main [& _args]
  (start))
