package com.chess.engine.player;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public class MoveTransition {

    /*
    Abstraction Function:
    This class represents a future Move and the board state in which the Move will result in. Only
    when the MoveStatus == DONE will the Move go through in a game.
        - transitionBoard: the Board to be created after the Move goes through.
        - move: the Move being considered.
        - moveStatus: the MoveStatus of the Move that will determine its validity.
     */

    private final Board transitionBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    /**
     * Constructor for a MoveTransition
     *
     * @param transitionBoard the Board to be created after the Move goes through
     * @param move the Move being considered
     * @param moveStatus the MoveStatus of the Move that will determine its validity
     */
    public MoveTransition(final Board transitionBoard, final Move move,
                          final MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMoveStatus() {
        return this.moveStatus;
    }

    public Board getBoard() {
        return this.transitionBoard;
    }
}
