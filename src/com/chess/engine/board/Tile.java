package com.chess.engine.board;

import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile {

    /*
    Abstraction Function:
    This class represents a tile on the chess board using an integer. It is further subclassed to
    specify whether the tile is occupied or empty.
        - tileCoordinate: the integer that corresponds to the location of the tile on the chess
                          board.
     */

    protected final int tileCoordinate;

    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    /**
     * Creates a mapping of the standard 64 tile chess board, filled with empty tiles.
     *
     * @return a map of an empty chess board, with an EmptyTile at each integer coordinate
     */
    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        }
        return ImmutableMap.copyOf(emptyTileMap);
    }

    /**
     * Factory method that creates a new OccupiedTile or EmptyTile, depending on the input piece.
     *
     * @param tileCoordinate the coordinate to which the tile corresponds
     * @param piece the piece on the tile (null if piece is empty)
     * @return the subclassing of the tile that corresponds to the piece and the integer coordinate
     */
    public static Tile createTile(final int tileCoordinate, final Piece piece) {
        return piece != null ? new OccupiedTile(tileCoordinate, piece) :
            EMPTY_TILES_CACHE.get(tileCoordinate);
    }

    /**
     * Constructor for the Tile superclass.
     *
     * @param tileCoordinate the coordinate to which the tile corresponds.
     */
    private Tile(final int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public int getTileCoordinate() {
        return this.tileCoordinate;
    }

    public static final class EmptyTile extends Tile {

        /*
        Abstraction Function:
        The class represents an tile on the chess board that is not occupied by a piece from either
        side.
         */

        /**
         * Constructor for an EmptyTile that simply calls the superclass constructor.
         *
         * @param tileCoordinate the coordinate to which the tile corresponds
         */
        private EmptyTile(final int tileCoordinate) {
            super(tileCoordinate);
        }

        @Override
        public String toString() {
            return "-";
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }
    }

    public static final class OccupiedTile extends Tile {

        /*
        Abstraction Function:
        The class represents an tile on the chess board that is occupied by some piece from either
        side.
            - pieceOnTile: the piece that occupies the tile.
         */

        private final Piece pieceOnTile;

        /**
         * Constructor for an OccupiedTile that sets the piece on the tile.
         *
         * @param tileCoordinate the coordinate to which the tile corresponds
         * @param pieceOnTile the piece that occupies the tile
         */
        private OccupiedTile(int tileCoordinate, final Piece pieceOnTile) {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public String toString() {
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() :
                getPiece().toString();
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnTile;
        }
    }
}
