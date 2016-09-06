package ru.pacman.model.ai;

import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;
import ru.pacman.ui.PacmanField;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Pinky extends GhostAI {
    GameModel model;
    DetailedPoint2D previousPosition = new DetailedPoint2D(-1, -1);
    DetailedPoint2D currentPosition = null;
    private boolean blockAxisX = false;
    private boolean blockAxisY = false;
    private boolean usingTeleport = false;
    private boolean insideTheHotel = true;

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
    public DetailedPoint2D getTargetTile() {
        DetailedPoint2D pacmanPosition = model.getCharacterCoords("Pacman");
        GameModel.Orientation pacmanOrientation = model.getPacmanOrientation();
        DetailedPoint2D target = new DetailedPoint2D(0, 0);

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
    DetailedPoint2D getPreviousPosition() {
        return previousPosition;
    }

    @Override
    public DetailedPoint2D getCurrentCoordinates() {
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
    protected void setHotelOutState(boolean state) {
        insideTheHotel = !state;
    }

    @Override
    protected boolean isInsideTheHotel() {
        return insideTheHotel;
    }

    @Override
    GameModel getGameModel() {
        return model;
    }
}
