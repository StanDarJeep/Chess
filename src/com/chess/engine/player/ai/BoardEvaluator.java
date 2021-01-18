package com.chess.engine.player.ai;

import com.chess.engine.board.Board;

public interface BoardEvaluator {

    /*
    This interface will be used for any evaluation algorithms that will be implemented, such as the
    StandardBoardEvaluator.
     */

    /**
     * Assesses the board state at a certain depth.
     *
     * @param board the Board to be evaluated
     * @param depth the depth at which the evaluation occurs
     * @return a comparable integer that represents the advantage a Player has given the Board
     */
    int evaluate(Board board, int depth);
}
