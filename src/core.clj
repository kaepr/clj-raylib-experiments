(ns core
  (:require [raylib.window :as rcw]
            [raylib.draw :as rcd]
            [raylib.nrepl :as nrepl]
            [raylib.colors :as rcc]))

;; Trying to follow steps from
;; An Introduction to Raylib by Coding with Sphere
;; https://www.youtube.com/watch?v=AniAoJC6QAc

(defn init []
  (rcw/init-window 600 400 "awesome window"))

(defn draw []
  (rcd/begin-drawing)
  (rcd/clear-background rcc/skyblue)
  (rcd/end-drawing))

(defn start []
  (nrepl/start {:port 7888})
  (init)
  (loop []
    (when-not (rcw/window-should-close?)
      (draw)
      (recur)))
  (rcw/close-window))

(defn -main [& args]
  (start))

(comment

  (start)

  ())
