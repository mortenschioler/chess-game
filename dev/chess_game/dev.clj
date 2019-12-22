(ns chess-game.dev
  (:require [chess-game.core :refer :all]
            [chess-game.fen :as fen]
            [chess-game.util :as util]
            [chess-board.squares :refer :all]
            [chess-board.core :refer :all]))

(defmacro ^{:private true} defrules
  []
  `(do 
     ~@(doall 
        (map 
          (fn [rule] `(def ~(symbol (:id rule)) ~(:predicate rule)))
          rules))))
(defrules)

(def new-game (util/new-game))
