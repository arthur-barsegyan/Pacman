package ru.pacman.model.gamelevel.parsers;

import javafx.util.Pair;
import ru.pacman.model.Point2D;

import java.util.List;
import java.util.Map;

public class GameLevelBundle {
    public byte level[];
    public java.util.List<Point2D<Integer>> superDotsCoordinates;
    public Map<String, Point2D<Integer>> heroCoordinates;
    public List<Pair<Point2D<Integer>, Point2D<Integer>>> teleportPointsCoordinates;
    public java.util.List<Point2D<Integer>> nonIntersectionCoordinates;
    public int width;
    public int height;
}