package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Pawn extends Piece {

    /*
    Abstraction Function:
    This class represents the Pawn piece in chess.
    - The Pawn moves forward one tile onto the board with its normal move, and may only attack
      diagonally forward.
    - The first move of a Pawn, while the Pawn is still on its starting rank, may be a PawnJump,
      moving forward two tiles.
    - The Pawn undergoes a PawnPromotion when it reaches the last rank, becoming a Queen (other
      pieces promotion is not supported).
    - The Pawn may capture an opposing Pawn that is adjacent to itself, so long as that opposing
      Pawn made a PawnJump last turn, in an PawnEnPassantAttackMove.
        - CANDIDATE_MOVE_COORDINATES: the coordinates relative to the Pawn's current position where
                                      it may make moves, following the one-dimensional organization
                                      of the board tiles.
     */

    private final static int[] CANDIDATE_MOVE_COORDINATES = { 8, 16, 7, 9 };

    /**
     * Constructor for a Pawn that simply calls the superclass constructor.
     *
     * @param pieceAlliance the side on which the Pawn is
     * @param piecePosition the tile coordinate where the Pawn currently is
     */
    public Pawn(final Alliance pieceAlliance, final int piecePosition) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            final int candidateDestinationCoordinate = this.piecePosition +
                (this.pieceAlliance.getDirection() * currentCandidateOffset);
            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }
            if (currentCandidateOffset == 8 &&
                !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    //Pawn Promotion
                    legalMoves.add(new PawnPromotion(new PawnMove(board, this,
                        candidateDestinationCoordinate)));
                } else {
                    //Normal Move
                    legalMoves.add(new PawnMove(board, this,
                        candidateDestinationCoordinate));
                }
            } else if (currentCandidateOffset == 16 && this.isFirstMove() &&
                ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack()) ||
                (BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite()))) {
                //Pawn Jump
                final int behindCandidateDestinationCoordinate = this.piecePosition +
                    (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                    !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new PawnJump(board, this,
                        candidateDestinationCoordinate));
                }
            } else if (currentCandidateOffset == 7 &&
                !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
                (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    //Pawn Attack
                    final Piece pieceOnCandidate =
                        board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.pieceAlliance) {
                        if (this.pieceAlliance.isPawnPromotionSquare(
                            candidateDestinationCoordinate)) {
                            //Promotion Attack
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board,
                                this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            //Normal Attack
                            legalMoves.add(new PawnAttackMove(board, this,
                                candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } else if (board.getEnPassantPawn() != null) {
                    //En Passant
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition +
                        this.pieceAlliance.getOppositeDirection())) {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            legalMoves.add(new PawnEnPassantAttackMove(board, this,
                                candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            } else if (currentCandidateOffset == 9 &&
                !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
                (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    //Pawn Attack
                    final Piece pieceOnCandidate =
                        board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.pieceAlliance) {
                        if (this.pieceAlliance.isPawnPromotionSquare(
                            candidateDestinationCoordinate)) {
                            //Promotion Attack
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board,
                                this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            //Normal Attack
                            legalMoves.add(new PawnAttackMove(board, this,
                                candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } else if (board.getEnPassantPawn() != null) {
                    //En Passant
                    if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition -
                        this.pieceAlliance.getOppositeDirection())) {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            legalMoves.add(new PawnEnPassantAttackMove(board, this,
                                candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    /**
     * This method returns the piece to which the Pawn will be promoted. Currently only supports the
     * specific promotion to a Queen.
     *
     * @return the piece to which the Pawn promotes
     */
    public Piece getPromotionPiece() {
        return new Queen(this.pieceAlliance, this.piecePosition);
    }
}
