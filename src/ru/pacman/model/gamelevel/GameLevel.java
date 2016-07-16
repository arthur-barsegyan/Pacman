package ru.pacman.model.gamelevel;

import javafx.util.Pair;
import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.Point2D;
import ru.pacman.model.gamelevel.parsers.GameLevelBundle;
import ru.pacman.model.gamelevel.parsers.GameLevelParser;

import java.io.IOException;
import java.util.List;

/* TODO: Make interface for identifiy width and height of current map */
public class GameLevel {
    /* TODO: Remove hardcode constants and read needful data from level file */
    public static int objectsOnXAxis = 19;
    public static int objectsOnYAxis = 23;
    public final static char SIMPLEDOT = '0';
    public final static char WALL = '1';
    public final static char SUPERDOT = '2';
    public final static char ROAD = '4';

    private String gameLevelName;
    private GameLevelBundle data;

    public GameLevel(String levelName) throws LevelFileFormatException {
        data = new GameLevelBundle();
        gameLevelName = levelName;

        try {
            GameLevelParser levelReader = new GameLevelParser(levelName);
            levelReader.parseLevelFile(data);
        } catch (IOException err) {

        } catch (LevelFileFormatException err) {
            throw err;
        }
    }

    public int getWidth() { return data.width; }
    public int getHeight() { return data.height; }
    public byte[] getLevelData() { return data.level; }
    public DetailedPoint2D getDefaultCoords(String characterName) { return data.heroCoordinates.get(characterName); }
    public List<DetailedPoint2D> getSpecialIntersectionsList() { return data.nonIntersectionCoordinates; }
    public List<Pair<Point2D,Point2D>> getTeleportationPoints() {
        return data.teleportPointsCoordinates;
    }
}



