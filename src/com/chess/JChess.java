package com.chess;

import com.chess.engine.board.Board;
import com.chess.gui.Table;

public class JChess {

    /**
     * Creates the instance of the standard chess game board, prints the String representation and
     * displays the GUI.
     */
    public static void main(String[] args) {
        Board board = Board.createStandardBoard();
        System.out.println(board);
        Table.get().show();
    }
}
