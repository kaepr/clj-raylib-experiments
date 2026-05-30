(ns raylib.draw
  (:require [coffi.mem :as mem]
            [raylib.core]
            [raylib.structs :as structs]
            [coffi.ffi :refer [defcfn]]))

(defcfn begin-drawing
  "BeginDrawing"
  [] ::mem/void)

(defcfn clear-background
  "ClearBackground"
  [::structs/color] ::mem/void)

(defcfn end-drawing
  "EndDrawing"
  [] ::mem/void)
