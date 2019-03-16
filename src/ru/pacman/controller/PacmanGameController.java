package ru.pacman.controller;

import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;
import ru.pacman.model.gamelevel.GameLevel;
import ru.pacman.ui.PacmanGameView;

public interface PacmanGameController {
    LevelData getLevelData();
    void addView(PacmanGameView view);
    void gameStart();
    int getScore();
    DetailedPoint2D getCharacterCoords(String characterName);
    GameModel.Orientation getPacmanOrientation();
}
