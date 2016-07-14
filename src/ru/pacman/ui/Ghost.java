package ru.pacman.ui;

import ru.pacman.model.Point2D;
import java.awt.*;

public interface Ghost {
    void paint(Graphics g);
    String getGhostName();
    void updatePosition(Point2D<Integer> position);
}
