(ns chess-game.fen-test
  (:require [clojure.test :refer :all]
            [chess-game.fen :as fen]
            [chess-board.squares :refer :all]))

(deftest test-read-square
  (is (= (fen/read-square "a8")
      a8)))

(deftest test-read-game
  (let [res (fen/read-game "8/8/8/8/8/8/8/8 b Kkq e3 ")]
    (is (= (:side-to-move res) :black))
    (is (= (:castling-availability res) 
           #{{:color :white :side :kingside}
             {:color :black :side :kingside}
             {:color :black :side :queenside}}))
    (is (= (:en-passant-square res) e3)))
  (let [res (fen/read-game "8/8/8/8/8/8/8/8 w KQkq ")]
    (is (= (:side-to-move res) :white))
    (is (= (:castling-availability res) 
           #{{:color :white :side :kingside}
             {:color :white :side :queenside}
             {:color :black :side :kingside}
             {:color :black :side :queenside}}))
    (is (= (:en-passant-square res) nil)))
  (let [res (fen/read-game "8/8/8/8/8/8/8/8 w - - 0 1")]
    (is (= (:side-to-move res) :white))
    (is (= (:castling-availability res) 
           #{}))
    (is (= (:en-passant-square res) nil))
    (is (= (:halfmove-clock res) 0))
    (is (= (:fullmove-counter res) 1))))

