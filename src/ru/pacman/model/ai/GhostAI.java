package ru.pacman.model.ai;

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

    protected abstract void setHotelOutState(boolean state);
    protected abstract boolean isInsideTheHotel();

    /* TODO: Refactor this code! */
    boolean moveAlgo() {
        ArrayList<DetailedPoint2D> pathsList = getGameModel().getPathFromPosition(this);
         /* TODO: Repair this string */
        ArrayList<Comparator<DetailedPoint2D>> cmp = getCmp();
        if (getCurrentCoordinates().equals(getGameModel().getGhostHotelExitCoordinates()))
            setHotelOutState(true);

        if (!isInsideTheHotel()) {
            // Teleportation point check
            Point2D currentPosition = getGameModel().fromDetailedCoordinatesToSimple(getCurrentCoordinates());
            // Maybe I should convert teleport coords to detailed when i parse this data?!
            GameModel.TeleportationStatus teleport = getGameModel().isTeleportationPoint(currentPosition);

            if (teleport.getTeleportationState() && !afterTeleport()) {
                DetailedPoint2D newPositionAfterTeleport = teleport.getTeleportationCoords();
                setPreviousPosition();
                setCurrentPosition(newPositionAfterTeleport.getX(), newPositionAfterTeleport.getY());
                usingTeleport(true);
                return true;
            } else
                usingTeleport(false);

            // Removing our previous path and ghost hotel enter path from pathList
            /* TODO: Read more about ArrayList structure */
            int pathCounter = pathsList.size();
            for (Iterator<DetailedPoint2D> it = pathsList.iterator(); it.hasNext(); ) {
                DetailedPoint2D currentPath = it.next();
                if (currentPath.equals(getPreviousPosition()) || getGameModel().checkGhostHotelZone(currentPath))
                    it.remove();
            }

            // If this case isn't intersection
            if (pathCounter < 3) {
                // Default case: choose current direction
                for (DetailedPoint2D currentPatch : pathsList) {
                    //if (!isLastMove(currentPatch)) { // todo: useless???1
                    checkAxisBlocking(currentPatch);
                    setPreviousPosition();
                    setCurrentPosition(currentPatch.getX(), currentPatch.getY());
                    return true;
                    //}
                }
            }

            final int leftRigtCmps[] = {1, 3};

            /* TODO: It's doesn't work! Ghost cannot moving upwards! */
            if (getGameModel().isSpecialIntersection(getCurrentCoordinates())) {
                // find another side (not upwards)
                for (int currentCmp : leftRigtCmps) {
                    Optional<DetailedPoint2D> currentDirection = pathsList.stream().min(cmp.get(currentCmp));

                    if (currentDirection.isPresent() && !isLastMove(currentDirection.get())) {
                        checkAxisBlocking(currentDirection.get());
                        setPreviousPosition();
                        setCurrentPosition(currentDirection.get().getX(), currentDirection.get().getY());
                        return true;
                    }
                }
            }
        }

        DetailedPoint2D target;
        if (isInsideTheHotel())
            target = getGameModel().getGhostHotelExitCoordinates();
        else
            target = getTargetTile();

        double pathLength[] = new double[pathsList.size()];
        boolean equalsMins = false;
        double minLength = Integer.MAX_VALUE;
        int minLengthPos = -1;

        for (int i = 0; i < pathsList.size(); i++) {
            pathLength[i] = Math.sqrt((Math.pow(pathsList.get(i).getX() - target.getX(), 2) +
                    Math.pow(pathsList.get(i).getY() - target.getY(), 2)));
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
            setCurrentPosition(pathsList.get(minLengthPos).getX(), pathsList.get(minLengthPos).getY());
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
                setCurrentPosition(currentDirection.get().getX(), currentDirection.get().getY());
                return true;
            }
        }

        return false;
    }

    private void checkAxisBlocking(DetailedPoint2D position) {
        if (getCurrentCoordinates().getX() != position.getX())
            setBlockingOnAxisX(true);
        else if (getCurrentCoordinates().getY() != position.getY())
            setBlockingOnAxisY(true);

        if ((position.getX() % getGameModel().getObjectSize()) == 0)
            setBlockingOnAxisX(false);

        if ((position.getY() % getGameModel().getObjectSize()) == 0)
            setBlockingOnAxisY(false);
    }

    private boolean isLastMove(DetailedPoint2D currentMove) {
        if (currentMove.getX() != getPreviousPosition().getX() || currentMove.getY() != getPreviousPosition().getY()) {
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

    ArrayList<Comparator<DetailedPoint2D>> getCmp()  {
        ArrayList<Comparator<DetailedPoint2D>> cmp = new ArrayList<>();
        cmp.add((DetailedPoint2D o1, DetailedPoint2D o2) -> {
                if (o1.getY() < o2.getY()) {
                    return -1;
                } else if (o2.getY() < o1.getY())
                    return 1;
                return 0;
        });

        cmp.add((DetailedPoint2D o1, DetailedPoint2D o2) -> {
                if (o1.getX() < o2.getX()) {
                    return -1;
                } else if (o2.getX() < o1.getX())
                    return 1;
                return 0;
        });

        cmp.add((DetailedPoint2D o1, DetailedPoint2D o2) -> {
                if (o1.getY() > o2.getY()) {
                    return -1;
                } else if (o2.getY() < o1.getY())
                    return 1;
                return 0;
        });

        cmp.add((DetailedPoint2D o1, DetailedPoint2D o2) -> {
            if (o1.getX() > o2.getX())
                return -1;
            else if (o2.getX() > o1.getX())
                return 1;

            return 0;
        });

        return cmp;
    }
}
