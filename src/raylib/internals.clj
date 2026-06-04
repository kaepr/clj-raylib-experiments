(ns raylib.internals
  (:require [coffi.mem :as mem]))

;; ubyte
(defmethod mem/primitive-type ::ubyte
  [_type]
  ::mem/byte)

(defmethod mem/serialize* ::ubyte
  [obj _type _scope]
  (unchecked-byte obj))

(defmethod mem/deserialize* ::ubyte
  [obj _type]
  (Byte/toUnsignedLong obj))

;; bool
(defmethod mem/primitive-type ::bool
  [_type]
  ::mem/byte)

(defmethod mem/serialize* ::bool
  [obj _type _scope]
  (byte (if obj 1 0)))

(defmethod mem/deserialize* ::bool
  [obj _type]
  (not (zero? obj)))

;; uint
(defmethod mem/primitive-type ::uint
  [_type]
  ::mem/int)

(defmethod mem/serialize* ::uint
  [obj _type _scope]
  (unchecked-int obj))

(defmethod mem/deserialize* ::uint
  [obj _type]
  (Integer/toUnsignedLong obj))
