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

(defalias ::vector-2
  [::mem/struct
   [[:x ::mem/float]
    [:y ::mem/float]]])

(defalias ::vector-3
  [::mem/struct
   [[:x ::mem/float]
    [:y ::mem/float]
    [:z ::mem/float]]])

(defalias ::vector-4
  [::mem/struct
   [[:x ::mem/float]
    [:y ::mem/float]
    [:z ::mem/float]
    [:w ::mem/float]]])

(defalias ::texture
  [::mem/struct
   [[:id ::mem/int]
    [:width ::mem/int]
    [:height ::mem/int]
    [:mipmaps ::mem/int]
    [:format ::mem/int]]])

(defalias ::render-texture
  [::mem/struct
   [[:id ::mem/int]
    [:texture ::texture]
    [:depth ::texture]]])

(defalias ::image
  [::mem/struct
   [[:data ::mem/pointer]
    [:width ::mem/int]
    [:height ::mem/int]
    [:mipmaps ::mem/int]
    [:format ::mem/int]]])

(defalias ::rectangle
  [::mem/struct
   [[:x ::mem/float]
    [:y ::mem/float]
    [:width ::mem/float]
    [:height ::mem/float]]])

(defalias ::camera-2d
  [::mem/struct
   [[:offset ::vector-2]
    [:target ::vector-2]
    [:rotation ::mem/float]
    [:zoom ::mem/float]]])


(defalias ::wave
  [::mem/struct
   [[:frame-count ::ri/uint]
    [:sample-rate ::ri/uint]
    [:sample-size ::ri/uint]
    [:channels ::ri/uint]
    [:data ::mem/pointer]]])

(defalias ::audio-stream
  [::mem/struct
   [[:buffer ::mem/pointer]
    [:processor ::mem/pointer]
    [:sample-rate ::ri/uint]
    [:sample-size ::ri/uint]
    [:channels ::ri/uint]
    [:__padding [::mem/padding 4]]]])

(defalias ::sound
  [::mem/struct
   [[:stream ::audio-stream]
    [:frame-count ::ri/uint]
    [:__padding [::mem/padding 4]]]])

(defalias ::music
  [::mem/struct
   [[:stream ::audio-stream]
    [:frame-count ::ri/uint]
    [:looping ::ri/bool]
    [:__padding-1 [::mem/padding 3]]
    [:ctx-type ::mem/int]
    [:__padding-2 [::mem/padding 4]]
    [:ctx-data ::mem/pointer]]])
