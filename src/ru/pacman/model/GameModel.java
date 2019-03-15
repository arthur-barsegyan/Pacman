package ru.pacman.model;

import ru.pacman.model.Pair;
import ru.pacman.model.ai.*;
import ru.pacman.model.gamelevel.GameLevel;
import ru.pacman.model.gamelevel.LevelErrorLoadingException;
import ru.pacman.model.gamelevel.LevelFileFormatException;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Observable;
import java.awt.Rectangle;

import static ru.pacman.model.GameModel.MoveRules.LeftUpRule;
import static ru.pacman.model.GameModel.MoveRules.RightDownRule;

public class GameModel extends Observable {
    private final String invalidLevelMessage = "Game can't loading level.\n" +
                                               "Please check your \"PacmanLevelList.txt\" file and try again!";
    private PacmanResourceManager resources;
    private GameLevel currentLevel;
    private byte currentLevelData[];

    private final String ghostsNames[] = {"Blinky", "Pinky", "Inky", "Clyde"};
    private ArrayList<GhostAI> ghosts;
    private boolean ghostAttack = false;

    private boolean levelOver = false;
    private boolean gameOver = false;
    private boolean winStatus = false;
    private int gameScore = 0;

    /* TODO: IMPORTANT! Change this speed model */
    private final int pacmanSpeed = 5;
    private Orientation pacmanOrientation = Orientation.RIGHT;
    private DetailedPoint2D pacmanCoords;
    private boolean blockAxisX = false;
    private boolean blockAxisY = false;

    private int dotsCounter = 0;
    private int dotsMaxCounter;

    private final int SIMPLEDOT_BONUS = 10;
    private final int SUPERDOT_BONUS = 100;
    private final int ENEMY_BONUS = 200;
    private final int objectSize = 10;
    private boolean afterTeleport = false;

    public static class TeleportationStatus {
        private boolean teleportationState = false;
        private DetailedPoint2D teleportationCoords;

        TeleportationStatus(DetailedPoint2D coords) {
            if (coords != null) {
                teleportationCoords = coords;
                teleportationState = true;
            }
        }

        public boolean getTeleportationState() { return teleportationState; }
        public DetailedPoint2D getTeleportationCoords() { return teleportationCoords; }
    }

    public enum Orientation {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    enum Axis {
        X,
        Y
    }

    enum MoveRules {
        RightDownRule,
        LeftUpRule
    }

    /* This constructor receive file which contains list with
       game levels file names. This method loading first level
       from this list and preparing for the game. */
    public GameModel(String _levelNameList) throws LevelErrorLoadingException {
        try {
            resources = new PacmanResourceManager(_levelNameList);
            currentLevel = resources.loadNextLevel();
            if (currentLevel == null)
                throw new LevelErrorLoadingException(invalidLevelMessage);

            currentLevelData = currentLevel.getLevelData();
            pacmanCoords = getCharacterCoords("Pacman");

            ghosts = new ArrayList<>();
            ghosts.add(new Blinky(this));
            ghosts.add(new Pinky(this));
            ghosts.add(new Clyde(this));
            ghosts.add(new Inky(this));

            dotsMaxCounter = getDotsCount();
        } catch (LevelFileFormatException err) {
            /* User doesn't know about details */
            throw new LevelErrorLoadingException(invalidLevelMessage);
        }
    }

    /* We create special method for starting game. After thar we independent about
       View and Controller */
    public void gameStart(Observer observer) {
        addObserver(observer);
        resources.handleSoundEvent("gamestart");
        //resources.handleSoundEvent("chasemode");
    }

    public void newMovementAction() {
        int x = pacmanCoords.x;
        int y = pacmanCoords.y;

        switch (pacmanOrientation) {
            case UP:
                handleMovementAction(new DetailedPoint2D(x, y - pacmanSpeed), MoveRules.LeftUpRule);
                break;
            case LEFT:
                handleMovementAction(new DetailedPoint2D(x - pacmanSpeed, y), MoveRules.LeftUpRule);
                break;
            case RIGHT:
                handleMovementAction(new DetailedPoint2D(x + pacmanSpeed, y), RightDownRule);
                break;
            case DOWN:
                handleMovementAction(new DetailedPoint2D(x, y + pacmanSpeed), RightDownRule);
                break;
            default:
                return;
        }
    }

    /* TODO: Maybe we should made this method like static method? */
    public int getObjectSize() { return objectSize; }
    public int getDotsCounter() {
        return dotsCounter;
    }

    public DetailedPoint2D getCharacterCoords(String characterName) {
        return currentLevel.getDefaultCoords(characterName);
    }

    public void updateGhostsPosition() {
        for (GhostAI currentGhost : ghosts) {
            currentGhost.move();

            if (isGameOver()) {
                return;
            }
        }
    }

    public DetailedPoint2D getGhostHotelExitCoordinates() {
        return currentLevel.getGhostHotelExitCoords();
    }

