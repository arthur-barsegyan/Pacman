package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;

import java.util.*;

class PositionsOnMapParser implements LevelParser {
    String heroNameList[] = {"Pacman", "Blinky", "Pinky", "Inky", "Clyde"};
    Map<String, DetailedPoint2D> heroPosList = new HashMap<>();

    public void parse(String sectionData, GameLevelBundle levelBundle) {
        StringTokenizer posIterator = new StringTokenizer(sectionData, "(),=\n ", false);
        DetailedPoint2D currentCoord = new DetailedPoint2D();
        String currentHero = null;
        int characterCounter = 0;
        boolean isFirstCoord = true;

        while (characterCounter < heroNameList.length && posIterator.hasMoreTokens()) {
            String currentToken = posIterator.nextToken();

            if (currentHero == null) {
                currentCoord = new DetailedPoint2D();
                for (String heroTemplate : heroNameList) {
                    if (currentToken.equals(heroTemplate)) {
                        currentHero = currentToken;
                        break;
                    }
                }

                /* If currentHero is null => throw special exception */
                continue;
            }

            /* TODO: Remove hardcode constants */
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
