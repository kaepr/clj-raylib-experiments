(ns raylib.text
  (:require
   [raylib.core]
   [raylib.structs :as structs]
   [raylib.loader :as loader]
   [coffi.mem :as mem]
   [coffi.ffi :refer [defcfn]]))

(loader/ensure-loaded!)

(defcfn draw-fps
  {:arglists '([pos-x pos-y])}
  "DrawFPS"
  [::mem/int ::mem/int] ::mem/void)

(defcfn draw-text
  {:arglists '([text pos-x pos-y font-size color])}
  "DrawText"
  [::mem/c-string ::mem/int ::mem/int ::mem/int ::structs/color] ::mem/void)
