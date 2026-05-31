(ns raylib.core
  (:require [coffi.mem :as mem]
            [raylib.core]
            [raylib.loader :as loader]
            [raylib.structs :as structs]
            [raylib.internals :as internals]
            [coffi.ffi :refer [defcfn]]))

(loader/ensure-loaded!)

(defcfn init-window
  {:arglists '([width height title])}
  "InitWindow"
  [::mem/int ::mem/int ::mem/c-string] ::mem/void)

(defcfn close-window
  "CloseWindow"
  [] ::mem/void)

(defcfn window-should-close?
  "WindowShouldClose"
  [] ::internals/bool)

(defcfn begin-drawing
  "BeginDrawing"
  [] ::mem/void)

(defcfn clear-background
  {:arglists '([color])}
  "ClearBackground"
  [::structs/color] ::mem/void)

(defcfn end-drawing
  "EndDrawing"
  [] ::mem/void)
(defcfn set-target-fps
  {:arglists '([fps])}
  "SetTargetFPS"
  [::mem/int] ::mem/void)

(defcfn get-frame-time
  {:arglists '([])}
  "GetFrameTime"
  [] ::mem/float)

(defcfn get-time
  {:arglists '([])}
  "GetTime"
  [] ::mem/double)

(defcfn get-fps
  {:arglists '([])}
  "GetFPS"
  [] ::mem/int)
