package ru.pacman;

import ru.pacman.controller.GameController;
import ru.pacman.controller.PacmanGameController;
import ru.pacman.model.GameModel;
import ru.pacman.ui.PacmanGameView;
import ru.pacman.ui.GraphicUI;
import ru.pacman.ui.PacmanGameView;

public class GameLoader {
    public static void main(String args[]) {
        try {
            GameModel model = new GameModel("resources/PacmanLevelList.txt");
            PacmanGameController controller = new GameController(model);
            PacmanGameView viewFrame = new GraphicUI(controller);
            controller.addView(viewFrame);
            controller.gameStart();
        } catch (Throwable err) {
            System.out.println(err.getMessage());
        }
    }
}
