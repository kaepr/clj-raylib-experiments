(ns raylib.shapes
  (:require [coffi.mem :as mem]
            [raylib.core]
            [raylib.loader :as loader]
            [raylib.structs :as structs]
            [raylib.internals :as internals]
            [coffi.ffi :refer [defcfn]]))

(loader/ensure-loaded!)

(defcfn draw-pixel
  {:arglists '([pos-x pos-y color])}
  "DrawPixel"
  [::mem/int ::mem/int ::structs/color] ::mem/void)

(defcfn draw-pixel-v
  {:arglists '([pos color])}
  "DrawPixelV"
  [::structs/vector-2 ::structs/color] ::mem/void)

(defcfn draw-line
  {:arglists '([start-pos-x start-pos-y end-pos-x end-pos-y color])}
  "DrawLine"
  [::mem/int ::mem/int ::mem/int ::mem/int ::structs/color] ::mem/void)

(defcfn draw-line-v
  {:arglists '([start-pos end-pos color])}
  "DrawLineV"
  [::structs/vector-2 ::structs/vector-2 ::structs/color] ::mem/void)

(defcfn draw-line-ex
  {:arglists '([start-pos end-pos thick color])}
  "DrawLineEx"
  [::structs/vector-2 ::structs/vector-2 ::mem/float ::structs/color] ::mem/void)

(defcfn draw-circle
  {:arglists '([center-x center-y radius color])}
  "DrawCircle"
  [::mem/int ::mem/int ::mem/float ::structs/color] ::mem/void)

(defcfn draw-circle-v
  {:arglists '([center radius color])}
  "DrawCircleV"
  [::structs/vector-2 ::mem/float ::structs/color] ::mem/void)

(defcfn check-collision-recs?
  {:arglists '([rec1 rec2])}
  "CheckCollisionRecs"
  [::structs/rectangle ::structs/rectangle] ::internals/bool)

(defcfn check-collision-circles?
  {:arglists '([center1 radius1 center2 radius2])}
  "CheckCollisionCircles"
  [::structs/vector-2 ::mem/float ::structs/vector-2 ::mem/float] ::internals/bool)

(defcfn check-collision-circle-rec?
  {:arglists '([center radius rec])}
  "CheckCollisionCircleRec"
  [::structs/vector-2 ::mem/float ::structs/rectangle] ::internals/bool)

(defcfn check-collision-circle-line?
  {:arglists '([center radius p1 p2])}
  "CheckCollisionCircleLine"
  [::structs/vector-2 ::mem/float ::structs/vector-2 ::structs/vector-2] ::internals/bool)

(defcfn check-collision-point-rec?
  {:arglists '([point rec])}
  "CheckCollisionPointRec"
  [::structs/vector-2 ::structs/rectangle] ::internals/bool)

(defcfn check-collision-point-circle?
  {:arglists '([point center radius])}
  "CheckCollisionPointCircle"
  [::structs/vector-2 ::structs/vector-2 ::mem/float] ::internals/bool)

(defcfn check-collision-point-triangle?
  {:arglists '([point p1 p2 p3])}
  "CheckCollisionPointTriangle"
  [::structs/vector-2 ::structs/vector-2 ::structs/vector-2 ::structs/vector-2] ::internals/bool)

(defcfn check-collision-point-line?
  {:arglists '([point p1 p2 threshold])}
  "CheckCollisionPointLine"
  [::structs/vector-2 ::structs/vector-2 ::structs/vector-2 ::mem/int] ::internals/bool)

(defcfn check-collision-point-poly?
  {:arglists '([point points])}
  "CheckCollisionPointPoly"
  [::structs/vector-2 ::mem/pointer ::mem/int] ::internals/bool
  native-fn
  [point points]
  (with-open [arena (mem/confined-arena)]
    (let [point-count (count points)
          points-ptr (mem/serialize points [::mem/array ::structs/vector-2 point-count] arena)]
      (native-fn point points-ptr point-count))))

(defcfn check-collision-lines-raw?
  {:arglists '([start-pos1 end-pos1 start-pos2 end-pos2 collision-point-ptr])}
  "CheckCollisionLines"
  [::structs/vector-2 ::structs/vector-2 ::structs/vector-2 ::structs/vector-2 ::mem/pointer]
  ::internals/bool)

(defn check-collision-lines
  {:arglists '([start-pos1 end-pos1 start-pos2 end-pos2])}
  [start-pos1 end-pos1 start-pos2 end-pos2]
  (with-open [arena (mem/confined-arena)]
    (let [collision-point-ptr (mem/alloc-instance ::structs/vector-2 arena)
          collision? (check-collision-lines-raw?
                      start-pos1 end-pos1
                      start-pos2 end-pos2
                      collision-point-ptr)]
      (when collision?
        (mem/deserialize collision-point-ptr ::structs/vector-2)))))

(defcfn get-collision-rec
  {:arglists '([rec1 rec2])}
  "GetCollisionRec"
  [::structs/rectangle ::structs/rectangle] ::structs/rectangle)

(defcfn draw-rectangle
  {:arglists '([pos-x pos-y width height color])}
  "DrawRectangle"
  [::mem/int ::mem/int ::mem/int ::mem/int ::structs/color] ::mem/void)

(defcfn draw-rectangle-v
  {:arglists '([position size color])}
  "DrawRectangleV"
  [::structs/vector-2 ::structs/vector-2 ::structs/color] ::mem/void)

(defcfn draw-rectangle-rec
  {:arglists '([rec color])}
  "DrawRectangleRec"
  [::structs/rectangle ::structs/color] ::mem/void)

(defcfn draw-rectangle-pro
  {:arglists '([rec origin rotation color])}
  "DrawRectanglePro"
  [::structs/rectangle ::structs/vector-2 ::mem/float ::structs/color] ::mem/void)