    /* This method should receive special class - Ghost, which should contains of special methods for
       getting current coordinates and other special attributes */
    public ArrayList<DetailedPoint2D> getPathFromPosition(GhostAI currentGhost) {
        DetailedPoint2D currentPosition = currentGhost.getCurrentCoordinates();
        int xcells[] = {currentPosition.x + 1, currentPosition.x - 1, currentPosition.x, currentPosition.x};
        int ycells[] = {currentPosition.y, currentPosition.y, currentPosition.y + 1, currentPosition.y - 1};
        MoveRules rules[] = {MoveRules.RightDownRule, MoveRules.LeftUpRule, MoveRules.RightDownRule, MoveRules.LeftUpRule};
        Orientation side[] = {Orientation.RIGHT, Orientation.LEFT, Orientation.DOWN, Orientation.UP};
        Axis currentAxis[] = {Axis.X, Axis.X, Axis.Y, Axis.Y};
        ArrayList<DetailedPoint2D> paths = new ArrayList<>();

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

            DetailedPoint2D detailedPathPoint = new DetailedPoint2D(xcells[i], ycells[i]);
            if (!isBorderSafety(detailedPathPoint))
                continue;

            Point2D cellPosition = getNewPosition(detailedPathPoint, rules[i]);
            char cellType = getCellType(cellPosition);
            if (cellType != GameLevel.WALL)
                paths.add(detailedPathPoint);
        }

        return paths;
    }

    public boolean isSpecialIntersection(DetailedPoint2D currentIntersection) {
        List<DetailedPoint2D> specialIntersections = currentLevel.getSpecialIntersectionsList();

        for (DetailedPoint2D nonIntersection : specialIntersections) {
            if (nonIntersection.equals(currentIntersection))
                return true;
        }

        return false;
    }


    public boolean checkGhostHotelZone(DetailedPoint2D currentPosition) {
        int x = currentLevel.getGhostHotelEnterCoords().x;
        int y = currentLevel.getGhostHotelEnterCoords().y;

        if (Math.abs(currentPosition.x - x) < objectSize && Math.abs(currentPosition.y - y) < objectSize)
            return true;

        return false;
    }

    public void changePacmanOrientation(Orientation orientation) {
        pacmanOrientation = orientation;
    }

