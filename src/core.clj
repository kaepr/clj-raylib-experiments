(ns core
  (:require
   [raylib.core :as rc]
   [raylib.nrepl :as nrepl]
   [raylib.colors :as rcc]))

;; Trying to follow steps from
;; An Introduction to Raylib by Coding with Sphere
;; https://www.youtube.com/watch?v=AniAoJC6QAc

(defn init []
  (rc/init-window 600 400 "awesome window"))

(defn draw []
  (rc/begin-drawing)
  (rc/clear-background rcc/skyblue)
  (rc/end-drawing))

(defn start []
  (nrepl/start {:port 7888})
  (init)
  (loop []
    (when-not (rc/window-should-close?)
      (draw)
      (recur)))
  (rc/close-window))

(defn -main [& args]
  (start))

(comment

  (start)

  ())
