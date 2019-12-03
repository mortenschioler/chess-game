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
