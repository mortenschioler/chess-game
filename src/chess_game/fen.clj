(ns chess-game.fen
  (:require
    [clojure.string :as str]
    [clojure.edn :as edn]
    [chess-board.core :as board]
    [chess-board.squares]
    [chess-board.fen :as board-fen]))

(def starting-position "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")

(defn read-active-color
  [color-str]
  (case color-str
    "w" :white
    "b" :black))

(defn read-castling-availability
  [castling-str]
  (reduce
    #(conj %
           (case %2
             \K {:color :white :side :kingside}
             \Q {:color :white :side :queenside}
             \k {:color :black :side :kingside}
             \q {:color :black :side :queenside}))
    #{}
    castling-str))

(defn read-square
  [square-str]
  (when-not (str/blank? square-str)
    (eval (symbol (str "chess-board.squares/" square-str)))))

(defn read-en-passant-square
  [square-str]
  (when-not (= square-str "-")
    (read-square square-str)))

(defn read-game
  "Read the fields in a Forsyth-Edwards Notation not associated with piece placement
  and return the corresponding game information, without the piece placement."
  [fen]
  (let [[piece-positions active-color castling-availability en-passant-square halfmove-clock fullmove-counter] (str/split fen #" ")]
    {:board (board-fen/read board/empty-board piece-positions)
     :side-to-move (read-active-color active-color)
     :castling-availability (read-castling-availability castling-availability)
     :en-passant-square (read-en-passant-square en-passant-square)
     :halfmove-clock (edn/read-string halfmove-clock)
     :fullmove-counter (edn/read-string fullmove-counter)}))
