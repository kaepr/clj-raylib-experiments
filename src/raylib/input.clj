(ns raylib.input
  (:require [coffi.mem :as mem]
            [coffi.ffi :refer [defcfn]]
            [raylib.loader :as loader]
            [raylib.structs :as structs]
            [raylib.internals :as internals]))

(loader/ensure-loaded!)

(defcfn key-pressed?
  {:arglists '([key])}
  "IsKeyPressed"
  [::mem/int] ::internals/bool)

(defcfn key-pressed-repeat?
  {:arglists '([key])}
  "IsKeyPressedRepeat"
  [::mem/int] ::internals/bool)

(defcfn key-down?
  {:arglists '([key])}
  "IsKeyDown"
  [::mem/int] ::internals/bool)

(defcfn key-released?
  {:arglists '([key])}
  "IsKeyReleased"
  [::mem/int] ::internals/bool)

(defcfn key-up?
  {:arglists '([key])}
  "IsKeyUp"
  [::mem/int] ::internals/bool)

(defcfn get-key-pressed
  {:arglists '([])}
  "GetKeyPressed"
  [] ::mem/int)

(defcfn get-char-pressed
  {:arglists '([])}
  "GetCharPressed"
  [] ::mem/int)

(defcfn set-exit-key
  {:arglists '([key])}
  "SetExitKey"
  [::mem/int] ::mem/void)

(defcfn mouse-button-pressed?
  {:arglists '([button])}
  "IsMouseButtonPressed"
  [::mem/int] ::internals/bool)

(defcfn mouse-button-down?
  {:arglists '([button])}
  "IsMouseButtonDown"
  [::mem/int] ::internals/bool)

(defcfn mouse-button-released?
  {:arglists '([button])}
  "IsMouseButtonReleased"
  [::mem/int] ::internals/bool)

(defcfn mouse-button-up?
  {:arglists '([button])}
  "IsMouseButtonUp"
  [::mem/int] ::internals/bool)

(defcfn get-mouse-x
  {:arglists '([])}
  "GetMouseX"
  [] ::mem/int)

(defcfn get-mouse-y
  {:arglists '([])}
  "GetMouseY"
  [] ::mem/int)

(defcfn get-mouse-position
  {:arglists '([])}
  "GetMousePosition"
  [] ::structs/vector-2)

(defcfn get-mouse-delta
  {:arglists '([])}
  "GetMouseDelta"
  [] ::structs/vector-2)

(defcfn set-mouse-position
  {:arglists '([x y])}
  "SetMousePosition"
  [::mem/int ::mem/int] ::mem/void)

(defcfn set-mouse-offset
  {:arglists '([offset-x offset-y])}
  "SetMouseOffset"
  [::mem/int ::mem/int] ::mem/void)

(defcfn set-mouse-scale
  {:arglists '([scale-x scale-y])}
  "SetMouseScale"
  [::mem/float ::mem/float] ::mem/void)

(defcfn get-mouse-wheel-move
  {:arglists '([])}
  "GetMouseWheelMove"
  [] ::mem/float)

(defcfn get-mouse-wheel-move-v
  {:arglists '([])}
  "GetMouseWheelMoveV"
  [] ::structs/vector-2)

(defcfn set-mouse-cursor
  {:arglists '([cursor])}
  "SetMouseCursor"
  [::mem/int] ::mem/void)
