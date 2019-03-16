package ru.pacman.ui;

import ru.pacman.controller.LevelData;
import ru.pacman.controller.PacmanGameController;
import ru.pacman.model.gamelevel.GameLevel;
import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.*;

/* Pacman GUI consists of game field, score table */
public class GraphicUI extends JFrame implements PacmanGameView {
    private PacmanGameController controller;
    private PacmanField gameField;
    private PacmanScore gameScore;
    private int windowSizeX;
    private int windowSizeY;

    /* TODO: Turning on double buffering */
    public GraphicUI(PacmanGameController _controller) {
        super("Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        controller = _controller;
        LevelData level = controller.getLevelData();
        /* TODO: Read about this bug (Why I should adding useless object on every dimension?) */
        windowSizeX = (level.width + 1) * PacmanField.objectSize;
        windowSizeY = (level.height + 1) * PacmanField.objectSize;

        gameField = new PacmanField(controller);
        gameScore = new PacmanScore(controller, 
                                    0, 
                                    0,
                                    (int)(windowSizeX * 0.25),
                                    windowSizeY);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(gameField, "West");
        panel.add(gameScore, "East");

        windowSizeX *= 1.25; // Game score area
        add(panel);

        setSize(windowSizeX, windowSizeY);
        // Sets the location of the window relative to center of the screen
        setLocationRelativeTo(null); 
        setResizable(false);
        setVisible(true);
    }

    public void getKeyListener(KeyListener handler) {
        addKeyListener(handler);
    }

    // @Override
    // public Dimension getMaximumSize() {
    //     return new Dimension(windowSizeX, windowSizeY);
    // }

    public void updateCoords() {
        gameField.updateCoords();
    }

    public void updateGhostsPosition() {
        gameField.updateGhostsPosition();
    }

    public void gameOver() {
        System.out.println("Game Over");
    }

    public void updateGameScore() {
        gameScore.updateScore();
    }
}
