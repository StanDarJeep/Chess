package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {

    /*
    Abstraction Function:
    This class represents a piece in chess. All of the different types of pieces are subclassed from
    this class. Each piece calculates their own possible moves given the current board state.
        - pieceType: the Piece's type.
        - piecePosition: the tile coordinate on which the Piece is located.
        - pieceAlliance: the side to which the Piece belongs.
        - isFirstMove: whether or not the Piece has made a move yet, in the game.
        - cachedHashCode: the hashCode of the Piece, cached in order to improve performance, since
                          the execution of each Move requires a new Board to be built.
     */

    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private final int cachedHashCode;

    /**
     * Constructor for the Piece superclass.
     *
     * @param pieceType the type of Piece being created
     * @param piecePosition the tile coordinate on which the Piece is located
     * @param pieceAlliance the side to which the Piece belongs
     * @param isFirstMove whether or not the Piece has made a move yet, in the game
     */
    Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance,
          final boolean isFirstMove) {
        this.pieceType = pieceType;
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    /**
     * Computes the hashCode for this Piece
     *
     * @return the unique hashCode
     */
    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Piece)) {
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.getPiecePosition()
            && pieceType == otherPiece.getPieceType()
            && pieceAlliance == otherPiece.getPieceAlliance()
            && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    public int getPiecePosition() {
        return this.piecePosition;
    }

    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }

    /**
     * This abstract method will return all the possible legal moves for a specific Piece, given the
     * board state.
     *
     * @param board the board on which the Piece exists
     * @return a collection of all the possible legal moves
     */
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    /**
     * This abstract method will create a new Piece with updated data coming from the Move being
     * passed as a parameter.
     *
     * @param move the Move involving the Piece which will occur
     * @return the new Piece after the Move occurs
     */
    public abstract Piece movePiece(Move move);

    public enum PieceType {

        /*
        Abstraction Function:
        This enum is for keeping track of the different piece types, whether or not they are Rooks
        or Kings (for castling purposes), the piece value of the type to be used in the
        BoardEvaluator, and their string representation.
         */

        PAWN("P", 100) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N", 300)  {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B", 300)  {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R", 500)  {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q", 900)  {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K", 10000)  {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };

        private final String pieceName;
        private final int pieceValue;

        PieceType(final String pieceName, final int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public int getPieceValue() {
            return this.pieceValue;
        }

        public abstract boolean isKing();

        public abstract boolean isRook();
    }
}
