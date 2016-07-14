package ru.pacman.model;

import ru.pacman.model.ai.*;
import ru.pacman.model.gamelevel.GameLevel;
import ru.pacman.ui.Ghost;

import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ru.pacman.model.GameModel.MoveRules.LeftUpRule;
import static ru.pacman.model.GameModel.MoveRules.RightDownRule;

public class GameModel {
    public static final int GAMESTART = 1;
    public static final int GAMEEND = 2;
    private PacmanResourceManager resources;
    private List<String> levelNameList;
    private byte currentLevel[];

    private String ghostsNames[] = {"Blinky", "Pinky", "Inky", "Clyde"};
    private ArrayList<GhostAI> ghosts;
    private boolean ghostAttack = false;

    private int levelNumber = 0;
    private boolean levelOver = false;
    private boolean gameOver = false;
    private boolean winStatus = false;
    private int gameScore = 0;

    private int pacmanSpeed = 5;
    private Orientation pacmanOrientation = Orientation.RIGHT;
    private Point2D<Integer> pacmanCoords;
    private boolean changeDirection = false;
    private boolean blockAxisX = false;
    private boolean blockAxisY = false;
    private boolean isIntermediatePosition = false;

    private int dotsCounter = 0;
    private int dotsMaxCounter;

    private final static int SIMPLEDOT_BONUS = 10;
    private final static int ENEMY_BONUS = 200;
    private final static int objectSize = 10;

    enum Axis {
        X,
        Y
    }

    enum MoveRules {
        RightDownRule,
        LeftUpRule
    }

    public enum Orientation {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    /* This constructor receive file name which contains list with
       game levels file names. This method loading first level
       from this list. */
    public GameModel(String _levelNameList) {
        try {
            levelNameList = Files.readAllLines(Paths.get(_levelNameList));
            resources = new PacmanResourceManager();
            resources.loadLevel(levelNameList.get(levelNumber));
            currentLevel = getCurrentLevel().getLevelData();
            pacmanCoords = getCharacterCoords("Pacman");

            ghosts = new ArrayList<>();
            ghosts.add(new Blinky(this));
            ghosts.add(new Pinky(this));
            ghosts.add(new Clyde(this));
            ghosts.add(new Inky(this));

            dotsMaxCounter = getPrimaryDotsCount();
            levelNumber++;
        } catch (Exception a) {

        }
    }

    /* TODO: It's normal practise? */
    public void gameStart() {
        //resources.handleSoundEvent("gamestart");
        resources.handleSoundEvent("chasemode");
    }

    public int getObjectSize() { return objectSize; }
    public int getDotsCounter() {
        return dotsCounter;
    }

    public Point2D<Integer> getCharacterCoords(String characterName) {
        return getCurrentLevel().getDefaultCoords(characterName);
    }

    public void updateGhostsPosition() {
        for (GhostAI currentGhost : ghosts) {
            currentGhost.move();
        }
    }

    /* This method should receive special class - Ghost, which should contains of special methods for
       getting current coordinates and other special attributes */
    public ArrayList<Point2D<Integer>> getPathFromPosition(GhostAI currentGhost) {
        Point2D<Integer> currentPosition = currentGhost.getCurrentCoordinates();
        int xcells[] = {currentPosition.x + 1, currentPosition.x - 1, currentPosition.x, currentPosition.x};
        int ycells[] = {currentPosition.y, currentPosition.y, currentPosition.y + 1, currentPosition.y - 1};
        MoveRules rules[] = {MoveRules.RightDownRule, MoveRules.LeftUpRule, MoveRules.RightDownRule, MoveRules.LeftUpRule};
        Orientation side[] = {Orientation.RIGHT, Orientation.LEFT, Orientation.DOWN, Orientation.UP};
        Axis currentAxis[] = {Axis.X, Axis.X, Axis.Y, Axis.Y};
        ArrayList<Point2D<Integer>> paths = new ArrayList<>();

        /* TODO: BorderSafety ?! */
        /* TODO: We can't moving in ghost hotel */
        for (int i = 0; i < xcells.length; i++) {
            Axis axis = currentAxis[i];
            switch (axis) {
                case X:
                    if (currentGhost.isBlockingOnAxisY())
                        continue;
                    break;

                case Y:
                    if (currentGhost.isBlockingOnAxisX())
                        continue;
                    break;
            }

            if (!isBorderSafety(xcells[i], ycells[i]))
                continue;

            Point2D<Integer> cellPosition = getNewPosition(xcells[i], ycells[i], rules[i]);
            char cellType = getCellType(cellPosition.x, cellPosition.y);
            if (cellType == GameLevel.SIMPLEDOT || cellType == GameLevel.ROAD)
                paths.add(new Point2D(xcells[i], ycells[i]));
        }

        return paths;
    }

    public boolean isSpecialIntersection(Point2D<Integer> currentIntersection) {
        List<Point2D<Integer>> specialIntersections = resources.getSpecialIntersectionsList();

        for (Point2D<Integer> nonIntersection : specialIntersections) {
            if (nonIntersection.equals(currentIntersection))
                return true;
        }

        return false;
    }

    public Orientation getPacmanOrientation() {
        return pacmanOrientation;
    }

