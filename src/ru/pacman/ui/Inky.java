package ru.pacman.ui;

import ru.pacman.model.Point2D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

class Inky extends JComponent implements Ghost {
    private int x;
    private int y;
    Image picture;

    Inky(Point2D<Integer> coords) {
        updatePosition(coords);

        try {
            picture = ImageIO.read(new File("resources/Ghosts/inky.png"));
        } catch (Throwable err) {
            System.out.println("Can't open image with blue ghost!");
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(picture, x, y, PacmanField.objectSize, PacmanField.objectSize, null, null);
    }

    @Override
    public String getGhostName() {
        return "Inky";
    }

    @Override
    public void updatePosition(Point2D<Integer> position) {
        x = position.x * 2;
        y = position.y * 2;
    }
}
