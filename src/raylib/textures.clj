(ns raylib.textures
  (:require
   [raylib.core]
   [raylib.structs :as structs]
   [raylib.loader :as loader]
   [coffi.mem :as mem]
   [coffi.ffi :refer [defcfn]]))

(loader/ensure-loaded!)

(defcfn load-image
  {:arglists '([filename])}
  "LoadImage"
  [::mem/c-string] ::structs/image)

(defcfn unload-image
  {:arglists '([image])}
  "UnloadImage"
  [::structs/image] ::mem/void)

;; Adding a ! now because it mutatates something held by Clojure
;; I am aware of my inconsistencies
(defcfn image-color-grayscale!
  {:arglists '([image])}
  "ImageColorGrayscale"
  [::mem/pointer] ::mem/void
  native-fn
  [image]
  (with-open [arena (mem/confined-arena)]
    (let [image-ptr (mem/serialize image [::mem/pointer ::structs/image] arena)]
      (native-fn image-ptr)
      (mem/deserialize image-ptr [::mem/pointer ::structs/image]))))

(defcfn load-texture
  {:arglists '([filename])}
  "LoadTexture"
  [::mem/c-string] ::structs/texture)

(defcfn load-texture-from-image
  {:arglists '([image])}
  "LoadTextureFromImage"
  [::structs/image] ::structs/texture)

(defcfn draw-texture
  {:arglists '([texture pos-x pos-y tint])}
  "DrawTexture"
  [::structs/texture ::mem/int ::mem/int ::structs/color] ::mem/void)

(defcfn draw-texture-v
  {:arglists '([texture position tint])}
  "DrawTextureV"
  [::structs/texture ::structs/vector-2 ::structs/color] ::mem/void)

(defcfn unload-texture
  {:arglists '([texture])}
  "UnloadTexture"
  [::structs/texture] ::mem/void)

(defcfn unload-image
  {:arglists '([image])}
  "UnloadImage"
  [::structs/image] ::mem/void)
