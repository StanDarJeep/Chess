package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Knight extends Piece {

    /*
    Abstraction Function:
    This class represents the Knight piece in chess. The Knight moves in L shape patterns around the
    board, bypassing any pieces that may be in its way.
        - CANDIDATE_MOVE_COORDINATES: the 8 coordinates relative to the Knight's current position
                                      where it may make moves, following the one-dimensional
                                      organization of the board tiles.
     */

    private final static int[] CANDIDATE_MOVE_COORDINATES = { -17, -15, -10, -6, 6, 10, 15, 17 };

    /**
     * Constructor for a Knight that simply calls the superclass constructor.
     *
     * @param pieceAlliance the side on which the Knight is
     * @param piecePosition the tile coordinate where the Knight currently is
     */
    public Knight(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, true);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                    isSecondColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                    isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                    isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                    continue;
                }
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new MajorMove(board, this,
                        candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.pieceAlliance;
                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new MajorAttackMove(board, this,
                            candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Knight movePiece(final Move move) {
        return new Knight(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString() { return PieceType.KNIGHT.toString(); }

    /**
     * The algorithm in calculateLegalMoves does not hold true if the Knight is located on the first
     * column. This method calculates whether or not to exclude a move possibility from the possible
     * moves.
     *
     * @param currentPosition the current position of the Knight
     * @param candidateOffset the offset of the current position that is being considered for a
     *                        legal move
     * @return true if the Knight is on the first column and the offset should be invalid, and false
     * otherwise
     */
    private static boolean isFirstColumnExclusion(final int currentPosition,
                                                  final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -17 ||
            candidateOffset == -10 || candidateOffset == 6 || candidateOffset == 15);
    }

    /**
     * The algorithm in calculateLegalMoves does not hold true if the Knight is located on the
     * second column. This method calculates whether or not to exclude a move possibility from the
     * possible moves.
     *
     * @param currentPosition the current position of the Knight
     * @param candidateOffset the offset of the current position that is being considered for a
     *                        legal move
     * @return true if the Knight is on the second column and the offset should be invalid, and
     * false otherwise
     */
    private static boolean isSecondColumnExclusion(final int currentPosition,
                                                   final int candidateOffset) {
        return BoardUtils.SECOND_COLUMN[currentPosition] && (candidateOffset == -10 ||
            candidateOffset == 6);
    }

    /**
     * The algorithm in calculateLegalMoves does not hold true if the Knight is located on the
     * seventh column. This method calculates whether or not to exclude a move possibility from the
     * possible moves.
     *
     * @param currentPosition the current position of the Knight
     * @param candidateOffset the offset of the current position that is being considered for a
     *                        legal move
     * @return true if the Knight is on the seventh column and the offset should be invalid, and
     * false otherwise
     */
    private static boolean isSeventhColumnExclusion(final int currentPosition,
                                                    final int candidateOffset) {
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && (candidateOffset == -6 ||
            candidateOffset == 10);
    }

    /**
     * The algorithm in calculateLegalMoves does not hold true if the Knight is located on the
     * eighth column. This method calculates whether or not to exclude a move possibility from the
     * possible moves.
     *
     * @param currentPosition the current position of the Knight
     * @param candidateOffset the offset of the current position that is being considered for a
     *                        legal move
     * @return true if the Knight is on the eighth column and the offset should be invalid, and
     * false otherwise
     */
    private static boolean isEighthColumnExclusion(final int currentPosition,
                                                   final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -15 ||
            candidateOffset == -6 || candidateOffset == 10 || candidateOffset == 17);
    }
}
