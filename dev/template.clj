(ns template
  (:require [raylib.core :as raylib]
            [raylib.shapes :as shapes]
            [raylib.textures :as textures]
            [raylib.audio :as audio]
            [raylib.input :as input]
            [raylib.keys :as keys]
            [raylib.colors :as colors])))

(def screen-width 1280)
(def screen-height 720)

;; Keeps the running game inspectable and editable from the REPL.
(defonce state (atom nil))

;; ---------------------------------------------------------------------------
;; Assets

(defn load-assets []
  {:textures
   {:player (textures/load-texture "assets/player.png")
    :enemy  (textures/load-texture "assets/enemy.png")}

   :sounds
   {:jump (audio/load-sound "assets/audio/jump.wav")
    :hit  (audio/load-sound "assets/audio/hit.wav")}

   :music
   {:background
    (audio/load-music-stream "assets/audio/background.ogg")}})

(defn unload-assets! [{:keys [textures sounds music]}]
  (doseq [texture (vals textures)]
    (textures/unload-texture texture))

  (doseq [sound (vals sounds)]
    (audio/unload-sound sound))

  (doseq [music-stream (vals music)]
    (audio/unload-music-stream music-stream)))

(defn texture [state asset-key]
  (get-in state [:assets :textures asset-key]))

(defn sound [state asset-key]
  (get-in state [:assets :sounds asset-key]))

(defn music [state asset-key]
  (get-in state [:assets :music asset-key]))

(defn update-music-streams! [state]
  (doseq [music-stream (vals (get-in state [:assets :music]))]
    (audio/update-music-stream music-stream)))

;; ---------------------------------------------------------------------------
;; State

(defn initial-game-state []
  {:player
   {:position {:x 100.0 :y 100.0}
    :direction {:x 0.0 :y 0.0}
    :speed 300.0
    :texture :player}

   :enemies []
   :score 0
   :paused? false})

(defn initial-state [assets]
  {:assets assets
   :game (initial-game-state)})

;; Reset gameplay without reloading or leaking native assets.
(defn reset-game! []
  (swap! state assoc :game (initial-game-state)))

;; ---------------------------------------------------------------------------
;; Input

(defn bool->int [value]
  (if value 1 0))

(defn normalize-vector-2 [{:keys [x y] :as vector}]
  (let [length (Math/sqrt (+ (* x x) (* y y)))]
    (if (zero? length)
      vector
      {:x (float (/ x length))
       :y (float (/ y length))})))

(defn read-input []
  {:movement
   (normalize-vector-2
    {:x (- (bool->int (input/key-down? keys/right))
           (bool->int (input/key-down? keys/left)))
     :y (- (bool->int (input/key-down? keys/down))
           (bool->int (input/key-down? keys/up)))})

   :jump? (input/key-pressed? keys/space)
   :reset? (input/key-pressed? keys/r)
   :mouse-position (input/get-mouse-position)})

;; ---------------------------------------------------------------------------
;; Update

(defn update-player [player input delta-time]
  (let [{:keys [x y] :as direction} (:movement input)
        distance (* (:speed player) delta-time)]
    (-> player
        (assoc :direction direction)
        (update-in [:position :x] + (* x distance))
        (update-in [:position :y] + (* y distance)))))

;; Keep this function pure. swap! may invoke it more than once.
(defn update-state [state input delta-time]
  (if (get-in state [:game :paused?])
    state
    (update-in state [:game :player]
               update-player input delta-time)))

(defn handle-events! [old-state new-state input]
  ;; Native side effects happen outside update-state.
  (when (:jump? input)
    (audio/play-sound (sound new-state :jump)))

  (when (:reset? input)
    (reset-game!)))

;; ---------------------------------------------------------------------------
;; Drawing

(defn draw-player [state]
  (let [{:keys [position texture]} (get-in state [:game :player])]
    (textures/draw-texture-v
     (texture state texture)
     position
     colors/white)))

(defn draw [state]
  (raylib/begin-drawing)
  (raylib/clear-background colors/black)

  (draw-player state)
  ;; Draw world, enemies, UI, debug information, etc.
  ;; (text/draw-fps 10 10)

  (raylib/end-drawing))

;; ---------------------------------------------------------------------------
;; Frame and lifecycle

(defn run-frame! []
  (let [input (read-input)
        delta-time (raylib/get-frame-time)
        old-state @state
        new-state (swap! state update-state input delta-time)]
    (update-music-streams! new-state)
    (handle-events! old-state new-state input)
    (draw new-state)))

(defn init! []
  (raylib/init-window screen-width screen-height "My Game")
  (raylib/set-target-fps 60)
  (audio/init-audio-device))

(defn start []
  (init!)
  (try
    (let [assets (load-assets)]
      (reset! state (initial-state assets))
      (audio/play-music-stream (music @state :background))

      (try
        (loop []
          (when-not (raylib/window-should-close?)
            (run-frame!)
            (recur)))
        (finally
          (unload-assets! assets)
          (reset! state nil))))
    (finally
      (audio/close-audio-device)
      (raylib/close-window))))

(defn -main [& _args]
  (start))
