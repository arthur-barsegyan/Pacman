package ru.pacman.ui;

import ru.pacman.controller.LevelData;
import ru.pacman.controller.PacmanGameController;
import ru.pacman.model.GameModel;
import ru.pacman.model.gamelevel.GameLevel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/* TODO: Create special interface for Field and View */
public class PacmanField extends JComponent {
    private PacmanGameController controller;
    private ArrayList<PacmanLevelObject> objectList;
    private ArrayList<Ghost> ghostsList;
    public final static int objectSize = 30;
    private Pacman pacman;
    private LevelData levelData;

    PacmanField(PacmanGameController _controller) {
        controller = _controller;
        levelData = controller.getLevelData();
        fieldInit();
        //setDoubleBuffered(true);
    }

    private void fieldInit() {
        objectList = new ArrayList<>();
        ghostsList = new ArrayList<>();

        pacman = new Pacman(controller.getCharacterCoords("Pacman"));
        pacman.updateOrientation(controller.getPacmanOrientation());

        ghostsList.add(new Blinky(controller.getCharacterCoords("Blinky")));
        ghostsList.add(new Pinky(controller.getCharacterCoords("Pinky")));
        ghostsList.add(new Clyde(controller.getCharacterCoords("Clyde")));
        ghostsList.add(new Inky(controller.getCharacterCoords("Inky")));
        add(pacman);

        byte[] level = levelData.level;
        for (int i = 0; i < level.length; i++) {
            int x = (i % levelData.width) * objectSize;
            int y = (i / levelData.width) * objectSize;

            if ((char)level[i] == GameLevel.WALL)
                objectList.add(new PacmanWall(x, y));
            else if((char)level[i] == GameLevel.ROAD)
                objectList.add(new Road(x, y));
            else if ((char)level[i] == GameLevel.SIMPLEDOT)
                objectList.add(new Dot(x, y, false));
            else if ((char)level[i] == GameLevel.SUPERDOT)
                objectList.add(new Dot(x, y, true));
        }
    }

    private void fieldUpdate() {
        for (int i = 0; i < objectList.size(); i++) {
            PacmanLevelObject currentObject = objectList.get(i);

            if (currentObject instanceof Dot) {
                if ((char)levelData.level[i] == GameLevel.ROAD) {
                    ((Dot) currentObject).deactivate();
                }
            }
        }
    }

    public void updateCoords() {
        pacman.updateCoords(controller.getCharacterCoords("Pacman"));
        pacman.updateOrientation(controller.getPacmanOrientation());
        fieldUpdate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((levelData.width + 1) * objectSize, (levelData.height + 1) * objectSize);
    }

    @Override
    public void paintComponent(Graphics g) {
        for (int currentPos = 0; currentPos < objectList.size(); currentPos++) {
            objectList.get(currentPos).paint(g);
        }

        pacman.paint(g);
        for (Ghost currentGhost : ghostsList) {
            currentGhost.paint(g);
        }
    }

    public void updateGhostsPosition() {
        for (Ghost ghost : ghostsList) {
            ghost.updatePosition(controller.getCharacterCoords(ghost.getGhostName()));
            repaint();
        }
    }
}
