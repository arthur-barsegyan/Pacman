package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.Point2D;
import ru.pacman.model.gamelevel.LevelFileFormatException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class SuperDotsParser implements LevelParser {
    public void parse(String sectionData, GameLevelBundle levelBundle) throws LevelFileFormatException {
        /* Tokenizer created with special flag, which specified current token */
        StringTokenizer dotsIterator = new StringTokenizer(sectionData, "(), \n", false);
        List<Point2D<Integer>> tempCoordinates = new ArrayList<>();
        Point2D<Integer> currentDot = new Point2D<>();
        boolean endOfData = false;
        boolean currentCoord = true;

        try {
            while (dotsIterator.hasMoreTokens()) {
                String currentToken = dotsIterator.nextToken();

                if (currentToken.equals("#")) {
                    endOfData = true;
                    break;
                }

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

        if (!currentCoord || !endOfData) throw new LevelFileFormatException("SuperDots position data is corrupted!");
        levelBundle.superDotsCoordinates = tempCoordinates;
    }
}