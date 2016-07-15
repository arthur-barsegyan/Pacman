package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.gamelevel.LevelFileFormatException;

interface LevelParser {
    public abstract void parse(String sectionData, GameLevelBundle levelBundle) throws LevelFileFormatException;
}