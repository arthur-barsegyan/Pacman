package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.gamelevel.LevelFileFormatException;

interface LevelParser {
    public abstract void parse(String levelData, GameLevelBundle levelBundle, int currentIndex) throws LevelFileFormatException;
}