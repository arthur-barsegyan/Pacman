package ru.pacman.ui;

import ru.pacman.model.gamelevel.GameLevel;

import java.awt.event.KeyListener;

public interface PacmanGameView  {
    void getKeyListener(KeyListener handler);
    void updateCoords();
    void updateGameScore();
    void updateGhostsPosition();
    void gameOver();
}
