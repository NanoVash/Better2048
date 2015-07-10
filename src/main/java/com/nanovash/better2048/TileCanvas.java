package com.github.thesupermariobro.better2048;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class TileCanvas extends JPanel implements ComponentListener, MouseListener, KeyListener {

	static int canvasLength = 4;
	static Integer movingTilesCount = 0;
	static boolean isShowingWin = false;
	static boolean alreadyWon = false;
	static boolean isGameOver = false;
	static JLabel winLabel = new JLabel();
	static JLabel lossLabel = new JLabel();
	static List<List<Tile>> verticLocs = new ArrayList<>();
	static List<List<Tile>> horizLocs = new ArrayList<>();
	static SpringLayout layout = new SpringLayout();
	static Dimension lastSize;
	static double verticSpeed;
	static double horizSpeed;
	
	static HashMap<Long, Color> tileColors = new HashMap<Long, Color>() {{
		put(2L, Color.decode("#D1ECFF")); //beginning of light blue colors
		put(4L, Color.decode("#B2E0FF"));
		put(8L, Color.decode("#66C2FF"));
		put(16L, Color.decode("#4DB8FF"));
		put(32L, Color.decode("#33ADFF"));
		put(64L, Color.decode("#19A3FF"));
		put(128L, Color.decode("#94FF94")); //beginning of light green color to darker green
		put(256L, Color.decode("#66FF66"));
        put(512L, Color.decode("#00CC00"));
		put(1024L, Color.decode("#00FFCC"));
		put(2048L, Color.decode("#00CCA3"));
	}};
	
	public TileCanvas() {
		for(int i = 0; i < canvasLength; i++) {
			List<Tile> temp = new ArrayList<>();
			for(int j = 0; j < canvasLength; j++)
				temp.add(null);
			verticLocs.add(temp);
			horizLocs.add(temp);
		}
		setLayout(layout);
		addComponentListener(this);
		addMouseListener(this);
		addKeyListener(this);
		setBackground(Color.LIGHT_GRAY);
		setFocusable(true);
	}
	
	public static void createNewTile() {
		Random r = new Random();
		/*A list of points is used just to keep track of both TileCanvas.verticLocs and TileCanvas.horizLocs
		  values without having to use a custom Pair class due to the fact a Point can keep track of two values*/
		List<Point> locs = new ArrayList<>();
		for(int i = 0; i < TileCanvas.canvasLength; i++)
			for(int j = 0; j < TileCanvas.canvasLength; j++)
				if(TileCanvas.verticLocs.get(i).get(j) == null)
					locs.add(new Point(i, j));
		if(!locs.isEmpty()) {
			Tile t = new Tile((r.nextInt(10) < 9) ? 2 : 4);
			int location = r.nextInt(locs.size());
			Better2048.game.positionTile(t, locs.get(location).x, locs.get(location).y);
			if(Long.parseLong(Better2048.highestTile.getText()) < t.getShownNumber())
				Better2048.highestTile.setText(Long.toString(t.getShownNumber()));
		}
	}
	
	public void positionTile(Tile t, int row, int position) {
		if(getPreferredSize().equals(new Dimension(0, 0)))
			setPreferredSize(new Dimension(Better2048.frame.getSize().width - 164, Better2048.frame.getSize().height - 36));
		verticLocs.get(row).set(position, t);
		horizLocs.get(position).set(row, t);
		add(t);
		t.setPreferredSize(new Dimension(getPreferredSize().width / canvasLength, getPreferredSize().height / canvasLength));
		layout.putConstraint(SpringLayout.WEST, t, (getPreferredSize().width / canvasLength) * row , SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, t, (getPreferredSize().height / canvasLength) * position, SpringLayout.NORTH, this);
		t.setFont(new Font("Tile Font", Font.PLAIN, calcFont(t)));
		if(!lossLabel.getText().equals("Game over! Click the Restart Game button to restart")) {
			lossLabel = new JLabel("Game over! Click the Restart Game button to restart", SwingConstants.CENTER);
			registerLabel(lossLabel, "Loss Font", new Color(200, 100, 100, 123));
		}
		if(!winLabel.getText().equals("You win! Click on the canvas to keep playing")) {
			winLabel = new JLabel("You win! Click on the canvas to keep playing", SwingConstants.CENTER);
			registerLabel(winLabel, "Win Font", new Color(100, 100, 200, 123));
		}
	}
	
	private void registerLabel(JLabel label, String font, Color color) {
		label.setFont(new Font(font, Font.BOLD, 20));
		label.setForeground(Color.WHITE);
		label.setBackground(color);
		label.setPreferredSize(getPreferredSize());
		label.setVisible(false);
		label.setOpaque(true);
		add(label);
		layout.putConstraint(SpringLayout.NORTH, label, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, this);
		 ();
		revalidate();
	}

	private int calcFont(Tile t) {
		return 70 - (5 * t.getText().length()) <= 0 ? 5 : 70 - (5 * t.getText().length());
	}
	
	public void checkGameOver() {
		isGameOver = true;
		vertic_loop:
		for(int i = 0; i < canvasLength; i++)
			for(int j = 0; j < canvasLength; j++)
				if(j != 0) {
					if(verticLocs.get(i).get(j) != null && verticLocs.get(i).get(j - 1) != null)
						if(verticLocs.get(i).get(j).getShownNumber() == verticLocs.get(i).get(j - 1).getShownNumber()) {
							isGameOver = false;
							break vertic_loop;
						}
					if(verticLocs.get(i).get(j) == null || verticLocs.get(i).get(j - 1) == null) {
						isGameOver = false;
						break vertic_loop;
					}
				}
		horiz_loop:
		for(int i = 0; i < canvasLength; i++)
			for(int j = 0; j < canvasLength; j++)
				if(j != 0) {
					if(horizLocs.get(i).get(j) != null && horizLocs.get(i).get(j - 1) != null)
						if(horizLocs.get(i).get(j).getShownNumber() == horizLocs.get(i).get(j - 1).getShownNumber()) {
							isGameOver = false;
							break horiz_loop;
						}
					if(horizLocs.get(i).get(j) == null || horizLocs.get(i).get(j - 1) == null) {
						isGameOver = false;
						break horiz_loop;
					}
				}
		if(isGameOver)
			lossLabel.setVisible(true);
	}
	
	public void move(final Direction d) {
		for(int i = d.getArg1(); d.firstLoopContinue(i); i = d.firstLoopUpdate(i))
			for(int j = d.getArg1(); d.firstLoopContinue(j); j = d.firstLoopUpdate(j))
				if(j != d.getArg1() && d.mainList.get(i).get(j) != null) {
					final Tile t = d.mainList.get(i).get(j);
					for(int k = d.secondLoopUpdate(j); d.secondLoopContinue(k); k = d.secondLoopUpdate(k)) {
						if(k == d.getArg1()) { 
							if(d.mainList.get(i).get(k) == null || d.mainList.get(i).get(k).getActualNumber() == t.getActualNumber() && !d.mainList.get(i).get(k).alreadyConnected) {
								t.heading = new Point(i, k);
								break;
							}
							else {
								t.heading = new Point(i, d.firstLoopUpdate(k));
								break;
							}
						}
						else if(d.mainList.get(i).get(k) == null) {}
						else if(d.mainList.get(i).get(k).getActualNumber() == t.getActualNumber() && !d.mainList.get(i).get(k).alreadyConnected) {
							t.heading = new Point(i, k);
							break;
						}
						else {
							t.heading = new Point(i, d.firstLoopUpdate(k));
							break;
						}
					}
					int to = (d.getNeededSize() / canvasLength) * t.heading.y;
					int where = layout.getConstraint(d.direction, t).getValue();
					if(where == to)
						continue;
					final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
					final TileCanvas canvas = Better2048.game;
					d.mainList.get(i).set(j, null);
					d.sideList.get(j).set(i, null);
					final Tile copy = d.mainList.get(t.heading.x).get(t.heading.y);
					if(copy == null) {
						d.mainList.get(t.heading.x).set(t.heading.y, t);
						d.sideList.get(t.heading.y).set(t.heading.x, t);
					}
					else if(copy.getActualNumber() == t.getActualNumber() && !copy.equals(t)) {
						copy.setActualNumber(copy.getActualNumber() * 2);
						copy.alreadyConnected = true;
						t.shouldConnect = true;
					}
					ses.scheduleAtFixedRate(new Runnable() {
						@Override
						public void run() {
							int to = (d.getNeededSize() / canvasLength) * t.heading.y;
							int where = layout.getConstraint(d.direction, t).getValue();
							if(where == to) {
								if(copy == null)
									d.place(t, t.heading.x, t.heading.y);
								else if(t.equals(copy)) {}
								else if(t.shouldConnect) {
									copy.setShownNumber(copy.getActualNumber());
									if(!alreadyWon && copy.getShownNumber() == 2048) {
										isShowingWin = true;
										alreadyWon = true;
									}
									copy.setFont(new Font("Tile Font", Font.PLAIN, calcFont(copy)));
									if(Better2048.discoBox.isSelected())
										copy.setRandomBackground();
									else
										copy.setBackground(copy.getShownNumber() > 2048 ? Color.decode("#FF0000") : tileColors.get(copy.getShownNumber()));
									if(Long.parseLong(Better2048.currentHighestTile.getText()) < copy.getShownNumber())
										Better2048.currentHighestTile.setText(Long.toString(copy.getShownNumber()));
									if(Long.parseLong(Better2048.highestTile.getText()) < Long.parseLong(Better2048.currentHighestTile.getText()))
										Better2048.highestTile.setText(Better2048.currentHighestTile.getText());
									Better2048.currentScore.setText(Long.toString(Long.parseLong(Better2048.currentScore.getText()) + copy.getShownNumber()));
									if(Long.parseLong(Better2048.highScore.getText()) < Long.parseLong(Better2048.currentScore.getText()))
										Better2048.highScore.setText(Long.toString(Long.parseLong(Better2048.currentScore.getText())));
									t.destroyWhileMoving();
								}
								if(t.isMoving) {
									t.isMoving = false;
									synchronized(movingTilesCount) {
										movingTilesCount--;
									}
									if(movingTilesCount == 0) {
										createNewTile();
										for(int i = 0; i < canvasLength; i++)
											for(int j = 0; j < canvasLength; j++)
												if(verticLocs.get(i).get(j) != null && verticLocs.get(i).get(j).alreadyConnected)
													verticLocs.get(i).get(j).alreadyConnected = false;
										checkGameOver();
										if(isShowingWin && !isGameOver) 
											winLabel.setVisible(true);
									}
								}
								ses.shutdown();
								return;
							}
							if(!t.isMoving) {
								t.isMoving = true;
								synchronized(movingTilesCount) {
									movingTilesCount++;
								}
							}
							layout.putConstraint(d.direction, t, d.positionChange(where), d.direction, canvas);
							checkGameOver();
							canvas.repaint();
							canvas.revalidate();
						}
					}, 0, (long) (d.equals(Direction.UP) || d.equals(Direction.DOWN) ? verticSpeed : horizSpeed), TimeUnit.MICROSECONDS);
				}
	}
	
	@Override
	public void componentResized(ComponentEvent arg0) {
		if(verticLocs.size() == 0)
			return;
		lossLabel.setPreferredSize(getPreferredSize());
		winLabel.setPreferredSize(getPreferredSize());
		setBackground(Color.LIGHT_GRAY);
		for(int i = 0; i < canvasLength; i++)
			for(int j = 0; j < canvasLength; j++)
				if(verticLocs.get(i).get(j) != null)
					positionTile(verticLocs.get(i).get(j), i, j);
		Dimension d = getPreferredSize();
		double i = (double) d.height / 664;
		double j = (double) d.width / 664;
		switch(Better2048.comboBox.getSelectedItem().toString()) {
            case "Very Fast":
                verticSpeed = (double) 300 / i;
                horizSpeed = (double) 300 / j;
                break;
            case "Fast":
                verticSpeed = (double) 600 / i;
                horizSpeed = (double) 600 / j;
                break;
            case "Normal":
                verticSpeed = (double) 900 / i;
                horizSpeed = (double) 900 / j;
                break;
            case "Slow":
                verticSpeed = (double) 1200 / i;
                horizSpeed = (double) 1200 / j;
                break;
            case "Very Slow":
                verticSpeed = (double) 1500 / i;
                horizSpeed = (double) 1500 / j;
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		requestFocusInWindow();
		if(isShowingWin) {
			isShowingWin = false;
			winLabel.setVisible(false);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent ke) {
		if(ke.getKeyCode() == KeyEvent.VK_R) {
			Better2048.restartGame.doClick();
			return;
		}
		else if(ke.getKeyCode() == KeyEvent.VK_Z) {
			Better2048.frame.setSize(new Dimension(828, 700));
			return;
		}
		if(isGameOver)
			return;
		if(isShowingWin)
			return;
		if(movingTilesCount > 0)
			return;
		if(ke.getKeyCode() == KeyEvent.VK_UP || ke.getKeyCode() == KeyEvent.VK_W)
			move(Direction.UP);
		else if(ke.getKeyCode() == KeyEvent.VK_DOWN || ke.getKeyCode() == KeyEvent.VK_S)
			move(Direction.DOWN);
		else if(ke.getKeyCode() == KeyEvent.VK_LEFT || ke.getKeyCode() == KeyEvent.VK_A)
			move(Direction.LEFT);
		else if(ke.getKeyCode() == KeyEvent.VK_RIGHT || ke.getKeyCode() == KeyEvent.VK_D)
			move(Direction.RIGHT);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		lastSize = getPreferredSize();
		if(canvasLength == 0)
			canvasLength = 4;
		g.setColor(Color.LIGHT_GRAY);
		for(int i = 0; i <= canvasLength; i++) {
			g.drawLine((lastSize.width / canvasLength) * i, 0, (lastSize.width / canvasLength) * i, lastSize.height);
			g.drawLine(0, (lastSize.height / canvasLength) * i, lastSize.width, (lastSize.height / canvasLength) * i);
		}
		g.setColor(Color.BLACK);
		for(int i = 0; i <= canvasLength; i++) {
			Dimension d = getPreferredSize();
			g.drawLine((d.width / canvasLength) * i, 0, (d.width / canvasLength) * i, d.height);
			g.drawLine(0, (d.height / canvasLength) * i, d.width, (d.height / canvasLength) * i);
		}
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentShown(ComponentEvent arg0) {}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}