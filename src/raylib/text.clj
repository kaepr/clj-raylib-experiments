(ns raylib.text
  (:require
   [raylib.core]
   [raylib.structs :as structs]
   [raylib.loader :as loader]
   [coffi.mem :as mem]
   [coffi.ffi :refer [defcfn]]))

(loader/ensure-loaded!)

(defcfn draw-text
  {:arglists '([text pos-x pos-y font-size color])}
  "DrawText"
  [::mem/c-string ::mem/int ::mem/int ::mem/int ::structs/color] ::mem/void)
