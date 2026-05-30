(ns raylib.structs
  (:require
   [coffi.mem :as mem :refer [defalias]]
   [raylib.internals :as ri]))

(defalias ::color
  [::mem/struct
   [[:r ::ri/ubyte]
    [:g ::ri/ubyte]
    [:b ::ri/ubyte]
    [:a ::ri/ubyte]]])