    private void updateScore(int newAchieve) {
        gameScore += newAchieve;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getMaxDotsCount() {
        return dotsMaxCounter;
    }

    public int getPrimaryDotsCount() {
        int counter = 0;
        for (int i = 0; i < currentLevel.length; i++) {
            if ((char)currentLevel[i] == GameLevel.SIMPLEDOT) {
                counter++;
            }
        }

        return counter;
    }

    public boolean isLevelOver() {
        /* TODO: Why I should check ghostAttack flag? */
        if (getDotsCounter() == getMaxDotsCount() && !ghostAttack)
            levelOver = true;

        return levelOver;
    }

    /* Load new level from file. If model doesn't knew about next levels
     * then game is over and user has won. */
    public boolean loadNextLevel() {
        levelNumber++;
        if (levelNumber > levelNameList.size()) {
            winStatus = true;
            gameOver = true;
            return false;
        }

        resources.loadLevel(levelNameList.get(levelNumber));
        return true;
    }

    private Point2D<Integer> getNewPosition(int diff_x, int diff_y, MoveRules rule) {
        int fractionDiff_x = diff_x / objectSize;
        int fractionDiff_y = diff_y / objectSize;
        int integerDiff_x = diff_x % objectSize;
        int integerDiff_y = diff_y % objectSize;

        /* Cell border checker */
        switch (rule) {
            case RightDownRule: {
                if (integerDiff_x > 0)
                    integerDiff_x = fractionDiff_x + 1;
                else
                    integerDiff_x = fractionDiff_x;

                if (integerDiff_y > 0)
                    integerDiff_y = fractionDiff_y + 1;
                else
                    integerDiff_y = fractionDiff_y;

                break;
            }

            case LeftUpRule: {
                integerDiff_x = fractionDiff_x;
                integerDiff_y = fractionDiff_y;
                break;
            }
        }

        Point2D<Integer> newPosition = new Point2D(integerDiff_x, integerDiff_y);
        return newPosition;
    }

    private char getCellType(int x, int y) {
        GameLevel currentLvl = getCurrentLevel();
        byte level[] = currentLvl.getLevelData();
        return (char)level[y * GameLevel.objectsOnXAxis + x];
    }

    private void setCellType(int x, int y, char newValue) {
        GameLevel currentLvl = getCurrentLevel();
        byte level[] = currentLvl.getLevelData();
        level[y * GameLevel.objectsOnXAxis + x] = (byte)newValue;
    }

    private boolean isBorderSafety(int diff_x, int diff_y) {
        if ((diff_x < (GameLevel.objectsOnXAxis * 10) && diff_x >= 0) && (diff_y < (GameLevel.objectsOnYAxis * 10) && diff_y >= 0))
            return true;

        return false;
    }

    private boolean newPositionChecker(int diff_x, int diff_y, MoveRules rule) {
        if (isBorderSafety(diff_x, diff_y)) {
            Point2D<Integer> newPosition = getNewPosition(diff_x, diff_y, rule);

            if (diff_x % objectSize > 0) {
                blockAxisX = true;
                isIntermediatePosition = true;
            } else {
                blockAxisX = false;
                isIntermediatePosition = false;
            }

            if (diff_y % objectSize > 0) {
                blockAxisY = true;
                isIntermediatePosition = true;
            } else {
                blockAxisY = false;
                isIntermediatePosition = false;
            }

            switch (pacmanOrientation) {
                case UP:
                    if (blockAxisX)
                        return false;
                    break;
                case DOWN:
                    if (blockAxisX)
                        return false;
                    break;
                case LEFT:
                    if (blockAxisY)
                        return false;
                    break;
                case RIGHT:
                    if (blockAxisY)
                        return false;
                    break;
            }

            char currentLocation = getCellType(newPosition.x, newPosition.y);
            if (currentLocation != GameLevel.WALL) {
                pacmanCoords.setLocation(diff_x, diff_y);
                return true;
            }
        }

        return false;
    }

    private void handleMovementAction(int diff_x, int diff_y, MoveRules rule) {
        boolean newPosState = newPositionChecker(diff_x, diff_y, rule);

        if (newPosState) {
            Point2D<Integer> newPosition = getNewPosition(diff_x, diff_y, rule);
            char newCellType = getCellType(newPosition.x, newPosition.y);

            switch (newCellType) {
                case GameLevel.ROAD:
                    break;
                case GameLevel.SIMPLEDOT:
                    dotsCounter++;
                    updateScore(SIMPLEDOT_BONUS);
                    //resources.handleSoundEvent("simpledot");
                    setCellType(newPosition.x, newPosition.y, GameLevel.ROAD);
                    break;
            }

        }
    }

    public void checkGhostsAttack() {
        for (String currentGhost : ghostsNames) {
            if (pacmanCoords.isEquals(getCharacterCoords(currentGhost))) {
                ghostAttack = true;
                gameOver();
                return;
            }
        }
    }

    private void gameOver() {
        if (ghostAttack) {
            resources.handleSoundEvent("gameover");
        }

        gameOver = true;
    }

    public void changePacmanOrientation(Orientation orientation) {
        pacmanOrientation = orientation;
    }

    public void newAction() {
        int x = pacmanCoords.x;
        int y = pacmanCoords.y;

        switch (pacmanOrientation) {
            case UP:
                handleMovementAction(x, y - pacmanSpeed, MoveRules.LeftUpRule);
                break;
            case LEFT:
                handleMovementAction(x - pacmanSpeed, y, MoveRules.LeftUpRule);
                break;
            case RIGHT:
                handleMovementAction(x + pacmanSpeed, y, RightDownRule);
                break;
            case DOWN:
                handleMovementAction(x, y + pacmanSpeed, RightDownRule);
                break;
            /*case (GAMESTART):
                resources.handleSoundEvent("gamestart");
                break;
            case (GAMEEND):
                resources.handleSoundEvent("gameend");
                break;*/
            default:
                return;
        }
    }

    public GameLevel getCurrentLevel() {
        return resources.getCurrentLevel();
    }
}