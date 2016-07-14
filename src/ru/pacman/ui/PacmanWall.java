package ru.pacman.ui;

import javax.swing.*;
import java.awt.*;

public class PacmanWall extends JComponent implements PacmanLevelObject {
    private int columnPos;
    private int rowPos;

    PacmanWall(int _columnPos, int _rowPos) {
        columnPos = _columnPos;
        rowPos = _rowPos;
        setPreferredSize(new Dimension(PacmanField.objectSize, PacmanField.objectSize));
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) { // getWidth() == getHeight() == 0 ???
        g.setColor(Color.gray);
        Dimension size = getPreferredSize();
        g.fillRect(columnPos, rowPos, columnPos + size.width, rowPos + size.height);
    }

    public Dimension getSize() { return getSize(); }
}
