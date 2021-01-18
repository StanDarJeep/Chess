package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public interface MoveStrategy {

    /*
    This interface will be used for any artificial intelligences that will be implemented, such as
    the MiniMax class.
     */

    /**
     * Given a board state, the MoveStrategy will determine the best possible next Move that the
     * Player should make.
     *
     * @param board the current board state
     * @return the best Move according to its algorithm
     */
    Move execute(Board board);
}
