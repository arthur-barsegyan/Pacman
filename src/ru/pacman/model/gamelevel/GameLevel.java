package ru.pacman.model.gamelevel;

import ru.pacman.model.Point2D;
import ru.pacman.model.gamelevel.parsers.GameLevelBundle;
import ru.pacman.model.gamelevel.parsers.GameLevelParser;

import java.io.IOException;
import java.util.List;

public class GameLevel {
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

    public byte[] getLevelData() { return data.level; }
    public Point2D getDefaultCoords(String characterName) {
        Point2D<Integer> characterCoordinates = data.heroCoordinates.get(characterName);
        return characterCoordinates;
    }
    public List<Point2D<Integer>> getSpecialIntersectionsList() { return data.nonIntersectionCoordinates; }
}