    public Orientation getPacmanOrientation() {
        return pacmanOrientation;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getMaxDotsCount() {
        return dotsMaxCounter;
    }

    public int getDotsCount() {
        int counter = 0;
        for (int i = 0; i < currentLevelData.length; i++) {
            if ((char)currentLevelData[i] == GameLevel.SIMPLEDOT) {
                counter++;
            }
        }

        return counter;
    }

    public boolean isLevelOver() {
        if (dotsCounter == dotsMaxCounter)
            levelOver = true;

        return levelOver;
    }

    public void checkGhostsAttack() {
        int delta = objectSize / 2;
        Rectangle pacmanRect = new Rectangle(pacmanCoords.x - delta, pacmanCoords.y - delta,
                                              objectSize, objectSize);
        for (String currentGhost : ghostsNames) {
            DetailedPoint2D point = getCharacterCoords(currentGhost);
            Rectangle ghostRect = new Rectangle(point.x - delta, point.y - delta,
                                                objectSize, objectSize);
            if (ghostRect.intersects(pacmanRect)) {
                ghostAttack = true;
                gameOver();
                return;
            }
        }
    }

    public TeleportationStatus isTeleportationPoint(Point2D newCoordinates) {
        List<Pair<Point2D, Point2D>> teleportationPoints = currentLevel.getTeleportationPoints();

        try {
            for (Pair<Point2D, Point2D> currentTeleportationPoint : teleportationPoints) {
                if (currentTeleportationPoint.getKey().equals(newCoordinates))
                    return new TeleportationStatus(fromSimpleCoordinatesToDetailed(currentTeleportationPoint.getValue()));
            }
        } catch (NullPointerException err) {
            /* If in this level we don't have a teleportation points */
        }

        return new TeleportationStatus(null);
    }


    /* Load new level from file. If ResourceManager doesn't knew about next levels
     * then game is over and user has won. */
    public boolean loadNextLevel() throws LevelFileFormatException, LevelErrorLoadingException {
        try {
            currentLevel = resources.loadNextLevel();
            if (currentLevel == null) {
                winStatus = true;
                gameOver = true;
                return false;
            }
        /* If level exist but invalid - it's lose */
        } catch (LevelFileFormatException err) {
            throw err;
        } catch (LevelErrorLoadingException err) {
            throw err;
        }

        return true;
    }

    public int getWidth() { return currentLevel.getWidth(); }
    public int getHeight() { return currentLevel. getHeight(); }

    /* TODO: Recheck this method; Maybe I should use objectSize instead getWidth/getHeight? */
    public Point2D fromDetailedCoordinatesToSimple(DetailedPoint2D detailedPoint2D) {
        return new Point2D(detailedPoint2D.x / objectSize, detailedPoint2D.y / objectSize);
    }

    public DetailedPoint2D fromSimpleCoordinatesToDetailed(Point2D point) {
        return new DetailedPoint2D(point.x * objectSize, point.y * objectSize);
    }

    private Point2D getNewPosition(DetailedPoint2D newCoordinates, MoveRules rule) {
        int integerDiff_x = newCoordinates.x / objectSize;
        int integerDiff_y = newCoordinates.y / objectSize;
        int fractionDiff_x = newCoordinates.x % objectSize;
        int fractionDiff_y = newCoordinates.y % objectSize;
        Point2D newPosition = new Point2D(fractionDiff_x, fractionDiff_y);

        /* Cell border checker */
        switch (rule) {
            case RightDownRule: {
                if (fractionDiff_x > 0)
                    newPosition.x = integerDiff_x + 1;
                else
                    newPosition.x = integerDiff_x;

                if (fractionDiff_y > 0)
                    newPosition.y = integerDiff_y + 1;
                else
                    newPosition.y = integerDiff_y;

                break;
            }

            case LeftUpRule: {
                newPosition.x = integerDiff_x;
                newPosition.y = integerDiff_y;
                break;
            }
        }

        return newPosition;
    }

    public byte[] getLevelData() {
        return currentLevel.getLevelData();
    }

    private char getCellType(Point2D point) {
        GameLevel currentLvl = currentLevel;
        byte level[] = currentLvl.getLevelData();
        return (char)level[point.y * currentLevel.getWidth() + point.x];
    }

    private void setCellType(Point2D point, char newValue) {
        GameLevel currentLvl = currentLevel;
        byte level[] = currentLvl.getLevelData();
        level[point.y * currentLevel.getWidth() + point.x] = (byte)newValue;
    }

    /* TODO: Rewrite this method! */
    private boolean isBorderSafety(DetailedPoint2D point) {
        if ((point.x < (currentLevel.getWidth() * 10) && point.x >= 0) && (point.y < (currentLevel.getHeight() * 10) && point.y >= 0))
            return true;

        return false;
    }

    private boolean newPositionChecker(DetailedPoint2D newCoordinates, MoveRules rule) {
        if (isBorderSafety(newCoordinates)) {
            Point2D newPosition = getNewPosition(newCoordinates, rule);

            if (newCoordinates.x % objectSize > 0)
                blockAxisX = true;
            else
                blockAxisX = false;

            if (newCoordinates.y % objectSize > 0)
                blockAxisY = true;
            else
                blockAxisY = false;

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

            char currentLocation = getCellType(newPosition);
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

    private void handleMovementAction(DetailedPoint2D newCoordinates, MoveRules rule) {
        boolean newPosState = newPositionChecker(newCoordinates, rule);

        if (newPosState) {
            pacmanCoords.setLocation(newCoordinates.x, newCoordinates.y);
            Point2D newPosition = getNewPosition(newCoordinates, rule);
            char newCellType = getCellType(newPosition);

            switch (newCellType) {
                case GameLevel.ROAD:
                    break;
                case GameLevel.SIMPLEDOT:
                    dotsCounter++;
                    updateScore(SIMPLEDOT_BONUS);
                    resources.handleSoundEvent("simpledot");
                    setCellType(newPosition, GameLevel.ROAD);
                    break;
                case GameLevel.SUPERDOT:
                    updateScore(SUPERDOT_BONUS);
                    // change game mode!
                    //resources.handleSoundEvent("ghosteaten");
                    setCellType(newPosition, GameLevel.ROAD);
                    break;
            }

            if (!afterTeleport) {
                TeleportationStatus teleport = isTeleportationPoint(newPosition);

                if (teleport.getTeleportationState()) {
                    DetailedPoint2D newPositionAfterTeleport = teleport.getTeleportationCoords();
                    Orientation newPacmanOrientation = detectOrientationByTeleportExit(fromDetailedCoordinatesToSimple(newPositionAfterTeleport));
                    afterTeleport = true;
                    handleMovementAction(newPositionAfterTeleport,
                                         getRuleByOrientation(newPacmanOrientation));
                    changePacmanOrientation(newPacmanOrientation);
                    return;
                }
            }

            afterTeleport = false;
        }
    }

    private DetailedPoint2D getDetailedPoint2D(Point2D point) {
        return new DetailedPoint2D(point.x * objectSize, point.y * objectSize);
    }

    private Orientation detectOrientationByTeleportExit(Point2D newPositionAfterTeleport) {
        int x = newPositionAfterTeleport.x;
        int y = newPositionAfterTeleport.y;
        int fieldWidth = currentLevel.getWidth();
        int fieldHeight = currentLevel.getHeight();

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

    private void gameOver() {
        setChanged();
        if (ghostAttack) {
            gameOver = true;
            notifyObservers("gameover");
        } else {
            notifyObservers("levelpass");
            System.out.println("You winner!");
        }
    }

    public void gameEnd() {
        resources.handleSoundEvent("gameover");
        resources.close();
    }

    private void updateScore(int newAchieve) {
        gameScore += newAchieve;
    }
}