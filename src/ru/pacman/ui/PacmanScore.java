package ru.pacman.ui;

import ru.pacman.controller.PacmanGameController;

import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;

public class PacmanScore extends JComponent {
	private PacmanGameController controller;
	private Font textFont;
	private int x;
	private int y;
	private int width;
	private int height;

	public PacmanScore(PacmanGameController controller, int x, int y, int width, int height) {
		this.controller = controller;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		try {
			textFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Joystix.TTF")).deriveFont(0, 24);
		} catch (FontFormatException | IOException e) {
			System.out.println("Problem with font");
			textFont = Font.getFont("Impact").deriveFont(0, 24);
		} 
		setSize(getPreferredSize());
		setVisible(true);
	}

	@Override
    public void paintComponent(Graphics g) {
    	g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.WHITE);
        g.setFont(textFont);
        g.drawString("SCORE:", 10, 36);
        g.drawString("" + controller.getScore(), 10, 66);
    }

    public void updateScore() {
    	repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
}