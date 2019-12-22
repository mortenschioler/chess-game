(ns chess-game.core-test
  (:require [clojure.test :refer :all]
            [chess-game.core :refer :all]
            [chess-game.fen :as fen]
            [chess-board.squares :refer :all]))

(def new-game
  (fen/read-game fen/starting-position))

(deftest legal?-test
  (testing "legal-starting-position-moves"
    (is (legal? new-game {:player :white :from e2 :to e4}))
    (is (legal? new-game {:player :white :from g1 :to f3})))
  (testing "illegal-starting-position-moves"
    (is (not (legal? new-game {:player :black :from e7 :to e5})))
    (is (not (legal? new-game {:player :white :from e7 :to e5})))
    (is (not (legal? new-game {:player :white :from e3 :to e4})))
    (is (not (legal? new-game {:player :white :from e1 :to e1})))
    (is (not (legal? new-game {:player :white :from e1 :to e4})))))

(deftest legaltarget-squares-test
  (testing "king moves"
    (is (= (legal-destination-squares 
             (fen/read-game "7k/8/8/8/8/8/8/K7 w - - 0 1")
             a1
             {:piece-color :white :piece-type :king})
           #{b1 b2 a2})))
  (testing "knight moves"
    (is (= (legal-destination-squares 
             new-game
             g1
             {:piece-color :white :piece-type :knight})
           #{f3 h3}))))
