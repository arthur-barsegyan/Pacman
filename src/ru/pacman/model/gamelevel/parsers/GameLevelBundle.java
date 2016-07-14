package ru.pacman.model.gamelevel.parsers;

import java.awt.geom.Point2D;
import java.util.Map;

public class GameLevelBundle {
    public byte level[];
    public java.util.List<ru.pacman.model.Point2D<Integer>> superDotsCoordinates;
    public Map<String, ru.pacman.model.Point2D<Integer>> heroCoordinates;
    public java.util.List<ru.pacman.model.Point2D<Integer>> nonIntersectionCoordinates;
}