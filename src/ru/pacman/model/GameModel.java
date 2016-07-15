package ru.pacman.model;

import javafx.util.Pair;
import ru.pacman.model.ai.*;
import ru.pacman.model.gamelevel.GameLevel;
import ru.pacman.model.gamelevel.LevelFileFormatException;
import ru.pacman.ui.Ghost;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;
import static ru.pacman.model.GameModel.MoveRules.LeftUpRule;
import static ru.pacman.model.GameModel.MoveRules.RightDownRule;

/* TODO: Refactor this class */
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
    private final static int SUPERDOT_BONUS = 100;
    private final static int ENEMY_BONUS = 200;
    private final static int objectSize = 10;
    private boolean afterTeleport = false;

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

    /* This constructor receive file which contains list with
       game levels file names. This method loading first level
       from this list. */
    public GameModel(String _levelNameList) throws LevelFileFormatException, IOException {
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
        } catch (LevelFileFormatException err) {
            throw err;
        } catch (IOException err) {
            throw err;
        }
    }

    /* TODO: It's normal practise? */
    public void gameStart() {
        //resources.handleSoundEvent("gamestart");
        //resources.handleSoundEvent("chasemode");
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
        if (getDotsCounter() == getMaxDotsCount())
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

        try {
            resources.loadLevel(levelNameList.get(levelNumber));
        } catch (LevelFileFormatException err) {
            return false;
        }

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

    /* TODO: Is it a normal practise? */
    private Pair<Boolean, Point2D<Integer>> isTeleportationPoint(Point2D<Integer> newCoordinates) {
        List<Pair<Point2D<Integer>, Point2D<Integer>>> teleportationPoints = resources.getTeleportationPoints();

        try {
            for (Pair<Point2D<Integer>, Point2D<Integer>> currentTeleportationPoint : teleportationPoints) {
                if (currentTeleportationPoint.getKey().isEquals(newCoordinates)) {
                    return new Pair<>(true, currentTeleportationPoint.getValue());
                }
            }
        } catch (NullPointerException err) {}

        return new Pair<>(false, null);
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
            if (currentLocation != GameLevel.WALL)
                return true;
        }

        return false;
    }

    private MoveRules getRuleByOrientation(Orientation orientation) {
        switch (orientation) {
            case UP:
                return LeftUpRule;
            case LEFT:
                return LeftUpRule;
            case RIGHT:
                return RightDownRule;
            case DOWN:
                return RightDownRule;
            /* TODO: WTF?! */
            default:
                return null;
        }
    }

    private void handleMovementAction(int diff_x, int diff_y, MoveRules rule) {
        boolean newPosState = newPositionChecker(diff_x, diff_y, rule);

        if (newPosState) {
            pacmanCoords.setLocation(diff_x, diff_y);
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
                case GameLevel.SUPERDOT:
                    updateScore(SUPERDOT_BONUS);
                    // change game mode!
                    //resources.handleSoundEvent("ghosteaten");
                    setCellType(newPosition.x, newPosition.y, GameLevel.ROAD);
                    break;
            }

            if (!afterTeleport) {
                Pair<Boolean, Point2D<Integer>> teleport = isTeleportationPoint(newPosition);

                if (teleport.getKey().booleanValue()) {
                    Point2D<Integer> newPositionAfterTeleport = teleport.getValue();
                    Orientation newPacmanOrientation = detectOrientationByTeleportExit(newPositionAfterTeleport);
                    afterTeleport = true;
                    handleMovementAction(newPositionAfterTeleport.x * objectSize, newPositionAfterTeleport.y * objectSize,
                                         getRuleByOrientation(newPacmanOrientation));
                    changePacmanOrientation(newPacmanOrientation);
                    return;
                }
            }

            afterTeleport = false;
        }
    }

    private Orientation detectOrientationByTeleportExit(Point2D<Integer> newPositionAfterTeleport) {
        // get width and height of game area
        // compare with newPosition coords
        // and we have 4 cases - our orientation
        int x = newPositionAfterTeleport.x;
        int y = newPositionAfterTeleport.y;
        int fieldWidth = resources.getCurrentLevel().getWidth();
        int fieldHeight = resources.getCurrentLevel().getHeight();

        if (y == (fieldHeight - 1))
            return Orientation.UP;
        else if (x == 0)
            return Orientation.RIGHT;
        else if (x == (fieldWidth - 1))
            return Orientation.LEFT;
        else if (y == 0)
            return Orientation.DOWN;

        /* TODO: Handle this */
        System.out.println("ERRRRRORR!!!");
        return null;
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
            //resources.handleSoundEvent("gameover");
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
            default:
                return;
        }
    }

    public GameLevel getCurrentLevel() {
        return resources.getCurrentLevel();
    }
}