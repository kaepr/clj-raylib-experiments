(ns clear-code.audio
  (:require [raylib.core :as rc]
            [raylib.shapes :as rs]
            [raylib.textures :as rt]
            [raylib.text :as rtext]
            [raylib.audio :as raudio]
            [raylib.colors :as colors]))

(defn init []
  (rc/init-window 1024 768 "Audio")
  (raudio/init-audio-device))

(defn load-audio []
  {:laser (raudio/load-sound "assets/start/basics/assets/laser.wav")
   :music (raudio/load-music-stream "assets/start/basics/assets/music.wav")})

(defn start-state []
  {:audio (load-audio)})

(defn update-state [state]
  state)

(defn unload-audio [{:keys [music]}]
  (raudio/unload-music-stream music))

(defn cleanup-state [{:keys [audio]}]
  (unload-audio audio))

(defn draw [{:keys [audio]}]
  (let [{:keys [music laser]} audio]
    ;; (raudio/play-music-stream music)
    (raudio/update-music-stream music)
    ;; (raudio/play-sound laser)
    (rc/begin-drawing)
    (rc/clear-background colors/black)
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
        (raudio/close-audio-device)
        (rc/close-window)))))

(comment

  (start)

  ())
