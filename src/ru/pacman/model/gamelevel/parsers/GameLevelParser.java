package ru.pacman.model.gamelevel.parsers;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/* Level File
    2 - SuperDots (Power Pellet) change color for ghosts and they stands undangerous
    1 - Wall or barrier
    0 - Pac - Dots (or just dots)
*/
public class GameLevelParser {
    private String gameLevelName;
    private ClassLoader gameLevelLoader = new ClassLoader();
    private Properties propertiesReader= new Properties();

    public GameLevelParser() {}
    public GameLevelParser(String levelName) throws IOException { // PROBLEM HERE
        gameLevelName = levelName;
        /* Try with resources ?! */
        InputStream levelParserProperties = gameLevelLoader.getResourceAsStream("PacmanLevelParserProperties.txt");

        try {
            propertiesReader.load(levelParserProperties);
        } catch (IOException e) {
            throw e;
        } finally {
            if (levelParserProperties != null)
                levelParserProperties.close();
        }
    }

    public static byte[] inputStreamToByteArray(InputStream data) throws IOException {
        if (data == null) {
            //log.warn("InputStream with source code is empty");
            throw new RuntimeException();
        }

        ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
        byte tempBuffer[] = new byte[4096];
        int count = 0;

        try {
            while ((count = data.read(tempBuffer)) != -1)
                tempStream.write(tempBuffer, 0, count);
        } catch (IOException err) {
            //log.error("I/O Error! " + err.getMessage());
            throw err;
        } finally {
            tempStream.close();
        }

        return tempStream.toByteArray();
    }

    public void parseLevelFile(GameLevelBundle levelBundle) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        /* Can I use this classloader for opening two different files?! */
        InputStream fileStream = gameLevelLoader.getResourceAsStream(gameLevelName);

        if (fileStream == null) {
            // log
            // RuntimeException - is it a good practise?
            throw new RuntimeException();
        }

        try {
            byte tempArray[] = inputStreamToByteArray(fileStream);
            String gameLevelData = new String (tempArray);
            java.util.Set<String> keysList = propertiesReader.stringPropertyNames();
            int currentIndex = 0;

            /* Searching special sections in level file. For each section we have special handler */
            for (String currentKey : keysList) {
                if ((currentIndex = gameLevelData.indexOf(currentKey)) != -1) {
                    String currentParserName = propertiesReader.getProperty(currentKey);

                    try {
                        Class newClass = Class.forName(currentParserName);
                        LevelParser newOperator = (LevelParser) newClass.newInstance();
                        newOperator.parse(gameLevelData, levelBundle, currentIndex);
                    } catch (LevelFileFormatException err) {
                        // log
                        throw err;
                    } catch (ClassNotFoundException err) {
                        // log (invalid level file)
                        throw err;
                    } catch (InstantiationException | IllegalAccessException err) {
                        //log.error("Error with instantiation or other stuff " + currentToken);
                        throw err;
                    } catch (Throwable err) {
                        System.out.println("Parser internal error!");
                    }
                }
            }
        } catch (IOException err) {

        }
    }
}