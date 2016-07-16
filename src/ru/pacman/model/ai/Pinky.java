package ru.pacman.model.ai;

import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;
import ru.pacman.ui.PacmanField;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Pinky extends GhostAI {
    GameModel model;
    Point2D<Integer> previousPosition = new Point2D<>(-1, -1);
    Point2D<Integer> currentPosition = null;
    private boolean blockAxisX = false;
    private boolean blockAxisY = false;
    private boolean usingTeleport = false;

    public Pinky(GameModel _model) {
        model = _model;
        currentPosition = model.getCharacterCoords("Pinky");
    }

    @Override
    public void move() {
        boolean moveState = moveAlgo();

        /*if (!moveState)
            System.out.println("Pinky strategy error!");
        */model.checkGhostsAttack();
    }

    @Override
    public Point2D<Integer> getTargetTile() {
        Point2D<Integer> pacmanPosition = model.getCharacterCoords("Pacman");
        GameModel.Orientation pacmanOrientation = model.getPacmanOrientation();
        Point2D<Integer> target = new Point2D<>(0, 0);

        /* TODO: Remove hardcoded constants */
        switch (pacmanOrientation) {
            case UP:
                target.setLocation(pacmanPosition.x, pacmanPosition.y - 40);
                break;

            case DOWN:
                target.setLocation(pacmanPosition.x, pacmanPosition.y + 40);
                break;

            case LEFT:
                target.setLocation(pacmanPosition.x - 40, pacmanPosition.y);
                break;

            case RIGHT:
                target.setLocation(pacmanPosition.x + 40, pacmanPosition.y);
                break;
        }

        return target;
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
    protected void usingTeleport(boolean state) {
        usingTeleport = state;
    }

    @Override
    protected boolean afterTeleport() {
        return usingTeleport;
    }

    @Override
    GameModel getGameModel() {
        return model;
    }
}
