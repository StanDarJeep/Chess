package com.chess.engine.player;

public enum MoveStatus {

    /*
    Abstraction Function:
    This enum represents the status of a particular MoveTransition.
        - DONE: the Move is legal and may be played.
        - ILLEGAL_MOVE or LEAVES_PLAYER_IN_CHECK: the Move is illegal and cannot be played.
     */

    DONE {
        @Override
        public boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE {
        @Override
        public boolean isDone() {
            return false;
        }
    },
    LEAVES_PLAYER_IN_CHECK {
        @Override
        public boolean isDone() {
            return false;
        }
    };

    public abstract boolean isDone();
}
