package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.Bishop;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Knight;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Queen;
import com.chess.engine.pieces.Rook;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

    /*
    Abstraction Function:
    This class represents the chess game board, along with the pieces on the board and the players
    that are playing the game. The Board is organized one-dimensionally rather than
    two-dimensionally.
        - gameBoard: the linear list of 64 tiles on the chess board.
        - whitePieces: the collection of the white player's pieces.
        - blackPieces: the collection of the black player's pieces.
        - enPassantPawn: the piece used to calculate the en passant move. Created when a pawn jump
                         occurs.
        - whitePlayer: the player in control of the white pieces.
        - blackPlayer: the player in control of the black pieces
        - currentPlayer: the player whose turn it is to make a move.

     Representation Invariants:
        - gameBoard.size() == 64;
        - currentPlayer.equals(whitePlayer) || currentPlayer.equals(blackPlayer);
        - whitePieces.contains(a king);
        - blackPieces.contains(a king);
     */

    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final Pawn enPassantPawn;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    /**
     * Constructor for a board that uses a builder class as its parameter.
     *
     * @param builder the builder used to create the board
     */
    private Board(final Builder builder) {

        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves,
            blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves,
            blackStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));
            if ((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public Player blackPlayer() {
        return this.blackPlayer;
    }

    public Player whitePlayer() {
        return this.whitePlayer;
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    /**
     * Calculates the collection of legal moves for a collection of pieces on the board.
     *
     * @param pieces the collection of pieces on the board
     * @return the collection of legal moves for those pieces
     */
    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final Piece piece : pieces) {
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    /**
     * Calculates the collection of active pieces on the board, for a specific side.
     *
     * @param gameBoard the board
     * @param alliance the side for which the calculation is made
     * @return the collection of pieces
     */
    private static Collection<Piece> calculateActivePieces(final List<Tile> gameBoard,
                                                           final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();
        for (final Tile tile : gameBoard) {
            if (tile.isTileOccupied()) {
                final Piece piece = tile.getPiece();
                if (piece.getPieceAlliance() == alliance) {
                    activePieces.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }

    public Tile getTile(final int tileCoordinate) {
        return gameBoard.get(tileCoordinate);
    }

    /**
     * Creates a list of 64 tiles, the pieces of which will be calculated from the builder class.
     *
     * @param builder the builder used to obtain the tile data
     * @return a list of tiles that represent the game board
     */
    private static List<Tile> createGameBoard(final Builder builder) {
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(tiles);
    }

    /**
     * Creates the standard opening positions on a chess board.
     *
     * @return the board
     */
    public static Board createStandardBoard() {
        final Builder builder = new Builder();

        //BLACK
        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Knight(Alliance.BLACK, 1));
        builder.setPiece(new Bishop(Alliance.BLACK, 2));
        builder.setPiece(new Queen(Alliance.BLACK, 3));
        builder.setPiece(new King(Alliance.BLACK, 4, true,
            true));
        builder.setPiece(new Bishop(Alliance.BLACK, 5));
        builder.setPiece(new Knight(Alliance.BLACK, 6));
        builder.setPiece(new Rook(Alliance.BLACK, 7));
        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 9));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 11));
        builder.setPiece(new Pawn(Alliance.BLACK, 12));
        builder.setPiece(new Pawn(Alliance.BLACK, 13));
        builder.setPiece(new Pawn(Alliance.BLACK, 14));
        builder.setPiece(new Pawn(Alliance.BLACK, 15));

        //WHITE
        builder.setPiece(new Rook(Alliance.WHITE, 63));
        builder.setPiece(new Knight(Alliance.WHITE, 62));
        builder.setPiece(new Bishop(Alliance.WHITE, 61));
        builder.setPiece(new King(Alliance.WHITE, 60, true,
            true));
        builder.setPiece(new Queen(Alliance.WHITE, 59));
        builder.setPiece(new Bishop(Alliance.WHITE, 58));
        builder.setPiece(new Knight(Alliance.WHITE, 57));
        builder.setPiece(new Rook(Alliance.WHITE, 56));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 52));
        builder.setPiece(new Pawn(Alliance.WHITE, 51));
        builder.setPiece(new Pawn(Alliance.WHITE, 50));
        builder.setPiece(new Pawn(Alliance.WHITE, 49));
        builder.setPiece(new Pawn(Alliance.WHITE, 48));

        //white to move
        builder.setMoveMaker(Alliance.WHITE);
        return builder.build();
    }

    /**
     * Calculates the union of the set of legal moves for white, and the set of legal moves for
     * black.
     *
     * @return the iterable collection of all possible moves
     */
    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(),
            this.blackPlayer.getLegalMoves()));
    }

    public static class Builder {

        /*
        A builder class that creates a Board from a specified construction.
            - boardConfig: the collection of pieces on the board, mapped by their position on the
                           board.
            - nextMoveMaker: the side that is to make the next move.
            - enPassantPawn: the piece used to calculate the en passant move. Is to be created when
                             a pawn jump occurs.
         */

        Map<Integer, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;

        /**
         * Constructor for an empty builder.
         */
        public Builder() {
            this.boardConfig = new HashMap<>();
        }

        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public void setMoveMaker(final Alliance nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
        }

        /**
         * Calls the constructor for the board class.
         *
         * @return the board that is created
         */
        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }
}
