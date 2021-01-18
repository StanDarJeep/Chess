package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {

    /*
    This class is the main Graphical User Interface of the chess engine. The class is a singleton,
    and contains the following UI elements:
        - a GameHistoryPanel on the right
        - a TakenPiecesPanel on the left
        - the BoardPanel to be displayed and interacted with in the middle
            - 64 individual Tiles that are displayed in the BoardPanel, each either empty or
              occupied with a Piece.
        - the File menu
            - The Exit menuItem that exits the process
            - The Load PGN File menuItem that is not yet functional
        - the Preferences menu
            - the Flip Board menuItem that rearranges the perspective of the Board on the screen
            - the checkBox to toggle the option of showing a green square where legal Moves are
              possible for a selected Piece
        - the Options menu
            - the GameSetup dialog box that appears to prompt the user to setup a new game, choosing
              human or AI participants

         Table also contains the following member fields:
            - sourceTile: the Tile on the game Board that is currently selected by the user.
            - destinationTile: the Tile on the game Board to which the user has selected a Move.
            - humanMovedPiece: the Piece which the user is currently selecting
            - boardDirection: the BoardDirection of the Board that the user has toggled.
     */

    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;

    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;

    private boolean highlightLegalMoves;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private static final String defaultImagesPath = "art/pieces/plain/";

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    private static final Table INSTANCE = new Table();

    /**
     * Constructor for the Table class.
     */
    private Table() {
        JFrame gameFrame = new JFrame("JChess");
        gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        gameFrame.setJMenuBar(tableMenuBar);
        gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(gameFrame, true);
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = false;
        gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);

        gameFrame.setVisible(true);
    }

    public static Table get() {
        return INSTANCE;
    }

    /**
     * This method updates the gameHistoryPanel, takenPiecesPanel, and boardPanel of the GUI.
     */
    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    private Board getGameBoard() {
        return this.chessBoard;
    }

    /**
     * Creates and populates the menuBar with the File, Preferences, and Options menus.
     *
     * @return the populated JMenuBar
     */
    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    /**
     * Creates and populates the File menu with the Load PGN File menuItem and the Exit menuItem.
     *
     * @return the populated File menu
     */
    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(e -> System.out.println("open up that pgn file!"));
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    /**
     * Creates and populates the Preferences menu with the Flip Board menuItem, a separator, and the
     * HighlightLegalMoves checkbox.
     *
     * @return the populated Preferences menu
     */
    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(e -> {
            boardDirection = boardDirection.opposite();
            boardPanel.drawBoard(chessBoard);
        });
        preferencesMenu.add(flipBoardMenuItem);

        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighterCheckbox =
            new JCheckBoxMenuItem("Highlight Legal Moves", false);
        legalMoveHighlighterCheckbox.addActionListener(
            e -> highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected());
        preferencesMenu.add(legalMoveHighlighterCheckbox);

        return preferencesMenu;
    }

    /**
     * Creates and populates the Options menu with the Setup Game menuItem.
     *
     * @return the populated Options menu
     */
    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(e -> {
            Table.get().getGameSetup().promptUser();
            Table.get().setupUpdate(Table.get().getGameSetup());
        });
        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }

    /**
     * This method notifies the GameSetup when the user has made a change to the dialog box.
     *
     * @param gameSetup the GameSetup to be notified
     */
    private void setupUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    private static class TableGameAIWatcher implements Observer {

        /*
        This observer class notifies the AI when it is their turn to play a Move. It also detects
        for checkmates and stalemates.
         */

        @Override
        public void update(final Observable o, final Object arg) {
            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                !Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }
            if (Table.get().getGameBoard().currentPlayer().isInCheckMate()) {
                System.out.println("game over, " + Table.get().getGameBoard().currentPlayer() +
                    " is in checkmate!");
            }
            if (Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                System.out.println("game over, " + Table.get().getGameBoard().currentPlayer() +
                    " is in stalemate!");
            }
        }
    }

    public void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    /**
     * This method notifies a PlayerType when a move has been played on the game Board. Used in the
     * AI implementation.
     *
     * @param playerType the PlayerType that is being notified
     */
    private void moveMadeUpdate(final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    private static class AIThinkTank extends SwingWorker<Move, String> {

        /*
        Abstraction Function:
        The AI Implementation of the MiniMax MoveStrategy, used when the Computer option is selected
        in the Setup Game dialog box. The AI is a SwingWorker, so it performs tasks in the
        background on a worker thread, as opposed to the current thread or the Event Dispatch
        thread.

        AIThinkTank calculates the best Move to make given a Board state, in the background, using
        the MiniMax MoveStrategy. When it completes, it will notify the main Table class and update
        the corresponding GUI elements.
         */

        private AIThinkTank() {
        }

        @Override
        protected Move doInBackground() {
            final MoveStrategy miniMax = new MiniMax(4);
            return miniMax.execute(Table.get().getGameBoard());
        }

        @Override
        public void done() {
            try {
                final Move bestMove = get();
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(
                    bestMove).getBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get()
                    .getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public enum BoardDirection {

        /*
        Abstraction Function:
        This enum represents the point of view from which the chess Board is viewed.
            - NORMAL: the point of view of the white Player.
            - FLIPPED: the point of view of the black Player.
         */

        NORMAL {

            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {

            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        /**
         * This method returns the proper ordering of the list of TilePanels, for a BoardDirection.
         *
         * @param boardTiles the list of TilePanels to be ordered
         * @return the ordered list of TilePanels, based on the BoardDirection
         */
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);

        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel {

        /*
        This class is the JPanel implementation of the centre panel, on which the Board and its
        Tiles are displayed. The BoardPanel consists of 64 TilePanels arranged in a List, whose
        order is determined by the BoardDirection specified by the user.
            - boardTiles: this List of TilePanels of the Board, ordered by the specified
                          BoardDirection.
         */

        final List<TilePanel> boardTiles;

        /**
         * Constructor for a default 8 x 8 chess board UI.
         */
        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        /**
         * This method updates the main game board, and redraws it based on the specified
         * BoardDirection.
         *
         * @param board the Board to be drawn
         */
        public void drawBoard(final Board board) {
            removeAll();
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog {

        /*
        Abstraction Function:
        This class wraps an instance of a list of Moves, used in the Table class for the GUI.
            - moves: the list of Moves that were played in a chess game.
         */

        private final List<Move> moves;

        /**
         * Constructor for an empty MoveLog.
         */
        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }
    }

    enum PlayerType {

        /*
        This enum represents the possible types of Players playing on the chess board.
         */

        HUMAN,
        COMPUTER
    }

    private class TilePanel extends JPanel {

        /*
        This class is the JPanel implementation of each of the 64 Tiles on a standard Board. Each
        TilePanel may be empty, have a piece icon, a green dot that represents a possible Move, or
        both the piece icon and the green dot side by side.

        The TilePanels are arranged on the Board, with their background colors determined based on
        their tileId, in order to achieve the checkerboard pattern.

        Each TilePanel has a mouseListener that detects mouse clicks, where a left click will
        determine the Piece to select or the Tile to make a Move to, and a right click will cancel
        any previously selected Piece/Tile.

        Once a Move has been made, the BoardPanel of the chess board is updated, as well as the
        gameHistoryPanel and takenPiecesPanel.
            - tileId: the integer that corresponds to the coordinate position of the Tile on the
                      Board.
         */

        private final int tileId;

        /**
         * Constructor for a new TilePanel on an assigned coordinate on the Board. The TilePanel
         * will have a color and a Piece icon based on its coordinate, and will have a mouseListener
         * that will detect left clicks and right clicks for selection/deselection.
         *
         * @param boardPanel the BoardPanel on which the TilePanel exists
         * @param tileId the specified coordinate on the Board
         */
        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else if (isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard,
                                sourceTile.getTileCoordinate(),
                                destinationTile.getTileCoordinate());
                            final MoveTransition transition =
                                chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(() -> {
                            gameHistoryPanel.redo(chessBoard, moveLog);
                            takenPiecesPanel.redo(moveLog);
                            if (gameSetup.isAIPlayer(chessBoard.currentPlayer())) {
                                Table.get().moveMadeUpdate(PlayerType.HUMAN);
                            }
                            boardPanel.drawBoard(chessBoard);
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();
        }

        /**
         * This method updates a TilePanel given data from a Board.
         *
         * @param board the Board from which data is used to update the TilePanel if necessary
         */
        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }

        /**
         * This method will assign a new Piece icon data from a Board. No icon is assigned if there
         * exists no Piece on the Tile.
         *
         * @param board the Board from which data is read
         */
        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTile(this.tileId).isTileOccupied()) {
                try {
                    final BufferedImage image = ImageIO.read(new File(defaultImagesPath +
                        board.getTile(this.tileId).getPiece().getPieceAlliance().toString().
                            charAt(0) + board.getTile(this.tileId).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * This method shows the green dot icon for the legal Moves of a selected Piece, if the
         * given option is enabled in the Preferences menu.
         *
         * @param board the Board from which data is read
         */
        private void highlightLegals(final Board board) {
            if (highlightLegalMoves) {
                for (final Move move : pieceLegalMoves(board)) {
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(
                                new File("art/misc/green_dot.png")))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        /**
         * This helper method calculates the legal Moves for a Piece that has been selected by the
         * user. Used for highlightLegals.
         *
         * @param board the Board from which data is read
         * @return a collection of the legal Moves that the selected Piece can make
         */
        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() ==
                board.currentPlayer().getAlliance()) {
                final List<Move> pieceMoves = new ArrayList<>();
                for (final Move move : board.currentPlayer().getLegalMoves()) {
                    if (move.getMovedPiece() == humanMovedPiece) {
                        pieceMoves.add(move);
                    }
                }
                return pieceMoves;
            }
            return Collections.emptyList();
        }

        /**
         * This method assigns the background color of the TilePanel, based on its coordinate on the
         * Board.
         */
        private void assignTileColor() {
            if (BoardUtils.EIGHTH_RANK[this.tileId] || BoardUtils.SIXTH_RANK[this.tileId] ||
                BoardUtils.FOURTH_RANK[this.tileId] || BoardUtils.SECOND_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.SEVENTH_RANK[this.tileId] || BoardUtils.FIFTH_RANK[this.tileId] ||
                       BoardUtils.THIRD_RANK[this.tileId] || BoardUtils.FIRST_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }
}
