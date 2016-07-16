package ru.pacman.model;

import javafx.util.Pair;
import ru.pacman.model.gamelevel.GameLevel;
import ru.pacman.model.gamelevel.LevelFileFormatException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class PacmanResourceManager {
    private int levelNumber = 0;
    private List<String> gameLevels;
    private GameLevel currentLevel;
    private PacmanAudioFX gameFX;

    public PacmanResourceManager(String levelNameList) {
        try {
            gameLevels = Files.readAllLines(Paths.get(levelNameList));
            gameFX = new PacmanAudioFX();
        } catch (IOException err) {

        } catch (Throwable err) {
            /* If AudioFX system isn't be a initialization */
        }
    }

    public boolean loadNextLevel() throws LevelFileFormatException {
        if (gameLevels.size() >= (levelNumber + 1)) {
            currentLevel = new GameLevel(gameLevels.get(levelNumber));
            levelNumber++;
            return true;
        } else
            return false;
    }

    public void handleSoundEvent(String event) { gameFX.handleEvent(event); }
    GameLevel getCurrentLevel() { return currentLevel; }
    List<DetailedPoint2D> getSpecialIntersectionsList() { return currentLevel.getSpecialIntersectionsList(); }
    public List<Pair<Point2D,Point2D>> getTeleportationPoints() {
        return currentLevel.getTeleportationPoints();
    }
}
