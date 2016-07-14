package ru.pacman.model.ai;

import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;

import java.util.*;

public abstract class GhostAI {
    public abstract void move();
    public abstract Point2D<Integer> getCurrentCoordinates();

    abstract Point2D<Integer> getTargetTile();
    abstract Point2D<Integer> getPreviousPosition();
    abstract void setPreviousPosition();
    abstract void setCurrentPosition(int x, int y);
    abstract GameModel getGameModel();

    abstract public boolean isBlockingOnAxisX();
    abstract public boolean isBlockingOnAxisY();
    abstract public void setBlockingOnAxisX(boolean state);
    abstract public void setBlockingOnAxisY(boolean state);

    boolean moveAlgo() {
        ArrayList<Point2D<Integer>> pathsList = getGameModel().getPathFromPosition(this);

        // Removing our previous path from pathList
        for (Point2D<Integer> currentPath : pathsList) {
            if (currentPath.isEquals(getPreviousPosition())) {
                pathsList.remove(currentPath);
                break;
            }
        }

        // TODO: Ghost hotel check
        // while ghost contains in hotel, our target is a hotel exit

        if ((pathsList.size() + 1) < 3) {
            // Default case: choose current direction
            for (Point2D<Integer> currentPatch : pathsList) {
                if (!isLastMove(currentPatch)) {
                    checkAxisBlocking(currentPatch);
                    setPreviousPosition();
                    setCurrentPosition(currentPatch.x, currentPatch.y);
                    return true;
                }
            }
        }

        /* TODO: Repair this string */
        ArrayList<Comparator<Point2D<Integer>>> cmp = getCmp();
        final int leftRigtCmps[] = {1, 3};

        // yellow intersections check!1
        if (getGameModel().isSpecialIntersection(getCurrentCoordinates())) {
            System.out.println("Handling special intersection!");
            // find another side (not upwards)
            for (int currentCmp : leftRigtCmps) {
                Optional<Point2D<Integer>> currentDirection = pathsList.stream().min(cmp.get(currentCmp));

                if (currentDirection.isPresent() && !isLastMove(currentDirection.get())) {
                    checkAxisBlocking(currentDirection.get());
                    setPreviousPosition();
                    setCurrentPosition(currentDirection.get().x, currentDirection.get().y);
                    return true;
                }
            }

            // unexpected situation - error
            System.out.println("Error in handling special intersection");
        }

        Point2D<Integer> target = getTargetTile();
        double pathLength[] = new double[pathsList.size()];
        boolean equalsMins = false;
        double minLength = Integer.MAX_VALUE;
        int minLengthPos = -1;

        for (int i = 0; i < pathsList.size(); i++) {
            pathLength[i] = Math.sqrt((Math.pow(pathsList.get(i).x - target.x, 2) +
                    Math.pow(pathsList.get(i).y - target.y, 2)));
        }

        for (int i = 0; i < pathLength.length; i++) {
            if (pathLength[i] < minLength) {
                minLength = pathLength[i];
                minLengthPos = i;
                /* TODO: Case when ghost contains in the deadlock */
            } else if (pathLength[i] == minLength) {
                equalsMins = true;
            }
        }

        if (minLengthPos == -1)
            return false;

        if (!equalsMins) {
            checkAxisBlocking(pathsList.get(minLengthPos));
            setPreviousPosition();
            setCurrentPosition(pathsList.get(minLengthPos).x, pathsList.get(minLengthPos).y);
            return true;
        }

        /* Now we should detect cell location */
        for(Comparator<Point2D<Integer>> currentCmp : cmp) {
            Optional<Point2D<Integer>> currentDirection = pathsList.stream().min(currentCmp);

            if (minEqualsChecker(currentDirection.get(), pathsList, currentCmp))
                continue;

            if (!isLastMove(currentDirection.get())) {
                checkAxisBlocking(currentDirection.get());
                setPreviousPosition();
                setCurrentPosition(currentDirection.get().x, currentDirection.get().y);
                return true;
            }
        }

        return false;
    }

    private void checkAxisBlocking(Point2D<Integer> position) {
        if (getCurrentCoordinates().x != position.x)
            setBlockingOnAxisX(true);
        else if (getCurrentCoordinates().y != position.y)
            setBlockingOnAxisY(true);

        if ((position.x % getGameModel().getObjectSize()) == 0)
            setBlockingOnAxisX(false);

        if ((position.y % getGameModel().getObjectSize()) == 0)
            setBlockingOnAxisY(false);
    }

    private boolean isLastMove(Point2D<Integer> currentMove) {
        if (currentMove.x != getPreviousPosition().x || currentMove.y != getPreviousPosition().y) {
            return false;
        }

        return true;
    }

    boolean minEqualsChecker(Point2D<Integer> currentDirection, ArrayList<Point2D<Integer>> pathsList, Comparator<Point2D<Integer>> cmp) {
        pathsList.remove(currentDirection);
        Optional<Point2D<Integer>> secondMin = pathsList.stream().min(cmp);
        pathsList.add(currentDirection);

        if (secondMin.get().isEquals(currentDirection))
            return true;

        return false;
    }

    /* TODO: Change functions on lamda expressions */
    ArrayList<Comparator<Point2D<Integer>>> getCmp()  {
        ArrayList<Comparator<Point2D<Integer>>> cmp = new ArrayList<>();
        cmp.add(new Comparator<Point2D<Integer>>() {
            @Override
            public int compare(Point2D<Integer> o1, Point2D<Integer> o2) {
                if (o1.y < o2.y) {
                    return -1;
                } else if (o2.y < o1.y)
                    return 1;
                return 0;
            }
        });

        cmp.add(new Comparator<Point2D<Integer>>() {
            @Override
            public int compare(Point2D<Integer> o1, Point2D<Integer> o2) {
                if (o1.x < o2.x) {
                    return -1;
                } else if (o2.x < o1.x)
                    return 1;
                return 0;
            }
        });

        cmp.add(new Comparator<Point2D<Integer>>() {
            @Override
            public int compare(Point2D<Integer> o1, Point2D<Integer> o2) {
                if (o1.y > o2.y) {
                    return -1;
                } else if (o2.y < o1.y)
                    return 1;
                return 0;
            }
        });

        cmp.add((Point2D<Integer> o1, Point2D<Integer> o2) -> {
            if (o1.x > o2.x)
                return -1;
            else if (o2.x > o1.x)
                return 1;

            return 0;
        });

        return cmp;
    }
}
