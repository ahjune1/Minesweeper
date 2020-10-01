package minesweeper;

import javax.swing.*;

public class MineSweeperStart extends JFrame {

    public MineSweeperStart() {
        int rows = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of rows (Min 10, Max 16)"));
        int columns = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of cols (Min 10, Max 30)"));
        int mines = Integer.parseInt(JOptionPane.showInputDialog("Convert what % of squares into mines? (Min 10, Max 20)"));
        makeNewGame(rows, columns, mines);
    }
    public void makeNewGame(int rows, int columns, int mines) {
        this.add(new MineSweeperPanel(rows, columns, mines));
        this.pack();
        this.setTitle("Minesweeper");
    }
}
