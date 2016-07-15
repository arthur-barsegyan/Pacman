package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.gamelevel.LevelFileFormatException;

class LevelMapParser implements LevelParser {
    /* TODO: Remove this */
    private final int mapLength = 460;

    public void parse(String sectionData, GameLevelBundle levelBundle) throws LevelFileFormatException {
        try {
            String temp = sectionData.substring(0, mapLength);
            char tempBytes[] = temp.toCharArray();

            for (int i = 0; i < tempBytes.length; i++) {
                if (tempBytes[i] == '\n') {
                    levelBundle.height++;
                } else {
                    levelBundle.width++;
                }
            }

            levelBundle.width /= levelBundle.height;
            temp = temp.replaceAll("\n", "");
            levelBundle.level = temp.getBytes();
        } catch (IndexOutOfBoundsException err) {
            throw new LevelFileFormatException("Error reading level file! Map section in file is corrupted");
        }
    }
}