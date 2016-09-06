package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.gamelevel.LevelFileFormatException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/* Level File
    2 - SuperDots (Power Pellet) change color for ghosts and they stands undangerous
    1 - Wall or barrier
    0 - Pac - Dots (or just dots) */
public class GameLevelParser {
    private String gameLevelName;
    private java.lang.ClassLoader gameLevelLoader = getClass().getClassLoader();
    private Properties propertiesReader= new Properties();
    private List<String> sectionsHandled = new ArrayList<>();
    private static String[] importantSections = {"map", "start_pos", "ghost_hotel"};

    public GameLevelParser(String levelName) throws IOException {
        gameLevelName = levelName;

        try (InputStream levelParserProperties = gameLevelLoader.getResourceAsStream("PacmanLevelParserProperties.txt")) {
            propertiesReader.load(levelParserProperties);
        } catch (IOException e) {
            throw e;
        }
    }

    public static byte[] inputStreamToByteArray(InputStream data) throws IOException {
        if (data == null)
            throw new NullPointerException("InputStream is empty");

        ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
        byte tempBuffer[] = new byte[4096];
        int count = 0;

        try {
            while ((count = data.read(tempBuffer)) != -1)
                tempStream.write(tempBuffer, 0, count);
        } catch (IOException err) {
            throw err;
        } finally {
            tempStream.close();
        }

        return tempStream.toByteArray();
    }

    public void parseLevelFile(GameLevelBundle levelBundle) throws LevelFileFormatException {
        try (InputStream fileStream = gameLevelLoader.getResourceAsStream(gameLevelName)) {
            byte tempArray[] = inputStreamToByteArray(fileStream);
            String gameLevelData = new String(tempArray);
            java.util.Set<String> sectionsList = propertiesReader.stringPropertyNames();
            int currentIndex = 0;

            /* Searching special sections in level file. For each section we have special handler */
            for (String currentKey : sectionsList) {
                if ((currentIndex = gameLevelData.indexOf(currentKey)) != -1) {
                    String currentParserName = propertiesReader.getProperty(currentKey);

                    try {
                        Class newClass = Class.forName(currentParserName);
                        LevelParser newOperator = (LevelParser) newClass.newInstance();
                        String sectionData = gameLevelData.substring(currentIndex + currentKey.length() + 2);
                        newOperator.parse(sectionData, levelBundle);
                        sectionsHandled.add(currentKey);
                    } catch (LevelFileFormatException err) {
                        /* We don't check every unlucky case because we can check important sections
                           after all parsing job (We can parse sections very fast) */
                        System.out.println(err.getMessage());
                    } catch (ClassNotFoundException err) {
                        System.out.println("[WARNING] Invalid header: " + currentParserName);
                    } catch (InstantiationException | IllegalAccessException err) {
                        System.out.println("Parser internal error!");
                    }
                }
            }

            importantSectionsChecker();
        } catch (IOException err) {

        } catch (LevelFileFormatException err) {
            throw err;
        }

    }

    private void importantSectionsChecker() throws LevelFileFormatException {
        for (String currentSection: importantSections) {
            if (!sectionsHandled.contains(currentSection))
                throw new LevelFileFormatException("File with map data was corrupted - closing...");
        }
    }
}