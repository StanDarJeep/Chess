package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;

public class MiniMax implements MoveStrategy {

    /*
    Abstraction Function:
    This class represents the MoveStrategy that is used by the artificial intelligence during a
    selected computer-played game. It uses a MiniMax algorithm, seeking to minimize the potential
    losses for a possible maximum loss scenario.
        - boardEvaluator: the evaluation algorithm that the artificial intelligence will use to
                          evaluate a specific board state.
        - searchDepth: the depth at which the algorithm will search for Moves.
     */

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;

    /**
     * Constructor for the MiniMax class. The larger the depth, the more resource and time consuming
     * that the algorithm becomes.
     *
     * @param searchDepth the depth at which the algorithm will search for moves.
     */
    public MiniMax(final int searchDepth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    @Override
    public Move execute(Board board) {
        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        System.out.println(board.currentPlayer() + " THINKING WITH DEPTH = " + this.searchDepth);
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                    min(moveTransition.getBoard(), this.searchDepth - 1) :
                    max(moveTransition.getBoard(), this.searchDepth - 1);
                if (board.currentPlayer().getAlliance().isWhite() && currentValue >=
                    highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() && currentValue <=
                    lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }

    /**
     * A corecursive function that will calculate the minimum gain for a certain ply. Intrinsically
     * calls the corecursive max function in order to calculate the maximum gain for the layer one
     * ply up.
     *
     * @param board the current board state
     * @param depth the depth at which this function was called
     * @return the minimum gain at this particular depth
     */
    public int min(final Board board, final int depth) {
        if (depth == 0 || isEndGameScenario(board)) {
            return this.boardEvaluator.evaluate(board, depth);
        }
        int lowestSeenValue = Integer.MAX_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = max(moveTransition.getBoard(), depth - 1);
                if (currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    /**
     * Helper method for the min and max functions. Determines whether or not the game has ended for
     * a given Board.
     *
     * @param board the Board to be assessed
     * @return true if the game has ended in checkmate or stalemate, and false otherwise
     */
    private static boolean isEndGameScenario(final Board board) {
        return board.currentPlayer().isInCheckMate() || board.currentPlayer().isInStaleMate();
    }

    /**
     * A corecursive function that will calculate the maximum gain for a certain ply. Intrinsically
     * calls the corecursive min function in order to calculate the minimum gain for the layer one
     * ply up.
     *
     * @param board the current board state
     * @param depth the depth at which this function was called
     * @return the maximum gain at this particular depth
     */
    public int max(final Board board, final int depth) {
        if (depth == 0 || isEndGameScenario(board)) {
            return this.boardEvaluator.evaluate(board, depth);
        }
        int highestSeenValue = Integer.MIN_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = min(moveTransition.getBoard(), depth - 1);
                if (currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }
}
