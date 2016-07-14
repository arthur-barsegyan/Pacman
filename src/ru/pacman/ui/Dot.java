package ru.pacman.ui;

import javax.swing.*;
import java.awt.*;

public class Dot extends JComponent implements PacmanLevelObject {
    private int xPos;
    private int yPos;
    private boolean isActive = true;
    private boolean isSuperDot = false;

    Dot(int _x, int _y, boolean _isSuperDot) {
        xPos = _x;
        yPos = _y;
        setPreferredSize(new Dimension(PacmanField.objectSize, PacmanField.objectSize));
        isSuperDot = _isSuperDot;
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

            if (isSuperDot)
                /* TODO: Make it more pretty */
                g.fillOval(xPos + size.width / 2, yPos + size.height / 2, size.width / 2, size.height / 2);
            else
                g.fillOval(xPos + size.width / 3, yPos + size.height / 3, size.width / 3, size.height / 3);
        }
    }
}
