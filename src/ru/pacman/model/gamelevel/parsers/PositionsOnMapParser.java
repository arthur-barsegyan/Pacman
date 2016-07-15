package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;

import java.util.*;

class PositionsOnMapParser implements LevelParser {
    String heroNameList[] = {"Pacman", "Blinky", "Pinky", "Inky", "Clyde"};
    Map<String, Point2D<Integer>> heroPosList = new HashMap<>();

    public void parse(String sectionData, GameLevelBundle levelBundle) {
        StringTokenizer posIterator = new StringTokenizer(sectionData, "(),=\n ", false);
        Point2D currentCoord = new Point2D(0, 0);
        String currentHero = null;
        int characterCounter = 0;
        boolean isFirstCoord = true;

        while (characterCounter < heroNameList.length && posIterator.hasMoreTokens()) {
            String currentToken = posIterator.nextToken();

            if (currentHero == null) {
                currentCoord = new Point2D<Integer>(0, 0);
                for (String heroTemplate : heroNameList) {
                    if (currentToken.equals(heroTemplate)) {
                        currentHero = currentToken;
                        break;
                    }
                }

                /* If currentHero is null => throw special exception */
                continue;
            }

            if (isFirstCoord) {
                currentCoord.x  = Integer.parseInt(currentToken) * 10;
                isFirstCoord = false;
            } else {
                currentCoord.y = Integer.parseInt(currentToken) * 10;
                heroPosList.put(currentHero, currentCoord);
                isFirstCoord = true;
                currentHero = null;
                characterCounter++;
            }
        }

        levelBundle.heroCoordinates = heroPosList;
    }
}
