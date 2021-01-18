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

public class Queen extends Piece {

    /*
    Abstraction Function:
    This class represents the Queen piece in chess. The Queen moves diagonally and horizontally
    across the board.
        - CANDIDATE_MOVE_VECTOR_COORDINATES: the vectors along which the Queen may make moves,
                                             following the one-dimensional organization of the board
                                             tiles.
     */

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };

    /**
     * Constructor for a Queen that simply calls the superclass constructor.
     *
     * @param pieceAlliance the side on which the Queen is
     * @param piecePosition the tile coordinate where the Queen currently is
     */
    public Queen(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.QUEEN, piecePosition, pieceAlliance, true);
    }

    public Queen(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
        super(PieceType.QUEEN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int candidateCoordinateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;
            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(candidateDestinationCoordinate,
                    candidateCoordinateOffset) ||
                    isEighthColumnExclusion(candidateDestinationCoordinate,
                        candidateCoordinateOffset)) {
                    break;
                }
                candidateDestinationCoordinate += candidateCoordinateOffset;
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    final Tile candidateDestinationTile =
                        board.getTile(candidateDestinationCoordinate);
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
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Queen movePiece(final Move move) {
        return new Queen(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString() { return PieceType.QUEEN.toString(); }

    /**
     * The algorithm in calculateLegalMoves does not hold true if the Queen is located on the first
     * column. This method calculates whether or not to exclude a move possibility from the possible
     * moves.
     *
     * @param currentPosition the current position of the Queen
     * @param candidateOffset the offset of the current position that is being considered for a
     *                        legal move
     * @return true if the Queen is on the first column and the offset should be invalid, and false
     * otherwise
     */
    private static boolean isFirstColumnExclusion(final int currentPosition,
                                                  final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1 ||
            candidateOffset == -9 || candidateOffset == 7);
    }

    /**
     * The algorithm in calculateLegalMoves does not hold true if the Queen is located on the eighth
     * column. This method calculates whether or not to exclude a move possibility from the possible
     * moves.
     *
     * @param currentPosition the current position of the Queen
     * @param candidateOffset the offset of the current position that is being considered for a
     *                        legal move
     * @return true if the Queen is on the eighth column and the offset should be invalid, and false
     * otherwise
     */
    private static boolean isEighthColumnExclusion(final int currentPosition,
                                                   final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 ||
            candidateOffset == 1 || candidateOffset == 9);
    }
}
