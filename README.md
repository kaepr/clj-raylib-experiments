# Clojure Raylib Experiments

Using Clojure to learn Raylib.

Based on

- [ertugrulcetin/raylib-clojure-playground](https://github.com/ertugrulcetin/raylib-clojure-playground) 
- [IGJoshua/coffi](https://github.com/IGJoshua/coffi)


## Requirements 

Have JDK22+ and Clojure installed locally.

I am using [sdkman](https://sdkman.io/) to manage Java versions locally.

I have only added libs for MacOS as that's what I am using.

Please take a look at [ertugrulcetin/raylib-clojure-playground](https://github.com/ertugrulcetin/raylib-clojure-playground/tree/master) for a more thorough setup and installation instructions.

## Run locally

``` shell
bb dev # or clj -M:dev
```

This will launch a game window defined in `src/core.clj` and start's an nrepl server at port `7888`.

## Clear Code Tutorial

I am using this repository to follow [The ultimate introduction to Raylib by Clear Code](https://www.youtube.com/watch?v=UoAsDlUwjy0)

Copy the `start` assets into `./assets` directory

https://github.com/clear-code-projects/raylib_intro

I have added them to `.gitignore`

Run the individual files from the tutorial series.

```shell
bb clear-code-dev

bb clear-code-dev clear-code.move
```

