package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class SuperDotsParser implements LevelParser {
    final int offsetToStartsData = 13; // Is it a best practise?
    public void parse(String levelData, GameLevelBundle levelBundle, int currentIndex) {
        /* Tokenizer created with special flag, which specified current token */
        StringTokenizer dotsIterator = new StringTokenizer(levelData.substring(currentIndex + offsetToStartsData), "(), \n\r", false);
        List<Point2D<Integer>> tempCoordinates = new ArrayList<>();
        Point2D<Integer> currentDot = new Point2D<Integer>();
        boolean currentCoord = true;

        try {
            while (dotsIterator.hasMoreTokens()) {
                String currentToken = dotsIterator.nextToken();

                if (currentToken.equals("#"))
                    break;

                if (currentCoord)
                    currentDot.x = Integer.parseInt(currentToken);
                else {
                    currentDot.y = Integer.parseInt(currentToken);
                    tempCoordinates.add(currentDot);
                    currentDot = new Point2D<Integer>();
                }

                currentCoord = !currentCoord;
            }
        } catch (Throwable err) {
            throw new LevelFileFormatException("SuperDots position data is corrupted!");
        }

        /* TODO: Repair this */
        //if (!currentCoord) throw new LevelFileFormatException("SuperDots position data is corrupted!");
        levelBundle.superDotsCoordinates = tempCoordinates;
    }
}