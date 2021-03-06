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

public class King extends Piece {

    /*
    Abstraction Function:
    This class represents the King piece in chess. The King moves diagonally and horizontally across
    the board, with a maximum range of one tile. Unlike the other pieces, the King may not move into
    a tile where the opponent pieces directly attack.
        - CANDIDATE_MOVE_COORDINATES: the 8 coordinates around the King's current position where it
                                      may make moves, following the one-dimensional organization of
                                      the board tiles.
     */

    private final static int[] CANDIDATE_MOVE_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };

    /**
     * Constructor for a King that simply calls the superclass constructor.
     *
     * @param pieceAlliance the side on which the King is
     * @param piecePosition the tile coordinate where the King currently is
     */
    public King(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.KING, piecePosition, pieceAlliance, true);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
                continue;
            }
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
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
    public King movePiece(final Move move) {
        return new King(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString() { return PieceType.KING.toString(); }

    /**
     * The algorithm in calculateLegalMoves does not hold true if the King is located on the first
     * column. This method calculates whether or not to exclude a move possibility from the possible
     * moves.
     *
     * @param currentPosition the current position of the King
     * @param candidateOffset the offset of the current position that is being considered for a
     *                        legal move
     * @return true if the King is on the first column and the offset should be invalid, and false
     * otherwise
     */
    private static boolean isFirstColumnExclusion(final int currentPosition,
                                                  final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 ||
            candidateOffset == -1 || candidateOffset == 7);
    }

    /**
     * The algorithm in calculateLegalMoves does not hold true if the King is located on the eighth
     * column. This method calculates whether or not to exclude a move possibility from the possible
     * moves.
     *
     * @param currentPosition the current position of the King
     * @param candidateOffset the offset of the current position that is being considered for a
     *                        legal move
     * @return true if the King is on the eighth column and the offset should be invalid, and false
     * otherwise
     */
    private static boolean isEighthColumnExclusion(final int currentPosition,
                                                   final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 ||
            candidateOffset == 1 || candidateOffset == 9);
    }
}
