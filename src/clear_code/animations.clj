(ns clear-code.animations
  (:require [raylib.core :as raylib]
            [raylib.shapes :as shapes]
            [raylib.textures :as textures]
            [raylib.audio :as audio]
            [raylib.input :as input]
            [raylib.keys :as keys]
            [raylib.colors :as colors]))

(def screen-width 1280)
(def screen-height 720)

;; Keeps the running game inspectable and editable from the REPL.
(defonce state (atom nil))

;; ---------------------------------------------------------------------------
;; Assets

(defn load-assets []
  {:animation-frames (mapv
                      (fn [i]
                        (textures/load-texture
                         (str "assets/start/basics/assets/animation/" i ".png")))
                      (range 8))})

(defn unload-assets! [{:keys [animation-frames]}]
  (doseq [texture animation-frames]
    (textures/unload-texture texture)))

;; ---------------------------------------------------------------------------
;; State

(defn initial-state [assets]
  {:animation-index 0
   :assets assets})

;; ---------------------------------------------------------------------------
;; Input

(defn read-input []
  {})

;; ---------------------------------------------------------------------------
;; Update

(defn update-animation-index [animation-index input delta-time]
  (+ animation-index (* 5 delta-time)))

(defn update-state [state input delta-time]
  (update-in state [:animation-index]
             update-animation-index (:animation-index state) delta-time))

;; ---------------------------------------------------------------------------
;; Drawing

(defn draw [{:keys [assets animation-index]}]
  (let [frame-index  (mod (int animation-index) 8)
        frame (nth (:animation-frames assets) frame-index)]
    (raylib/begin-drawing)
    (raylib/clear-background colors/black)
    (textures/draw-texture frame 0 0 colors/white)
    (raylib/end-drawing)))

;; ---------------------------------------------------------------------------
;; Frame and lifecycle

(defn run-frame! []
  (let [input (read-input)
        delta-time (raylib/get-frame-time)
        old-state @state
        new-state (swap! state update-state input delta-time)]
    (draw new-state)))

(defn init! []
  (raylib/init-window screen-width screen-height "Animations")
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
