package ru.pacman.model.ai;

import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Blinky extends GhostAI {
    GameModel model;
    DetailedPoint2D currentPosition;
    DetailedPoint2D previousPosition = new DetailedPoint2D();
    private boolean blockAxisX = false;
    private boolean blockAxisY = false;
    private boolean usingTeleport = false;
    private boolean insideTheHotel = true;

    public Blinky(GameModel _model) {
        model = _model;
        currentPosition = model.getCharacterCoords("Blinky");
        previousPosition = new DetailedPoint2D(currentPosition.x, currentPosition.y);
    }

    @Override
    public void move() {
        boolean moveState = moveAlgo();

        if (!moveState)
            System.out.println("Blinky strategy error!");
        model.checkGhostsAttack();
    }

    void setPreviousPosition() {
        previousPosition.x = currentPosition.x;
        previousPosition.y = currentPosition.y;
    }

    @Override
    void setCurrentPosition(int x, int y) {
        currentPosition.x = x;
        currentPosition.y = y;
    }


    @Override
    public DetailedPoint2D getTargetTile() {
        return model.getCharacterCoords("Pacman");
    }

    @Override
    public DetailedPoint2D getCurrentCoordinates() {
        return currentPosition;
    }

    @Override
    DetailedPoint2D getPreviousPosition() {
        return previousPosition;
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
