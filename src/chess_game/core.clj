(ns chess-game.core
  (:require [chess-board.core :as board]))

(defmulti legal-destination-squares (fn [game from piece] (:piece-type piece)))

(defn legal-destination-squares* [game from]
  (when-let [piece (board/get-piece (:board game) from)]
    (legal-destination-squares game from piece)))

(defmethod legal-destination-squares :default
  [game from piece]
  board/squares)

(defn offset-destination-squares
  [game from piece offsets]
  (into #{}
        (filter #(not= (:piece-color piece) (:piece-color (board/get-piece (:board game) %))) 
                (keep #(board/offset from %) offsets))))

(defmethod legal-destination-squares :king
  [game from piece]
  (offset-destination-squares game from piece (for [d (:all board/directions)] {d 1})))

(defmethod legal-destination-squares :knight
  [game from piece]
  (offset-destination-squares game from piece
    [{:north 2 :west 1}
     {:north 2 :east 1}
     {:east 2 :north 1} 
     {:east 2 :south 1}
     {:south 2 :east 1}
     {:south 2 :west 1}
     {:west 2 :south 1}
     {:west 2 :north 1}]))

(defn sliding-destination-squares
  "Sequence of moves sliding in given direction
  until the path is obstructed by the edge, 
  a friendly piece, or after an opposing piece 
  is captured."
  [game-board piece-color from direction]
  (reduce
    (fn [acc square]
      (let [piece (board/get-piece game-board square)]
        (cond
          (not piece) (conj acc square)
          (not= piece-color (:piece-color piece)) (reduced (conj acc square))
          (= piece-color (:piece-color piece)) (reduced acc)
          :panic (ex-info "Error while generating possible square to slide to" 
                          {:starting-square from :direction direction :accumulated-candidates acc :failing-square square :piece piece}))))
    #{}
    (board/slide from direction)))

(defn legal-destination-squares-for-slide
  [game from piece directions]
  (set (mapcat #(sliding-destination-squares (:board game) (:piece-color piece) from %) 
               directions)))

(defmethod legal-destination-squares :rook
  [game from piece]
  (legal-destination-squares-for-slide game from piece (:lateral board/directions)))

(defmethod legal-destination-squares :bishop
  [game from piece]
  (legal-destination-squares-for-slide game from piece (:diagonal board/directions)))

(defmethod legal-destination-squares :queen
  [game from piece]
  (legal-destination-squares-for-slide game from piece (:all board/directions)))

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
