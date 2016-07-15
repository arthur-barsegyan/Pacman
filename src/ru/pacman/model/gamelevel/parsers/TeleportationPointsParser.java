package ru.pacman.model.gamelevel.parsers;

import javafx.util.Pair;
import ru.pacman.model.Point2D;
import ru.pacman.model.gamelevel.LevelFileFormatException;

import java.util.*;

/* In current realisation of this game, we can have more than one teleportation point and also this points
   can teleport to different others teleports on map */
public class TeleportationPointsParser implements LevelParser {
    private final int teleportEntrances = 2;
    private final int teleportEntrance = 0;
    private final int teleportExit = 1;
    private String exceptionMessage = "TeleportationPoints position data is corrupted!";

    public void parse(String sectionData, GameLevelBundle levelBundle) throws LevelFileFormatException {
        /* Tokenizer created with special flag, which specified current token */
        StringTokenizer teleportIterator = new StringTokenizer(sectionData, "(), \n", false);
        /* We use a Pair because we can very easily search teleport exit for every teleport entrance */
        List<Pair<Point2D<Integer>, Point2D<Integer>>> tempCoordinates = new ArrayList<>();

        Point2D<Integer> currentEntrance = new Point2D<>();
        Point2D<Integer> currentExit = new Point2D<>();
        boolean endOfData = false;
        boolean firstCoord = true;
        boolean endOfPoint = false;

        try {
            /* TODO: Repair this algo. Read more about parsers in Java */
            while (teleportIterator.hasMoreTokens()) {
                for (int i = 0; i < teleportEntrances && !endOfData; i++) {
                    endOfPoint = false;
                    while (teleportIterator.hasMoreTokens() && !endOfPoint) { // ERROR HERE
                        String currentToken = teleportIterator.nextToken();

                        if (currentToken.equals("#")) {
                            endOfData = true;
                            break;
                        }

                        if (i == teleportEntrance) {
                            if (firstCoord)
                                currentEntrance.x = Integer.parseInt(currentToken);
                            else {
                                currentEntrance.y = Integer.parseInt(currentToken);
                                endOfPoint = true;
                            }
                        } else if (i == teleportExit) {
                            if (firstCoord)
                                currentExit.x = Integer.parseInt(currentToken);
                            else {
                                currentExit.y = Integer.parseInt(currentToken);
                                tempCoordinates.add(new Pair<>(currentEntrance, currentExit));
                                endOfPoint = true;
                                currentEntrance = new Point2D<>();
                                currentExit = new Point2D<>();
                            }
                        }

                        firstCoord = !firstCoord;
                    }
                }

                if (endOfData)
                    break;

            }
        } catch (Throwable err) {
            throw new LevelFileFormatException(exceptionMessage);
        }

        if (!firstCoord || !endOfData) throw new LevelFileFormatException(exceptionMessage);
        levelBundle.teleportPointsCoordinates = tempCoordinates;
    }
}
