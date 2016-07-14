package ru.pacman.model.ai;

import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Inky extends GhostAI {
    GameModel model;
    Point2D<Integer> previousPosition = new Point2D<>(-1, -1);
    Point2D<Integer> currentPosition = null;
    private boolean blockAxisX = false;
    private boolean blockAxisY = false;

    public Inky(GameModel _model) {
        model = _model;
        currentPosition = model.getCharacterCoords("Inky");
    }

    @Override
    public void move() {
        if (model.getDotsCounter() < 30)
            return;

        boolean moveState = moveAlgo();
        /*if (!moveState)
            System.out.println("Inky strategy error!");
        */model.checkGhostsAttack();
    }

    @Override
    void setCurrentPosition(int x, int y) {
        currentPosition.x = x;
        currentPosition.y = y;
    }

    @Override
    Point2D<Integer> getPreviousPosition() {
        return previousPosition;
    }

    @Override
    public Point2D<Integer> getTargetTile() {
        Point2D<Integer> pacmanPosition = model.getCharacterCoords("Pacman");
        GameModel.Orientation pacmanOrientation = model.getPacmanOrientation();
        Point2D<Integer> target = new Point2D<>(0, 0);

        /* TODO: Remove hardcoded constants */
        switch (pacmanOrientation) {
            case UP:
                target.setLocation(pacmanPosition.x, pacmanPosition.y - 20);
                break;

            case DOWN:
                target.setLocation(pacmanPosition.x, pacmanPosition.y + 20);
                break;

            case LEFT:
                target.setLocation(pacmanPosition.x - 20, pacmanPosition.y);
                break;

            case RIGHT:
                target.setLocation(pacmanPosition.x + 20, pacmanPosition.y);
                break;
        }

        Point2D<Integer> blinkyPosition = model.getCharacterCoords("Blinky");
        int absX = Math.abs(blinkyPosition.x - target.x);
        int absY = Math.abs(blinkyPosition.y - target.y);

        if (pacmanPosition.x > blinkyPosition.x && pacmanPosition.y < blinkyPosition.y) {
            target.setLocation(pacmanPosition.x - 2 * absX, pacmanPosition.y - 2 * absY);
        } else if (pacmanPosition.x < blinkyPosition.x && pacmanPosition.y > blinkyPosition.y) {
            target.setLocation(pacmanPosition.x + 2 * absX, pacmanPosition.y - 2 * absY);
        } else if (pacmanPosition.x < blinkyPosition.x && pacmanPosition.y < blinkyPosition.y) {
            target.setLocation(pacmanPosition.x + 2 * absX, pacmanPosition.y + 2 * absY);
        } else {
            target.setLocation(pacmanPosition.x, pacmanPosition.y + 20);
        }

        return target;
    }

    @Override
    public Point2D<Integer> getCurrentCoordinates() {
        return currentPosition;
    }

    @Override
    public boolean isBlockingOnAxisX() {
        return blockAxisX;
    }

    @Override
    public boolean isBlockingOnAxisY() {
        return blockAxisY;
    }

    @Override
    public void setBlockingOnAxisX(boolean state) {
        blockAxisX = state;
    }

    @Override
    public void setBlockingOnAxisY(boolean state) {
        blockAxisY = state;
    }

    void setPreviousPosition() {
        previousPosition.x = currentPosition.x;
        previousPosition.y = currentPosition.y;
    }

    @Override
    GameModel getGameModel() {
        return model;
    }
}
