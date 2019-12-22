(ns chess-game.util
  (:require [chess-game.fen :as fen]))

(defn new-game
  []
  (fen/read-game fen/starting-position))
