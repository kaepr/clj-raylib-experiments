(ns raylib.window
  (:require [coffi.mem :as mem]
            [raylib.core]
            [raylib.internals :as internals]
            [coffi.ffi :refer [defcfn]]))

(defcfn init-window
  "InitWindow"
  [::mem/int ::mem/int ::mem/c-string] ::mem/void)

(defcfn close-window
  "CloseWindow"
  [] ::mem/void)

(defcfn window-should-close?
  "WindowShouldClose"
  [] ::internals/bool)
