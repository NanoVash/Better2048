package com.nanovash.better2048;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Point;
import java.util.Random;

public class Tile extends JLabel {

	long actualNumber = 0;
	boolean isMoving = false;
	boolean shouldConnect = false;
	boolean alreadyConnected = false;
	Point heading;

	public Tile(long shownNumber) {
		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		setShownNumber(shownNumber);
		setActualNumber(shownNumber);
		if(Better2048.discoBox.isSelected())
			setRandomBackground();
		else
			setBackground(TileCanvas.tileColors.get(getShownNumber()));
	}

	public void setShownNumber(long shownNumber) {
		setText(Long.toString(shownNumber));
	}

	public long getShownNumber() {
		return Long.parseLong(getText());
	}

	public void setActualNumber(long actualNumber) {
		this.actualNumber = actualNumber;
	}

	public long getActualNumber() {
		return actualNumber;
	}

	public void setRandomBackground() {
		float base = 1 / getShownNumber();
		Random r = new Random();
		setBackground(new Color(Math.abs(base - r.nextFloat()), Math.abs(base - r.nextFloat()), Math.abs(base - r.nextFloat())));
	}

	public void destroy() {
		for(int i = 0; i < TileCanvas.canvasLength; i++)
			for(int j = 0; j < TileCanvas.canvasLength; j++)
				if(TileCanvas.verticLocs.get(i).get(j) == this) {
					Better2048.game.remove(this);
					Better2048.game.repaint();
					Better2048.game.revalidate();
					TileCanvas.verticLocs.get(i).set(j, null);
					TileCanvas.horizLocs.get(j).set(i, null);
				}
	}

	public void destroyWhileMoving() {
		Better2048.game.remove(this);
		Better2048.game.repaint();
		Better2048.game.revalidate();
	}
}