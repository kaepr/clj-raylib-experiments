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
