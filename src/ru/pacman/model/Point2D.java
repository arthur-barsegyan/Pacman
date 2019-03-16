package ru.pacman.model;

public class Point2D {
    public int x;
    public int y;

    public Point2D() {}
    public Point2D(int _x, int _y) {
        x = _x;
        y = _y;
    }

    public void setLocation(int diff_x, int diff_y) {
        x = diff_x;
        y = diff_y;
    }

    /* TODO: Reoverride hashcode() method */
    public boolean equals(Object secondPoint) {
        if (!(secondPoint instanceof Point2D))
            return false;

        if (((Point2D) secondPoint).getX() == x && ((Point2D) secondPoint).getY() == y)
            return true;

        return false;
    }
}