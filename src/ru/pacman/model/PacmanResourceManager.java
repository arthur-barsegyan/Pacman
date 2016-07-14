package ru.pacman.model;

import ru.pacman.model.gamelevel.GameLevel;
import ru.pacman.model.gamelevel.LevelFileFormatException;

import java.io.IOException;
import java.util.List;

class PacmanResourceManager {
    private GameLevel currentLevel;
    private PacmanAudioFX gameFX;

    public PacmanResourceManager() {
        try {
            gameFX = new PacmanAudioFX();
        } catch (IOException err) {
            /* Передавать строку в лог (исключение) с информацией об ошибке*/
        } catch (Throwable err) {
            /* If AudioFX system isn't be a initialization */
        }
    }

    public void loadLevel(String levelName) throws LevelFileFormatException { currentLevel = new GameLevel(levelName); }
    public void handleSoundEvent(String event) { gameFX.handleEvent(event); }
    GameLevel getCurrentLevel() { return currentLevel; }
    List<Point2D<Integer>> getSpecialIntersectionsList() { return currentLevel.getSpecialIntersectionsList(); }
}
