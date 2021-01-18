package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator {

    /*
    Abstraction Function:
    This class represents an evaluator for a given Board state. It assesses the material value,
    mobility, checking, and castling bonuses that a Player may possess. This class will be used in
    artificially intelligent algorithms, such as MiniMax.
        - BONUS fields: these arbitrary values are multipliers for the potential advantage that a
                        certain criteria may create.
     */

    private static final int CHECK_BONUS = 50;
    private static final int CHECK_MATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;

    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) -
            scorePlayer(board, board.blackPlayer(), depth);
    }

    /**
     * Given a certain Player, quantify how strong their current game state is.
     *
     * @param board the Board being evaluated
     * @param player the Player whose advantage will be evaluated
     * @param depth the depth at which the evaluation is made
     * @return the sum of multiple different criteria that combine to form to strength of the
     * Player's game state
     */
    private int scorePlayer(final Board board, final Player player, final int depth) {
        return pieceValue(player) + mobility(player) + check(player) + checkmate(player, depth) +
            castled(player);
    }

    private static int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private static int checkmate(Player player, int depth) {
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * (depth);
    }

    private static int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int mobility(final Player player) {
        return player.getLegalMoves().size();
    }

    private static int pieceValue(final Player player) {
        int pieceValueScore = 0;
        for (final Piece piece : player.getActivePieces()) {
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    }
}
