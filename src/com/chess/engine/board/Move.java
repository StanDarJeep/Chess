package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import static com.chess.engine.board.Board.*;

public abstract class Move {

    /*
    Abstraction Function:
    The class represents a move being made on the chess board, by any player. The execution of the
    move will always create a new Board.
        - board: the Board on which the move is made.
        - movedPiece: the Piece that is being moved.
        - destinationCoordinate: the tile coordinate on which the movedPiece will end up.
        - isFirstMove: whether or not this is the Piece's first move. Used for calculating pawn
                       jumps and castles.

        - NULL_MOVE: the singleton that is created when the factory class for Move creates an
                     illegal move.

     Representation Invariants:
        - board.currentPlayer().contains(movedPiece);
        - 0 <= destinationCoordinate < 64, otherwise the move is a NULL_MOVE;
     */

    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE = new NullMove();

    /**
     * Default constructor for a move given the board, the moved piece, and its destination
     * coordinate.
     *
     * @param board the board on which the move is played
     * @param movedPiece the piece that is being moved
     * @param destinationCoordinate the tile coordinate where the moving piece will end up
     */
    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    /**
     * Special constructor that creates an empty move. Used for instantiating the null move.
     *
     * @param board some board
     * @param destinationCoordinate some tile coordinate
     */
    private Move(final Board board, final int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Move)) {
            return false;
        }
        final Move otherMove = (Move) other;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
            getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
            getMovedPiece().equals(otherMove.getMovedPiece());
    }

    public Board getBoard() {
        return this.board;
    }

    public int getCurrentCoordinate() {
        return this.getMovedPiece().getPiecePosition();
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Piece getMovedPiece() {
        return this.movedPiece;
    }

    public boolean isAttack() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    /**
     * Creates the board that corresponds to board state after the move is played.
     *
     * @return the new board
     */
    public Board execute() {
        final Builder builder = new Builder();
        for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
            if (!this.movedPiece.equals(piece)) {
                builder.setPiece(piece);
            }
        }
        for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public static class AttackMove extends Move {

        /*
        Abstraction Function:
        This class represents a move wherein the moved piece is attacking an opponent piece.
            - attackedPiece the opponent piece that is being attacked.
         */

        final Piece attackedPiece;

        /**
         * Constructor for an AttackMove.
         *
         * @param board the board on which the move occurs
         * @param movedPiece the piece that is undergoing the move
         * @param destinationCoordinate the tile coordinate where the moving piece will end up
         * @param attackedPiece the opponent piece that is being attacked
         */
        public AttackMove(final Board board, final Piece movedPiece,
                          final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() { return this.attackedPiece.hashCode() + super.hashCode(); }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AttackMove)) {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) &&
                getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
    }

    public static final class MajorMove extends Move {

        /*
        Abstraction Function:
        This class represents any major piece undergoing a Move.
         */

        /**
         * Constructor for a MajorMove.
         *
         * @param board the board on which the move occurs
         * @param movedPiece the piece that is undergoing the move
         * @param destinationCoordinate the tile coordinate where the moving piece will end up
         */
        public MajorMove(final Board board, final Piece movedPiece,
                         final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other ||  other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() +
                BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class MajorAttackMove extends AttackMove {

        /*
        Abstraction Function:
        This class represents any major piece undergoing an AttackMove.
         */

        /**
         * Constructor for a MajorAttackMove.
         *
         * @param board the board on which the move occurs
         * @param pieceMoved the piece that is undergoing the move
         * @param destinationCoordinate the tile coordinate where the moving piece will end up
         * @param pieceAttacked the opponent piece that is being attacked
         */
        public MajorAttackMove(final Board board, final Piece pieceMoved,
                               final int destinationCoordinate, final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType() +
                BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnMove extends Move {

        /*
        Abstraction Function:
        This class represents a pawn undergoing a Move.
         */

        /**
         * Constructor for a normal forward PawnMove.
         *
         * @param board the board on which the move occurs
         * @param movedPiece the pawn that is undergoing the move
         * @param destinationCoordinate the tile coordinate where the pawn will end up
         */
        public PawnMove(final Board board, final Piece movedPiece,
                        final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnJump extends Move {

        /*
        Abstraction Function:
        This class represents a pawn undergoing a jump from the second rank to the fourth rank.
         */

        /**
         * Constructor for a two-tile PawnJump.
         *
         * @param board the board on which the move occurs
         * @param movedPiece the pawn that is undergoing the jump
         * @param destinationCoordinate the tile coordinate where the pawn will end up
         */
        public PawnJump(final Board board, final Piece movedPiece,
                        final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!(this.movedPiece.equals(piece))) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnPromotion extends Move {

        /*
        Abstraction Function:
        This class represents a pawn undergoing a promotion at the eighth rank. Wraps a Move.
            - decoratedMove: the move that results in the pawn reaching the eighth rank.
            - promotedPawn: the pawn that is to be promoted.
         */

        final Move decoratedMove;
        final Pawn promotedPawn;

        /**
         * Constructor for a PawnPromotion at the eighth rank.
         *
         * @param decoratedMove the move that results in the pawn reaching the eighth rank
         */
        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(),
                decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }

        @Override
        public int hashCode() {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnPromotion && (super.equals(other));
        }

        @Override
        public Board execute() {
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Builder builder = new Builder();
            for (final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()) {
                if (!this.promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().
                getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return this.decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString() {
            return "";
        }
    }

    public static class PawnAttackMove extends AttackMove {

        /*
        Abstraction Function:
        This class represents a pawn undergoing an AttackMove.
         */

        /**
         * Constructor for a diagonal PawnAttackMove.
         *
         * @param board the board on which the move occurs
         * @param movedPiece the pawn that is undergoing the move
         * @param destinationCoordinate the tile coordinate on which the pawn ends up
         * @param attackedPiece the opponent piece that is being attacked
         */
        public PawnAttackMove(final Board board, final Piece movedPiece,
                              final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).charAt(0)
                + "x" + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove {

        /*
        Abstraction Function:
        This class represents a pawn undergoing an en passant AttackMove, calculated using the
        special enPassantPawn member field of the board.
         */

        /**
         * Constructor for the special chess move, the PawnEnPassantAttackMove.
         *
         * @param board the board on which the move occurs
         * @param movedPiece the pawn that is undergoing the move
         * @param destinationCoordinate the tile coordinate where the pawn ends up
         * @param attackedPiece the opponent piece that is being attacked
         */
        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece,
                                       final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()) {
                if (!piece.equals(this.getAttackedPiece())) {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    static abstract class CastleMove extends Move {

        /*
        This class represents a castling Move, and is further subclassed in order to provide the
        special PGN string for both the KingSideCastle and the QueenSideCastle.
            - castleRook: the rook with which the castle is performed.
            - castleRookStart: the tile coordinate in which the rook was originally.
            - castleRookDestination: the tile coordinate where the rook will end up.
         */

        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        /**
         * Constructor for the special move CastleMove.
         *
         * @param board the board on which the move occurs
         * @param movedPiece the king that is undergoing the move
         * @param destinationCoordinate the tile coordinate where the king will end up
         * @param castleRook the rook that is undergoing the rook
         * @param castleRookStart the original tile coordinate of the rook
         * @param castleRookDestination the tile coordinate where the rook will end up
         */
        public CastleMove(final Board board, final Piece movedPiece,
                          final int destinationCoordinate, final Rook castleRook,
                          final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRook.getPieceAlliance(),
                this.castleRookDestination));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CastleMove)) {
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) &&
                this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }

    public static final class KingSideCastleMove extends CastleMove {

        /*
        Abstraction Function:
        This class represents a king side castle CastleMove.
         */

        public KingSideCastleMove(final Board board, final Piece movedPiece,
                                  final int destinationCoordinate, final Rook castleRook,
                                  final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart,
                castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
        }

        @Override
        public String toString() {
            return "O-O";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove {

        /*
        Abstraction Function:
        This class represents a queen side castle CastleMove
         */

        public QueenSideCastleMove(final Board board, final Piece movedPiece,
                                   final int destinationCoordinate, final Rook castleRook,
                                   final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart,
                castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }

        @Override
        public String toString() {
            return "O-O-O";
        }
    }

    public static final class NullMove extends Move {

        /*
        Abstraction Function:
        This class is a singleton that is to be created when the MoveFactory class creates some
        illegal move.
         */

        public NullMove() {
            super(null, 65);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("cannot execute the null move!");
        }

        @Override
        public int getCurrentCoordinate() {
            return -1;
        }
    }

    public static class MoveFactory {

        /*
        Abstraction Function:
        This is a factory class that will create a Move without needing to specify its specific
        subclassing.
         */

        private MoveFactory() {
            throw new RuntimeException("Not instantiable!");
        }

        /**
         * Creates a move given the board, and the start and end points of the move being played.
         * This method checks for whether or not the move is legal and will return the NULL_MOVE if
         * the specified move is illegal.
         *
         * @param board the board on which the move occurs
         * @param currentCoordinate the starting tile coordinate of the piece being moved
         * @param destinationCoordinate the end tile coordinate of the piece being moved
         * @return a subclassing of Move that corresponds to the specified start and end points
         */
        public static Move createMove(final Board board, final int currentCoordinate,
                                      final int destinationCoordinate) {
            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate &&
                    move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}
