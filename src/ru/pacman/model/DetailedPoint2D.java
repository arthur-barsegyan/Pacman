package ru.pacman.model;

public class DetailedPoint2D {
    public int x;
    public int y;

    public DetailedPoint2D() {}
    public DetailedPoint2D(int _x, int _y) {
        x = _x;
        y = _y;
    }

    public void setLocation(int diff_x, int diff_y) {
        x = diff_x;
        y = diff_y;
    }

    /*public Point2D getPoint2D() {

    }*/

    /* TODO: Reoverride hashcode() method */
    public boolean equals(Object secondPoint) {
        if (!(secondPoint instanceof DetailedPoint2D)) {
            System.out.println("ESDFSDSFS");
            return false;
        }

        if (((DetailedPoint2D) secondPoint).x == x && ((DetailedPoint2D) secondPoint).y == y)
            return true;

        return false;
    }
}
