package ru.pacman.model.ai;

import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Inky extends GhostAI {
    GameModel model;
    DetailedPoint2D previousPosition = new DetailedPoint2D(-1, -1);
    DetailedPoint2D currentPosition = null;
    private boolean blockAxisX = false;
    private boolean blockAxisY = false;
    private boolean usingTeleport = false;
    private boolean insideTheHotel = true;

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
        currentPosition.getX() = x;
        currentPosition.getY() = y;
    }

    @Override
    DetailedPoint2D getPreviousPosition() {
        return previousPosition;
    }

    @Override
    public DetailedPoint2D getTargetTile() {
        DetailedPoint2D pacmanPosition = model.getCharacterCoords("Pacman");
        GameModel.Orientation pacmanOrientation = model.getPacmanOrientation();
        DetailedPoint2D target = new DetailedPoint2D(0, 0);

        /* TODO: Remove hardcoded constants */
        switch (pacmanOrientation) {
            case UP:
                target.setLocation(pacmanPosition.getX(), pacmanPosition.getY() - 20);
                break;

            case DOWN:
                target.setLocation(pacmanPosition.getX(), pacmanPosition.getY() + 20);
                break;

            case LEFT:
                target.setLocation(pacmanPosition.getX() - 20, pacmanPosition.getY());
                break;

            case RIGHT:
                target.setLocation(pacmanPosition.getX() + 20, pacmanPosition.getY());
                break;
        }

        DetailedPoint2D blinkyPosition = model.getCharacterCoords("Blinky");
        int absX = Math.abs(blinkyPosition.getX() - target.getX());
        int absY = Math.abs(blinkyPosition.getY() - target.getY());

        if (pacmanPosition.getX() > blinkyPosition.getX() && pacmanPosition.getY() < blinkyPosition.getY()) {
            target.setLocation(pacmanPosition.getX() - 2 * absX, pacmanPosition.getY() - 2 * absY);
        } else if (pacmanPosition.getX() < blinkyPosition.getX() && pacmanPosition.getY() > blinkyPosition.getY()) {
            target.setLocation(pacmanPosition.getX() + 2 * absX, pacmanPosition.getY() - 2 * absY);
        } else if (pacmanPosition.getX() < blinkyPosition.getX() && pacmanPosition.getY() < blinkyPosition.getY()) {
            target.setLocation(pacmanPosition.getX() + 2 * absX, pacmanPosition.getY() + 2 * absY);
        } else {
            target.setLocation(pacmanPosition.getX(), pacmanPosition.getY() + 20);
        }

        return target;
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
        previousPosition.getX() = currentPosition.getX();
        previousPosition.getY() = currentPosition.getY();
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
