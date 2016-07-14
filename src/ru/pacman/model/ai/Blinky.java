package ru.pacman.model.ai;

import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Blinky extends GhostAI {
    GameModel model;
    Point2D<Integer> currentPosition;
    Point2D<Integer> previousPosition = new Point2D<>();
    private boolean blockAxisX = false;
    private boolean blockAxisY = false;

    public Blinky(GameModel _model) {
        model = _model;
        currentPosition = model.getCharacterCoords("Blinky");
        previousPosition = new Point2D<>(currentPosition.x, currentPosition.y);
    }

    @Override
    public void move() {
        boolean moveState = moveAlgo();

        /*if (!moveState)
            System.out.println("Blinky strategy error!");
        */model.checkGhostsAttack();
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
    public Point2D<Integer> getTargetTile() {
        return model.getCharacterCoords("Pacman");
    }

    @Override
    public Point2D<Integer> getCurrentCoordinates() {
        return currentPosition;
    }

    @Override
    Point2D<Integer> getPreviousPosition() {
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
    GameModel getGameModel() {
        return model;
    }
}
