(ns clear-code.main
  (:require
   [raylib.nrepl :as nrepl]
   [clojure.string :as str]))

;; Trying to follow steps from
;; The ultimate introduction to Raylib [ 2D & 3D game dev ]
;; https://www.youtube.com/watch?v=UoAsDlUwjy0

(def default-scene 'clear-code.base)

(defn resolve-start [scene-ns]
  (let [start-sym (symbol (str scene-ns) "start")]
    (or (requiring-resolve start-sym)
        (throw (ex-info "Scene does not define start"
                        {:scene scene-ns
                         :expected start-sym})))))

(defn scene-symbol [scene]
  (if (str/blank? scene)
    default-scene
    (symbol scene)))

(defn -main [& [scene]]
  (let [scene (scene-symbol scene)
        start (resolve-start scene)]
    (nrepl/start {:port 7888})
    (start)))

(comment

  (start)

  ())
