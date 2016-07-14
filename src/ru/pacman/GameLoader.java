package ru.pacman;

import ru.pacman.controller.GameController;
import ru.pacman.controller.PacmanGameController;
import ru.pacman.model.GameModel;
import ru.pacman.model.gamelevel.LevelFileFormatException;
import ru.pacman.ui.PacmanGameView;
import ru.pacman.ui.GraphicUI;
import ru.pacman.ui.PacmanGameView;

import java.io.IOException;

public class GameLoader {
    public static void main(String args[]) {
        try {
            GameModel model = new GameModel("resources/PacmanLevelList.txt");
            PacmanGameController controller = new GameController(model);
            PacmanGameView viewFrame = new GraphicUI(controller);
            controller.addView(viewFrame);
            controller.gameStart();
        } catch (LevelFileFormatException err) {
            System.out.println(err.getMessage());
        } catch (IOException err) {
            /* TODO: Make a normal error notation */
            System.out.println("Problems with opening file - ");
        }
    }
}
