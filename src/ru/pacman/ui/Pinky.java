package ru.pacman.ui;

import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.Point2D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

class Pinky extends JComponent implements Ghost {
    private int x;
    private int y;
    Image picture;

    Pinky(DetailedPoint2D coords) {
        updatePosition(coords);

        try {
            picture = ImageIO.read(new File("resources/Ghosts/pinky.png"));
        } catch (Throwable err) {
            System.out.println("Can't open image with pink ghost!");
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(picture, x, y, PacmanField.objectSize, PacmanField.objectSize, null, null);
    }

    @Override
    public String getGhostName() {
        return "Pinky";
    }

    @Override
    public void updatePosition(DetailedPoint2D position) {
        x = position.x * PacmanField.objectSize / 10;
        y = position.y * PacmanField.objectSize / 10;
    }
}
