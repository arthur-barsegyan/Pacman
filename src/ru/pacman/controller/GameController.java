package ru.pacman.controller;

import ru.pacman.controller.LevelData;
import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;
import ru.pacman.model.gamelevel.GameLevel;
import ru.pacman.ui.PacmanGameView;

import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.tools.JavaCompiler;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class GameController implements PacmanGameController, Observer {
    private GameModel model;
    private PacmanGameView view;
    private Thread controllerThread;

    public GameController(GameModel _model) {
        model = _model;
    }

    public void gameStart() {
        model.gameStart(this);
        view.getKeyListener(new MoveTracker(model, view));

        controllerThread = new Thread(() -> {
            while (!controllerThread.isInterrupted()) {
                try {
                    model.newMovementAction();
                    model.updateGhostsPosition();

                    if (controllerThread.isInterrupted()) {
                        System.exit(0);
                    }

                    SwingUtilities.invokeLater(() -> {
                        view.updateCoords();
                        view.updateGhostsPosition();
                    });

                    Thread.currentThread().sleep(100);
                } catch (InterruptedException e) {
                    // System.out.println("Game was ended -> kill controller thread...");
                    System.exit(0);
                }
            }
        });

        controllerThread.start();
    }

    public void update(Observable o, Object arg) {
        String event = (String) arg;
        try {
            switch (event) {
                case "gameover": {
                    model.gameEnd();
                    SwingUtilities.invokeAndWait(() -> {
                        view.gameOver();
                    });
                    Thread.currentThread().interrupt();
                    break;
                }
                default:
                    return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {}
    }

    @Override
    public DetailedPoint2D getCharacterCoords(String characterName) {
        return model.getCharacterCoords(characterName);
    }

    @Override
    public GameModel.Orientation getPacmanOrientation() {
        return model.getPacmanOrientation();
    }

    public void getScore() {

    }

    @Override
    public LevelData getLevelData() {
        return new LevelData(model.getLevelData(), model.getWidth(), model.getHeight());
    }

    public void addView(PacmanGameView _view) { view = _view; }

    /* This tracker will speak to model about Pacman actions,
       and model will analyse this data. */
    public class MoveTracker extends KeyAdapter {
        private GameModel model;
        private PacmanGameView view;

        MoveTracker(GameModel _model, PacmanGameView _view) {
            model = _model;
            view = _view;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case (KeyEvent.VK_UP):
                    model.changePacmanOrientation(GameModel.Orientation.UP);
                    break;
                case (KeyEvent.VK_LEFT):
                    model.changePacmanOrientation(GameModel.Orientation.LEFT);
                    break;
                case (KeyEvent.VK_RIGHT):
                    model.changePacmanOrientation(GameModel.Orientation.RIGHT);
                    break;
                case (KeyEvent.VK_DOWN):
                    model.changePacmanOrientation(GameModel.Orientation.DOWN);
                    break;
            }
        }
    }
}
