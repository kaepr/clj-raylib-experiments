(ns raylib.audio
  (:require [coffi.mem :as mem]
            [coffi.ffi :refer [defcfn]]
            [raylib.loader :as loader]
            [raylib.structs :as structs]
            [raylib.internals :as internals]))

(loader/ensure-loaded!)

;; Audio device

(defcfn init-audio-device
  {:arglists '([])}
  "InitAudioDevice"
  [] ::mem/void)

(defcfn close-audio-device
  {:arglists '([])}
  "CloseAudioDevice"
  [] ::mem/void)

(defcfn audio-device-ready?
  {:arglists '([])}
  "IsAudioDeviceReady"
  [] ::internals/bool)

(defcfn set-master-volume
  {:arglists '([volume])}
  "SetMasterVolume"
  [::mem/float] ::mem/void)

(defcfn get-master-volume
  {:arglists '([])}
  "GetMasterVolume"
  [] ::mem/float)

;; Wave

(defcfn load-wave
  {:arglists '([filename])}
  "LoadWave"
  [::mem/c-string] ::structs/wave)

(defcfn wave-valid?
  {:arglists '([wave])}
  "IsWaveValid"
  [::structs/wave] ::internals/bool)

(defcfn unload-wave
  {:arglists '([wave])}
  "UnloadWave"
  [::structs/wave] ::mem/void)

(defcfn load-sound-from-wave
  {:arglists '([wave])}
  "LoadSoundFromWave"
  [::structs/wave] ::structs/sound)

(defcfn wave-copy
  {:arglists '([wave])}
  "WaveCopy"
  [::structs/wave] ::structs/wave)

;; Sound effects

(defcfn load-sound
  {:arglists '([filename])}
  "LoadSound"
  [::mem/c-string] ::structs/sound)

(defcfn sound-valid?
  {:arglists '([sound])}
  "IsSoundValid"
  [::structs/sound] ::internals/bool)

(defcfn unload-sound
  {:arglists '([sound])}
  "UnloadSound"
  [::structs/sound] ::mem/void)

(defcfn play-sound
  {:arglists '([sound])}
  "PlaySound"
  [::structs/sound] ::mem/void)

(defcfn stop-sound
  {:arglists '([sound])}
  "StopSound"
  [::structs/sound] ::mem/void)

(defcfn pause-sound
  {:arglists '([sound])}
  "PauseSound"
  [::structs/sound] ::mem/void)

(defcfn resume-sound
  {:arglists '([sound])}
  "ResumeSound"
  [::structs/sound] ::mem/void)

(defcfn sound-playing?
  {:arglists '([sound])}
  "IsSoundPlaying"
  [::structs/sound] ::internals/bool)

(defcfn set-sound-volume
  {:arglists '([sound volume])}
  "SetSoundVolume"
  [::structs/sound ::mem/float] ::mem/void)

(defcfn set-sound-pitch
  {:arglists '([sound pitch])}
  "SetSoundPitch"
  [::structs/sound ::mem/float] ::mem/void)

(defcfn set-sound-pan
  {:arglists '([sound pan])}
  "SetSoundPan"
  [::structs/sound ::mem/float] ::mem/void)

;; Music streams

(defcfn load-music-stream
  {:arglists '([filename])}
  "LoadMusicStream"
  [::mem/c-string] ::structs/music)

(defcfn music-valid?
  {:arglists '([music])}
  "IsMusicValid"
  [::structs/music] ::internals/bool)

(defcfn unload-music-stream
  {:arglists '([music])}
  "UnloadMusicStream"
  [::structs/music] ::mem/void)

(defcfn play-music-stream
  {:arglists '([music])}
  "PlayMusicStream"
  [::structs/music] ::mem/void)

(defcfn update-music-stream
  {:arglists '([music])}
  "UpdateMusicStream"
  [::structs/music] ::mem/void)

(defcfn stop-music-stream
  {:arglists '([music])}
  "StopMusicStream"
  [::structs/music] ::mem/void)

(defcfn pause-music-stream
  {:arglists '([music])}
  "PauseMusicStream"
  [::structs/music] ::mem/void)

(defcfn resume-music-stream
  {:arglists '([music])}
  "ResumeMusicStream"
  [::structs/music] ::mem/void)

(defcfn music-stream-playing?
  {:arglists '([music])}
  "IsMusicStreamPlaying"
  [::structs/music] ::internals/bool)

(defcfn seek-music-stream
  {:arglists '([music position])}
  "SeekMusicStream"
  [::structs/music ::mem/float] ::mem/void)

(defcfn set-music-volume
  {:arglists '([music volume])}
  "SetMusicVolume"
  [::structs/music ::mem/float] ::mem/void)

(defcfn set-music-pitch
  {:arglists '([music pitch])}
  "SetMusicPitch"
  [::structs/music ::mem/float] ::mem/void)

(defcfn set-music-pan
  {:arglists '([music pan])}
  "SetMusicPan"
  [::structs/music ::mem/float] ::mem/void)

(defcfn get-music-time-length
  {:arglists '([music])}
  "GetMusicTimeLength"
  [::structs/music] ::mem/float)

(defcfn get-music-time-played
  {:arglists '([music])}
  "GetMusicTimePlayed"
  [::structs/music] ::mem/float)
