(ns chess-game.core
  (:require [chess-board.core :as board]))

(defmulti legal-destination-squares (fn [game from piece] (:piece-type piece)))

(defmethod legal-destination-squares :default
  [game from piece]
  (set (range 64)))

(def rules
  [{:id :active-player-to-move 
    :predicate (fn [game move] (= (:side-to-move game) (:player move)))}
   {:id :squares-are-legal
    :predicate (fn [game move] (every? board/square? [(:from move) (:to move)]))}
   {:id :player-owns-piece
    :predicate (fn [game move] (= (:piece-color (board/get-piece (:board game) (:from move))) (:player move)))}
   {:id :target-square-is-empty-or-enemy-piece
    :predicate (fn [game move] (let [dest (board/get-piece (:board game) (:to move))]
                                 (or (nil? dest) (not= (:player move) (:piece-color dest)))))}
   {:id :game-is-not-over
    :predicate (fn [game move] (not (:result game)))}

   {:id :piece-movement-is-allowed
    :predicate (fn [game move] 
                 (contains?
                  (legal-destination-squares 
                    game 
                    (:from move) 
                    (board/get-piece game (:from move)))
                  (:to move)))}
   ])

(defn abides?
  [game move rule]
  ((:predicate rule) game move))

(defn legal? 
  [game move]
  (every? (partial abides? game move) rules))

(defn explain
  [game move]
  {:violated-rules (->> rules
                        (filter (complement (partial abides? game move)))
                        (map :id))})
