package ru.pacman.ui;

import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Pacman extends JComponent implements PacmanLevelObject {
    private int x;
    private int y;
    private GameModel.Orientation orientation;
    Image picture_left;
    Image picture_right;
    Image picture_up;
    Image picture_down;

    Pacman(Point2D<Integer> coords) {
        updateCoords(coords);

        try {
            picture_left = ImageIO.read(new File("resources/pacman_left.png"));
            picture_right = ImageIO.read(new File("resources/pacman_right.png"));
            picture_up = ImageIO.read(new File("resources/pacman_up.png"));
            picture_down = ImageIO.read(new File("resources/pacman_down.png"));
        } catch (Throwable err) {
            System.out.println("Can't open image with pacman!");
        }
    }

    public void updateCoords(Point2D<Integer> coords) {
        x = coords.x * 2;
        y = coords.y * 2;
    }

    public void updateOrientation(GameModel.Orientation _orientation) {
        orientation = _orientation;
    }

    @Override
    public void paint(Graphics g) {
        switch (orientation) {
            case UP:
                g.drawImage(picture_up, x, y, PacmanField.objectSize, PacmanField.objectSize, null, null);
                break;

            case DOWN:
                g.drawImage(picture_down, x, y, PacmanField.objectSize, PacmanField.objectSize, null, null);
                break;

            case LEFT:
                g.drawImage(picture_left, x, y, PacmanField.objectSize, PacmanField.objectSize, null, null);
                break;

            case RIGHT:
                g.drawImage(picture_right, x, y, PacmanField.objectSize, PacmanField.objectSize, null, null);
                break;
        }

    }
}
