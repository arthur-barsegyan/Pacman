package ru.pacman.model.ai;

import javafx.util.Pair;
import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;

import java.util.*;

public abstract class GhostAI {
    public abstract void move();
    public abstract DetailedPoint2D getCurrentCoordinates();

    abstract DetailedPoint2D getTargetTile();
    abstract DetailedPoint2D getPreviousPosition();
    abstract void setPreviousPosition();
    abstract void setCurrentPosition(int x, int y);
    abstract GameModel getGameModel();

    abstract public boolean isBlockingOnAxisX();
    abstract public boolean isBlockingOnAxisY();
    abstract public void setBlockingOnAxisX(boolean state);
    abstract public void setBlockingOnAxisY(boolean state);
    protected abstract void usingTeleport(boolean state);
    protected abstract boolean afterTeleport();

    boolean moveAlgo() {
        // teleportation point check
        /* TODO: Refactor this code! */
        /* TODO: Make special interface for translate from firts type coordinates in second type */
        /* TODO: Rewrite this method! It looks very ugly! */
        Point2D pos = new Point2D(getCurrentCoordinates().x, getCurrentCoordinates().y);
        pos.x /= 10;
        pos.y /= 10;
        Pair<Boolean, Point2D> teleport = getGameModel().isTeleportationPoint(pos);

        if (teleport.getKey().booleanValue() && !afterTeleport()) {
            Point2D newPositionAfterTeleport = teleport.getValue();
            setPreviousPosition();
            setCurrentPosition(newPositionAfterTeleport.x * 10, newPositionAfterTeleport.y * 10);
            usingTeleport(true);
            return true;
        } else
            usingTeleport(false);

        ArrayList<DetailedPoint2D> pathsList = getGameModel().getPathFromPosition(this);

        // Removing our previous path from pathList
        for (DetailedPoint2D currentPath : pathsList) {
            if (currentPath.equals(getPreviousPosition())) {
                pathsList.remove(currentPath);
                break;
            }
        }

        // TODO: Ghost hotel check
        // while ghost contains in hotel, our target is a hotel exit

        if ((pathsList.size() + 1) < 3) {
            // Default case: choose current direction
            for (DetailedPoint2D currentPatch : pathsList) {
                if (!isLastMove(currentPatch)) {
                    checkAxisBlocking(currentPatch);
                    setPreviousPosition();
                    setCurrentPosition(currentPatch.x, currentPatch.y);
                    return true;
                }
            }
        }

        /* TODO: Repair this string */
        ArrayList<Comparator<DetailedPoint2D>> cmp = getCmp();
        final int leftRigtCmps[] = {1, 3};

        // yellow intersections check!1
        if (getGameModel().isSpecialIntersection(getCurrentCoordinates())) {
            // find another side (not upwards)
            for (int currentCmp : leftRigtCmps) {
                Optional<DetailedPoint2D> currentDirection = pathsList.stream().min(cmp.get(currentCmp));

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

        DetailedPoint2D target = getTargetTile();
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
        for(Comparator<DetailedPoint2D> currentCmp : cmp) {
            Optional<DetailedPoint2D> currentDirection = pathsList.stream().min(currentCmp);

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

    private void checkAxisBlocking(DetailedPoint2D position) {
        if (getCurrentCoordinates().x != position.x)
            setBlockingOnAxisX(true);
        else if (getCurrentCoordinates().y != position.y)
            setBlockingOnAxisY(true);

        if ((position.x % getGameModel().getObjectSize()) == 0)
            setBlockingOnAxisX(false);

        if ((position.y % getGameModel().getObjectSize()) == 0)
            setBlockingOnAxisY(false);
    }

    private boolean isLastMove(DetailedPoint2D currentMove) {
        if (currentMove.x != getPreviousPosition().x || currentMove.y != getPreviousPosition().y) {
            return false;
        }

        return true;
    }

    boolean minEqualsChecker(DetailedPoint2D currentDirection, ArrayList<DetailedPoint2D> pathsList, Comparator<DetailedPoint2D> cmp) {
        pathsList.remove(currentDirection);
        Optional<DetailedPoint2D> secondMin = pathsList.stream().min(cmp);
        pathsList.add(currentDirection);

        if (secondMin.get().equals(currentDirection))
            return true;

        return false;
    }

    /* TODO: Change functions on lamda expressions */
    ArrayList<Comparator<DetailedPoint2D>> getCmp()  {
        ArrayList<Comparator<DetailedPoint2D>> cmp = new ArrayList<>();
        cmp.add(new Comparator<DetailedPoint2D>() {
            @Override
            public int compare(DetailedPoint2D o1, DetailedPoint2D o2) {
                if (o1.y < o2.y) {
                    return -1;
                } else if (o2.y < o1.y)
                    return 1;
                return 0;
            }
        });

        cmp.add(new Comparator<DetailedPoint2D>() {
            @Override
            public int compare(DetailedPoint2D o1, DetailedPoint2D o2) {
                if (o1.x < o2.x) {
                    return -1;
                } else if (o2.x < o1.x)
                    return 1;
                return 0;
            }
        });

        cmp.add(new Comparator<DetailedPoint2D>() {
            @Override
            public int compare(DetailedPoint2D o1, DetailedPoint2D o2) {
                if (o1.y > o2.y) {
                    return -1;
                } else if (o2.y < o1.y)
                    return 1;
                return 0;
            }
        });

        cmp.add((DetailedPoint2D o1, DetailedPoint2D o2) -> {
            if (o1.x > o2.x)
                return -1;
            else if (o2.x > o1.x)
                return 1;

            return 0;
        });

        return cmp;
    }
}
