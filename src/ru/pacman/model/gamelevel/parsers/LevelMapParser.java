package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.gamelevel.LevelFileFormatException;

class LevelMapParser implements LevelParser {
    private final int headerOffset = 5;
    private final int mapLength = 460 + headerOffset;

    public void parse(String levelData, GameLevelBundle levelBundle, int currentIndex) throws LevelFileFormatException {
        try {
            String temp = levelData.substring(currentIndex + headerOffset, currentIndex + mapLength);
            temp = temp.replaceAll("\n", "");
            levelBundle.level = temp.getBytes();
        } catch (IndexOutOfBoundsException err) {
            throw new LevelFileFormatException("Error reading level file! Map section in file is corrupted");
        }
    }
}