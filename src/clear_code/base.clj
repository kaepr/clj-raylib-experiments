(ns clear-code.base
  (:require [raylib.core :as rc]
            [raylib.shapes :as rs]
            [raylib.textures :as rt]
            [raylib.text :as rtext]
            [raylib.colors :as colors]))

;; Trying to follow steps from
;; The ultimate introduction to Raylib [ 2D & 3D game dev ]
;; https://www.youtube.com/watch?v=UoAsDlUwjy0

(defn init []
  (rc/init-window 1024 768 "Clear Code"))

(defn load-assets []
  (let [spaceship-texture (rt/load-texture "assets/start/basics/assets/spaceship.png")
        spaceship-image (rt/load-image "assets/start/basics/assets/spaceship.png")
        spaceship-image-gray (rt/image-color-grayscale! spaceship-image)
        spaceship-texture-gray (rt/load-texture-from-image spaceship-image-gray)]
    {:spaceship-texture spaceship-texture
     :spaceship-image spaceship-image
     :spaceship-image-gray spaceship-image-gray
     :spaceship-texture-gray spaceship-texture-gray}))

(defn start-state []
  {:assets (load-assets)})

(defn update-state [state]
  state)

(defn unload-assets [assets]
  (let [{:keys [spaceship-texture spaceship-image spaceship-image-gray spaceship-texture-gray]} assets]
    (rt/unload-texture spaceship-texture)
    (rt/unload-texture spaceship-texture-gray)
    ;; (rt/unload-image spaceship-image)
    (rt/unload-image spaceship-image-gray)))

(defn cleanup-state [state]
  (unload-assets (:assets state)))

(defn draw [{:keys [assets]}]
  (let [{:keys [spaceship-texture spaceship-image spaceship-image-gray spaceship-texture-gray]} assets]
    (rc/begin-drawing)
    (rc/clear-background colors/black)
    (rs/draw-line-ex {:x 0 :y 0} {:x 500 :y 200} 10 colors/red)
    (rs/draw-pixel 100 200 colors/red)
    (rs/draw-pixel-v {:x 101 :y 200} colors/blue)
    (rs/draw-circle 400 200 10 colors/green)
    (rs/draw-circle-v {:x 100 :y 600} 20 colors/yellow)
    (rt/draw-texture spaceship-texture 0 0 colors/white)
    (rt/draw-texture-v spaceship-texture-gray {:x 100 :y 0} colors/white)
    (rtext/draw-text "Some Text"  0 400 100 colors/white)
    (rc/end-drawing)))

(defn start []
  (init)
  (let [state (start-state)]
    (try
      (loop [state state]
        (when-not (rc/window-should-close?)
          (draw state)
          (recur (update-state state))))
      (finally
        (cleanup-state state)
        (rc/close-window)))))

(comment

  (start)

  ())
