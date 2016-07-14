package ru.pacman.ui;

import javax.swing.*;
import java.awt.*;

public class SimpleDot extends JComponent implements PacmanLevelObject {
    private int xPos;
    private int yPos;
    private boolean isActive = true;

    SimpleDot(int _x, int _y) {
        xPos = _x;
        yPos = _y;
        setPreferredSize(new Dimension(PacmanField.objectSize, PacmanField.objectSize));
        setVisible(true);
    }

    public void deactivate() { isActive = false; }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.black);
        Dimension size = getPreferredSize();
        g.fillRect(xPos, yPos, xPos + size.width, yPos + size.height);

        if (isActive) {
            g.setColor(Color.white);
            g.fillOval(xPos + size.width / 3, yPos + size.height / 3, size.width / 3, size.height / 3);
        }
    }
}
