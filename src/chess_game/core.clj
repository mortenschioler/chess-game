(ns chess-game.core
  (:require [chess-board.core :as board]))

(defmulti legal-destination-squares (fn [game from piece] (:piece-type piece)))

(defn legal-destination-squares* [game from]
  (when-let [piece (board/get-piece (:board game) from)]
    (legal-destination-squares game from piece)))

(defmethod legal-destination-squares :default
  [game from piece]
  board/squares)

(defmethod legal-destination-squares :king
  [game from piece]
  (into #{}
        (keep #(board/offset from {% 1}) (:all board/directions))))

(defmethod legal-destination-squares :knight
  [game from piece]
  (into #{}
        (keep (partial board/offset from) 
              [{:north 2 :west 1}
               {:north 2 :east 1}
               {:east 2 :north 1} 
               {:east 2 :south 1}
               {:south 2 :east 1}
               {:south 2 :west 1}
               {:west 2 :south 1}
               {:west 2 :north 1}])))

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
                  (legal-destination-squares*
                    game 
                    (:from move))
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
