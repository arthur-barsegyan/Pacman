package ru.pacman.controller;

import ru.pacman.model.DetailedPoint2D;
import ru.pacman.model.GameModel;
import ru.pacman.model.Point2D;
import ru.pacman.model.gamelevel.GameLevel;
import ru.pacman.ui.PacmanGameView;

import javax.swing.*;
import javax.tools.JavaCompiler;
import java.awt.event.*;
import java.util.ArrayList;

public class GameController implements PacmanGameController {
    GameModel model;
    ArrayList<PacmanGameView> views = new ArrayList<>();

    public GameController(GameModel _model) {
        model = _model;
    }

    public void gameStart() {
        model.gameStart();
        for (PacmanGameView view : views) {
            view.getKeyListener(new MoveTracker(model, view));
            Timer pacmanTimer = new Timer(100, (ActionEvent event) -> {
                    model.newMovementAction();
                    view.updateCoords();
            });

            Timer ghostTimer = new Timer(50, (ActionEvent event) -> {
                model.updateGhostsPosition();
                view.updateGhostsPosition();
            });

            while (!model.isLevelOver() && !model.isGameOver()) {
                pacmanTimer.start();
                ghostTimer.start();
            }

            view.gameOver();
        }
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

    public byte[] getLevelData() {
        return model.getLevelData();
    }

    public void addView(PacmanGameView _view) { views.add(_view); }

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
