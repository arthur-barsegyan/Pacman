package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.gamelevel.LevelFileFormatException;

import java.util.StringTokenizer;

public class GhostHotelParser implements LevelParser {
    private String exceptionMessage = "Ghost hotel position data is corrupted!";
    @Override
    public void parse(String sectionData, GameLevelBundle levelBundle) throws LevelFileFormatException {
        StringTokenizer tokenIterator = new StringTokenizer(sectionData, "(),= \n", false);

        try {
            while (tokenIterator.hasMoreTokens()) {
                String currentToken = tokenIterator.nextToken();

                if (currentToken.equals("inside")) {
                    if (tokenIterator.countTokens() < 2)
                        throw new LevelFileFormatException(exceptionMessage);

                    /* TODO: Remove this hardcode constants */
                    int x = Integer.parseInt(tokenIterator.nextToken()) * 10;
                    int y = Integer.parseInt(tokenIterator.nextToken()) * 10;
                    levelBundle.ghostHotelEnter = new DetailedPoint2D(x, y);
                    continue;
                }

                if (currentToken.equals("outside")) {
                    if (tokenIterator.countTokens() < 2)
                        throw new LevelFileFormatException(exceptionMessage);

                    /* TODO: Remove this hardcode constants */
                    int x = Integer.parseInt(tokenIterator.nextToken()) * 10;
                    int y = Integer.parseInt(tokenIterator.nextToken()) * 10;
                    levelBundle.ghostHotelExit = new DetailedPoint2D(x, y);
                    return;
                }
            }

            throw new LevelFileFormatException(exceptionMessage);
        } catch (Throwable err) {
            throw new LevelFileFormatException(exceptionMessage);
        }
    }
}
