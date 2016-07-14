package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class NonIntersectionParser implements LevelParser {
    final int offsetToStartsData = 17; // Is it a best practise?
    public void parse(String levelData, GameLevelBundle levelBundle, int currentIndex) {
        /* Tokenizer created with special flag, which specified current token */
        StringTokenizer dotsIterator = new StringTokenizer(levelData.substring(currentIndex + offsetToStartsData), "(), \n\r", false);
        List<Point2D<Integer>> tempCoordinates = new ArrayList<>();
        Point2D<Integer> currentPos = new Point2D<Integer>();
        boolean currentCoord = true;

        try {
            while (dotsIterator.hasMoreTokens()) {
                String currentToken = dotsIterator.nextToken();

                if (currentToken.equals("#"))
                    break;

                if (currentCoord)
                    //TODO: Create interface for getting this
                    currentPos.x = Integer.parseInt(currentToken) * 10;
                else {
                    currentPos.y = Integer.parseInt(currentToken) * 10;
                    tempCoordinates.add(currentPos);
                    currentPos = new Point2D<Integer>();
                }

                currentCoord = !currentCoord;
            }
        } catch (Throwable err) {
            throw new LevelFileFormatException("NonIntersection position data is corrupted!");
        }

        if (!currentCoord) throw new LevelFileFormatException("NonIntersection position data is corrupted!");
        levelBundle.nonIntersectionCoordinates = tempCoordinates;
    }

}
