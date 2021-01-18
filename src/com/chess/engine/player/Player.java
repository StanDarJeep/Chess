package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {

    /*
    Abstraction Function:
    The Player class represents a player that is playing the chess game. The Player is responsible
    for the creation of MoveTransitions using the makeMove method, as well as determining possible
    castle moves. The Player may be subject to checks, checkmates, and stalemates depending on the
    configuration of the board, as well as the position of their King.
        - board: the Board on which the Players play.
        - playerKing: the King that belongs to the Player.
        - legalMoves: the collection of all legal Moves that the Player can play.
        - isInCheck: whether or not the Player is currently in check.

     Representation Invariant:
        - playerKing exists and is not null after calling establishKing.
     */

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    /**
     * Constructor for the Player superclass. This constructor concatenates the set of all legal
     * Moves of the pieces that belong to the Player, with the list of possible castles that can be
     * made.
     *
     * @param board the board on which the players play
     * @param legalMoves the set of legal moves for the player
     * @param opponentMoves the set of legal moves for opponent
     */
    protected Player(final Board board, final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves,
            calculateKingCastles(legalMoves, opponentMoves)));
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(),
            opponentMoves).isEmpty();
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    /**
     * This method returns the every Move that results in an attack on a specific Tile on the Board.
     * Used as a helper method in order to calculate legal King Moves and possible castles.
     *
     * @param piecePosition the tile coordinate
     * @param moves the possible legal Moves that the opponent can make
     * @return a collection of moves that represent the current attacks on the Tile
     */
    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move>
        moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves) {
            if (piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    /**
     * Establishes the King amongst the set of active Pieces. Used whenever a new Board is created
     * and new Players are assigned.
     *
     * @return the King Piece
     */
    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here! Not a valid board");
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && hasNoEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && hasNoEscapeMoves();
    }

    /**
     * A helper method that determines whether or not any possible Move is legal. Used in
     * determining checkmate and stalemate game conditions
     *
     * @return true if no escape Moves are possible and false otherwise
     */
    protected boolean hasNoEscapeMoves() {
        for (final Move move : this.legalMoves) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return false;
            }
        }
        return true;
    }

    public boolean isCastled() {
        return false;
    }

    /**
     * This method creates a new MoveTransition that represents the Move attempting to be made, as
     * well as the status of that Move (whether it is legal or not) to be used in calculating
     * whether or not a selected Move is possible.
     *
     * @param move the Move that is attempting to be made
     * @return a MoveTransition along with its MoveStatus
     */
    public MoveTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(
            transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
            transitionBoard.currentPlayer().getLegalMoves());
        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();

    public abstract Alliance getAlliance();

    public abstract Player getOpponent();

    /**
     * This method delegates to the subclasses the task of calculating possible legal castles
     *
     * @param playerLegals the legal Moves of the Player
     * @param opponentsLegals the legal Moves of the opponent
     * @return the collection of possible castles
     */
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                             Collection<Move> opponentsLegals);
}
