package ru.pacman.model;

public class DetailedPoint2D {
    private double x;
    private double y;

    public DetailedPoint2D() {}

    public DetailedPoint2D(double _x, double _y) {
        x = _x;
        y = _y;
    }

    public void setLocation(double diff_x, double diff_y) {
        x = diff_x;
        y = diff_y;
    }

    public int getX() {
        return (int)Math.floor(x);
    }

    public int getY() {
        return (int)Math.floor(y);
    }

    public void setX(double diff_x) {
        x = diff_x;
    }

    public void setY(double diff_y) {
        y = diff_y;
    }

    /* TODO: Reoverride hashcode() method */
    public boolean equals(Object secondPoint) {
        if (!(secondPoint instanceof DetailedPoint2D)) {
            return false;
        }

        if (((DetailedPoint2D) secondPoint).getX() == x && ((DetailedPoint2D) secondPoint).getY() == y)
            return true;

        return false;
    }
}
