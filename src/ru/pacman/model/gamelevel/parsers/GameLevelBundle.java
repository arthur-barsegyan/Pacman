package ru.pacman.model.gamelevel.parsers;

import ru.pacman.model.Pair;
import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.Point2D;

import java.util.List;
import java.util.Map;

public class GameLevelBundle {
    public int width;
    public int height;
    public byte level[];
    public java.util.List<Point2D> superDotsCoordinates;
    public Map<String, DetailedPoint2D> heroCoordinates;
    public List<Pair<Point2D, Point2D>> teleportPointsCoordinates;
    public java.util.List<DetailedPoint2D> nonIntersectionCoordinates;
    public DetailedPoint2D ghostHotelEnter;
    public DetailedPoint2D ghostHotelExit;
}