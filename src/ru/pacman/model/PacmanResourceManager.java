package ru.pacman.model;

import ru.pacman.model.audiofx.AudioFXInitException;
import ru.pacman.model.audiofx.PacmanAudioFX;
import ru.pacman.model.gamelevel.GameLevel;
import ru.pacman.model.gamelevel.LevelErrorLoadingException;
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

    public PacmanResourceManager(String levelNameList) throws LevelFileFormatException {
        try {
            gameLevels = Files.readAllLines(Paths.get(levelNameList));
            gameFX = new PacmanAudioFX();
        } catch (IOException err) {
            throw new LevelFileFormatException("List of game levels can't be read");
        } catch (AudioFXInitException err) {
            System.out.println(err.getMessage());
        }
    }

    public void handleSoundEvent(String event) { gameFX.handleEvent(event); }

    public GameLevel loadNextLevel() throws LevelFileFormatException, LevelErrorLoadingException {
        /* If we have some levels yet */
        if (gameLevels.size() >= (levelNumber + 1)) {
            try {
                currentLevel = new GameLevel(gameLevels.get(levelNumber));
                levelNumber++;
            } catch (LevelErrorLoadingException err) {
                /* We support the state in its class in the correct form */
                currentLevel = null;
                throw err;
            }
        } else
            currentLevel = null;

        return currentLevel;
    }

    public void close() {
        gameFX.close();
    }
}
