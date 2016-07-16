package ru.pacman.model.ai;

import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;
import ru.pacman.model.gamelevel.GameLevel;

import java.rmi.MarshalledObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Clyde extends GhostAI {
    GameModel model;
    Point2D<Integer> previousPosition = new Point2D<>(-1, -1);
    Point2D<Integer> currentPosition = null;
    private boolean blockAxisX = false;
    private boolean blockAxisY = false;
    private boolean usingTeleport = false;

    public Clyde(GameModel _model) {
        model = _model;
        currentPosition = model.getCharacterCoords("Clyde");
    }

    @Override
    public void move() {
        if (model.getDotsCounter() * 3 < model.getMaxDotsCount())
            return;

        boolean moveState = moveAlgo();
        /*if (!moveState)
            System.out.println("Clyde strategy error!");
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

    /* TODO: Recheck this method */
    @Override
    public Point2D<Integer> getTargetTile() {
        Point2D<Integer> pacmanPosition = model.getCharacterCoords("Pacman");
        GameModel.Orientation pacmanOrientation = model.getPacmanOrientation();
        Point2D<Integer> target = new Point2D<>(0, 0);

        double lengthToPacman = Math.sqrt((Math.pow(pacmanPosition.x - currentPosition.x, 2) +
                Math.pow(pacmanPosition.y - currentPosition.y, 2)));

        if (lengthToPacman > 8) {
            return pacmanPosition;
        } else {
            target.x = -5;
            target.y = (GameLevel.objectsOnYAxis + 5) * 10;
            return target;
        }
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
