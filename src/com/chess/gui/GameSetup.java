package com.chess.gui;

import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.chess.engine.Alliance;
import com.chess.engine.player.Player;
import com.chess.gui.Table.PlayerType;

class GameSetup extends JDialog {

    /*
    This class is the JDialog implementation of a dialog box that opens when the Setup Game option
    is clicked from the main GUI. It prompts the user to setup a new game, with either human or AI
    players.
        - whitePlayerType: the PlayerType of the Player in control of the white Pieces.
        - blackPlayerType: the PlayerType of the Player in control of the black Pieces.
     */

    private PlayerType whitePlayerType;
    private PlayerType blackPlayerType;

    private static final String HUMAN_TEXT = "Human";
    private static final String COMPUTER_TEXT = "Computer";

    /**
     * Constructor for a new JDialog box, given the frame from which it is called and a specified
     * modality.
     *
     * @param frame the frame from which the GameSetup dialog box is called
     * @param modal the modality of the dialog box
     */
    GameSetup(final JFrame frame, final boolean modal) {
        super(frame, modal);
        final JPanel myPanel = new JPanel(new GridLayout(0, 1));
        final JRadioButton whiteHumanButton = new JRadioButton(HUMAN_TEXT);
        final JRadioButton whiteComputerButton = new JRadioButton(COMPUTER_TEXT);
        final JRadioButton blackHumanButton = new JRadioButton(HUMAN_TEXT);
        final JRadioButton blackComputerButton = new JRadioButton(COMPUTER_TEXT);
        whiteHumanButton.setActionCommand(HUMAN_TEXT);
        final ButtonGroup whiteGroup = new ButtonGroup();
        whiteGroup.add(whiteHumanButton);
        whiteGroup.add(whiteComputerButton);
        whiteHumanButton.setSelected(true);

        final ButtonGroup blackGroup = new ButtonGroup();
        blackGroup.add(blackHumanButton);
        blackGroup.add(blackComputerButton);
        blackHumanButton.setSelected(true);

        getContentPane().add(myPanel);
        myPanel.add(new JLabel("White"));
        myPanel.add(whiteHumanButton);
        myPanel.add(whiteComputerButton);
        myPanel.add(new JLabel("Black"));
        myPanel.add(blackHumanButton);
        myPanel.add(blackComputerButton);

        final JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("OK");

        okButton.addActionListener(e -> {
            whitePlayerType = whiteComputerButton.isSelected() ? PlayerType.COMPUTER :
                PlayerType.HUMAN;
            blackPlayerType = blackComputerButton.isSelected() ? PlayerType.COMPUTER :
                PlayerType.HUMAN;
            GameSetup.this.setVisible(false);
        });

        cancelButton.addActionListener(e -> {
            System.out.println("Cancel");
            GameSetup.this.setVisible(false);
        });

        myPanel.add(cancelButton);
        myPanel.add(okButton);

        setLocationRelativeTo(frame);
        pack();
        setVisible(false);
    }

    /**
     * This method sets the dialog screen to be visible to the user.
     */
    void promptUser() {
        setVisible(true);
        repaint();
    }

    /**
     * Determines whether or not the specified Player is controlled by the AI.
     *
     * @param player the Player being examined
     * @return true if the Player is an AI, and false otherwise
     */
    boolean isAIPlayer(final Player player) {
        if (player.getAlliance() == Alliance.WHITE) {
            return getWhitePlayerType() == PlayerType.COMPUTER;
        }
        return getBlackPlayerType() == PlayerType.COMPUTER;
    }

    PlayerType getWhitePlayerType() {
        return this.whitePlayerType;
    }

    PlayerType getBlackPlayerType() {
        return this.blackPlayerType;
    }
}
