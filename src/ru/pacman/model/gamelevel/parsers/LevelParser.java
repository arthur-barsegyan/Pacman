package ru.pacman.model.gamelevel.parsers;

interface LevelParser {
    public abstract void parse(String levelData, GameLevelBundle levelBundle, int currentIndex);
}