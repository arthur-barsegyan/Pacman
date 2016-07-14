package ru.pacman.model;

public class Point2D<PointType> {
    public PointType x;
    public PointType y;

    public Point2D() {}
    public Point2D(PointType _x, PointType _y) {
        x = _x;
        y = _y;
    }

    public void setLocation(PointType diff_x, PointType diff_y) {
        x = diff_x;
        y = diff_y;
    }

    public boolean isEquals(Point2D<Integer> second) {
        if (second.x.equals(x) && second.y.equals(y))
            return true;

        return false;
    }
}