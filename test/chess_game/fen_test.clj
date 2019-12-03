(ns chess-game.fen-test
  (:require [clojure.test :refer :all]
            [chess-game.fen :as fen]))

(deftest test-read-square
  (is (= (fen/read-square "a8")
      0)))

(deftest test-read-game
  (let [res (fen/read-game "8/8/8/8/8/8/8/8 b Kkq e3 ")]
    (is (= (:side-to-move res) :black))
    (is (= (:castling-availability res) 
           #{{:color :white :side :kingside}
             {:color :black :side :kingside}
             {:color :black :side :queenside}}))
    (is (= (:en-passant-square res) 44)))
  (let [res (fen/read-game "8/8/8/8/8/8/8/8 w KQkq ")]
    (is (= (:side-to-move res) :white))
    (is (= (:castling-availability res) 
           #{{:color :white :side :kingside}
             {:color :white :side :queenside}
             {:color :black :side :kingside}
             {:color :black :side :queenside}}))
    (is (= (:en-passant-square res) nil))))

