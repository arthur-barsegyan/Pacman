package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.Point2D;
import ru.pacman.model.gamelevel.LevelFileFormatException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class NonIntersectionParser implements LevelParser  {
    public void parse(String sectionData, GameLevelBundle levelBundle) throws LevelFileFormatException {
        /* Tokenizer created with special flag, which specified current token */
        StringTokenizer dotsIterator = new StringTokenizer(sectionData, "(), \n", false);
        List<DetailedPoint2D> tempCoordinates = new ArrayList<>();
        DetailedPoint2D currentPos = new DetailedPoint2D();
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
                    //TODO: Create interface for getting this
                    currentPos.getX() = Integer.parseInt(currentToken) * 10;
                else {
                    currentPos.getY() = Integer.parseInt(currentToken) * 10;
                    tempCoordinates.add(currentPos);
                    currentPos = new DetailedPoint2D();
                }

                currentCoord = !currentCoord;
            }
        } catch (Throwable err) {
            throw new LevelFileFormatException("NonIntersection position data is corrupted!");
        }

        if (!currentCoord || !endOfData) throw new LevelFileFormatException("NonIntersection position data is corrupted!");
        levelBundle.nonIntersectionCoordinates = tempCoordinates;
    }

}
