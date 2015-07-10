package com.github.thesupermariobro.better2048;

import java.awt.EventQueue;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class Better2048 extends JFrame implements ComponentListener, ActionListener {

	static Better2048 frame;
	static TileCanvas game;
	static JPanel stats;
	static JLabel currentScore;
	static JLabel currentHighestTile;
	static JLabel highScore;
	static JLabel highestTile;
	static JTextField lengthField;
	static JCheckBox discoBox;
	static JComboBox<Object> comboBox;
	static JButton restartGame;
	static Properties props;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
            try {
                frame = new Better2048();
                frame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
	}

	public Better2048() throws IOException {
		frame = this;
		addComponentListener(this);
		setTitle("Better2048"); 
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(828, 700);
		setMinimumSize(new Dimension(700, 700));
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent event) {
				save();
				frame.dispose();
				System.exit(0);
			}
		});
		
		discoBox = new JCheckBox("Disco - Off");
		discoBox.setForeground(Color.WHITE);
		discoBox.setBackground(Color.BLACK);
		discoBox.setToolTipText("Toggle disco mode where tiles get a random color when connected or created");
		discoBox.addActionListener(arg -> {
            if(discoBox.isSelected())
                discoBox.setText("Disco - On");
            else
                discoBox.setText("Disco - Off");
            game.requestFocusInWindow();
        });
		
		game = new TileCanvas();
		
		stats = new JPanel();
		stats.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));
		stats.setBackground(Color.GRAY);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(game, GroupLayout.PREFERRED_SIZE, 529, Short.MAX_VALUE)
					.addComponent(stats, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addComponent(stats, GroupLayout.PREFERRED_SIZE, 661, Short.MAX_VALUE)
				.addComponent(game, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE)
		);
		
		JLabel labelCurrentScore = createJLabel("Current Score:", false);
		JLabel lblCurrentHighestTile = createJLabel("Current Highest Tile:", false);
		JLabel lblHighScore = createJLabel("High Score:", false);
		JLabel lblHighestTile = createJLabel("Highest Tile:", false);
		JLabel lblTileCanvasLength = createJLabel("Tile Canvas Length:", false);
		JLabel lblDiscoMode = createJLabel("Disco Mode:", false);
		JLabel lblTileMovingSpeed = createJLabel("Tile Moving Speed:", false);
		currentScore = createJLabel("Shows your current score", true);
		currentHighestTile = createJLabel("Shows your current highest tile", true);
		highScore = createJLabel("Shows your high score", true);
		highestTile = createJLabel("Shows your highest tile", true);
		
		restartGame = new JButton("Restart Game");
		restartGame.setToolTipText("Click to restart the game");
		restartGame.setForeground(Color.WHITE);
		restartGame.setBackground(Color.BLACK);
		restartGame.addActionListener(this);
		
		lengthField = new JTextField();
		lengthField.setSelectedTextColor(Color.WHITE);
		lengthField.setBackground(Color.BLACK);
		lengthField.setForeground(Color.WHITE);
		lengthField.setToolTipText("Change the number to set the length of the tile canvas");
		lengthField.setText("4");
		lengthField.setColumns(10);
		lengthField.setHorizontalAlignment(JTextField.CENTER);
		
		String[] options = new String[] {"Very Fast", "Fast", "Normal", "Slow","Very Slow"};
		comboBox = new JComboBox<Object>(options);
		comboBox.setBackground(Color.BLACK);
		comboBox.setForeground(Color.WHITE);
		comboBox.setSelectedIndex(2);
		comboBox.setToolTipText("Choose how fast you want the tiles to move");
		comboBox.addActionListener(e -> {
            game.requestFocusInWindow();
            Dimension d = getSize();
            double i = (double) d.height / 664;
            double j = (double) d.width / 664;
            switch(comboBox.getSelectedItem().toString()) {
                case "Very Fast":
                    TileCanvas.verticSpeed = (double) 300 / i;
                    TileCanvas.horizSpeed = (double) 300 / j;
                    break;
                case "Fast":
                    TileCanvas.verticSpeed = (double) 600 / i;
                    TileCanvas.horizSpeed = (double) 600 / j;
                    break;
                case "Normal":
                    TileCanvas.verticSpeed =  (double) 900 / i;
                    TileCanvas.horizSpeed = (double) 900 / j;
                    break;
                case "Slow":
                    TileCanvas.verticSpeed = (double) 1200 / i;
                    TileCanvas.horizSpeed = (double) 1200 / j;
                    break;
                case "Very Slow":
                    TileCanvas.verticSpeed = (double) 1500 / i;
                    TileCanvas.horizSpeed = (double) 1500 / j;
            }
        });
		
		GroupLayout gl_stats = new GroupLayout(stats);
		gl_stats.setHorizontalGroup(
			gl_stats.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_stats.createSequentialGroup()
					.addGroup(gl_stats.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_stats.createSequentialGroup()
							.addContainerGap()
							.addComponent(lengthField, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
						.addComponent(labelCurrentScore)
						.addGroup(gl_stats.createSequentialGroup()
							.addGap(10)
							.addComponent(currentScore, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
						.addGroup(gl_stats.createSequentialGroup()
							.addContainerGap()
							.addComponent(restartGame, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
						.addComponent(lblCurrentHighestTile)
						.addGroup(gl_stats.createSequentialGroup()
							.addContainerGap()
							.addComponent(currentHighestTile, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
						.addComponent(lblHighScore)
						.addGroup(gl_stats.createSequentialGroup()
							.addContainerGap()
							.addComponent(highScore, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
						.addComponent(lblHighestTile)
						.addGroup(gl_stats.createSequentialGroup()
							.addContainerGap()
							.addComponent(highestTile, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
						.addComponent(lblTileCanvasLength)
						.addGroup(gl_stats.createSequentialGroup()
							.addGap(10)
							.addComponent(discoBox, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
						.addComponent(lblDiscoMode)
						.addComponent(lblTileMovingSpeed)
						.addGroup(gl_stats.createSequentialGroup()
							.addContainerGap()
							.addComponent(comboBox, 0, 128, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_stats.setVerticalGroup(
			gl_stats.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_stats.createSequentialGroup()
					.addComponent(labelCurrentScore)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(currentScore, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblCurrentHighestTile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(currentHighestTile, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblHighScore)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(highScore, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblHighestTile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(highestTile, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblTileCanvasLength)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lengthField, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblDiscoMode)
					.addGap(7)
					.addComponent(discoBox)
					.addGap(18)
					.addComponent(lblTileMovingSpeed)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
					.addComponent(restartGame, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		stats.setLayout(gl_stats);
		getContentPane().setLayout(groupLayout);
		game.requestFocusInWindow();
		new Thread() {
			@Override
			public void run() {
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						restartGame.doClick();
					}
				}, 10L);
			}
		}.start();
		props = new Properties();
		File f = new File(System.getenv("Appdata") + File.separator + ".better2048");
		if(!f.exists())
			f.mkdirs();
		f = new File(f + File.separator + "better2048.properties");
		if(!f.exists())
			f.createNewFile();
		load();
	}
	
	@Override
	public void componentResized(ComponentEvent ce) {
		game.setPreferredSize(new Dimension(getSize().width - 164, getSize().height - 36));
	}
	
	private void save() {
		try {
			OutputStream os = new FileOutputStream(System.getenv("Appdata") + File.separator + ".better2048" + File.separator + "better2048.properties");
			props.setProperty("highScore" + TileCanvas.canvasLength, highScore.getText());
			props.setProperty("highestTile" + TileCanvas.canvasLength, highestTile.getText());
			props.store(os, "Better2048's highscore saving file");
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void load() {
		try {
			InputStream is = new FileInputStream(System.getenv("Appdata") + File.separator + ".better2048" + File.separator + "better2048.properties");
			props.load(is);
			is.close();
			if(props.getProperty("highScore" + TileCanvas.canvasLength) != null && props.getProperty("highestTile" + TileCanvas.canvasLength) != null) {
				highScore.setText(props.getProperty("highScore" + TileCanvas.canvasLength));
				highestTile.setText(props.getProperty("highestTile" + TileCanvas.canvasLength));
			}
			else {
				highScore.setText("0");
				highestTile.setText("0");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		TileCanvas.isShowingWin = false;
		TileCanvas.alreadyWon = false;
		TileCanvas.isGameOver = false;
		TileCanvas.lossLabel.setVisible(false);
		TileCanvas.winLabel.setVisible(false);
		save();
		for(int i = 0; i < TileCanvas.canvasLength; i++)
			for(int j = 0; j < TileCanvas.canvasLength; j++)
				if(TileCanvas.verticLocs.get(i).get(j) != null)
					TileCanvas.verticLocs.get(i).get(j).destroy();
		lengthField.setText(Integer.toString(TileCanvas.canvasLength = (lengthField.getText().matches("[1-9][0-9]*") ? Integer.parseInt(lengthField.getText()) : 4)));
		int smaller = game.getPreferredSize().width < game.getPreferredSize().height ? game.getPreferredSize().width : game.getPreferredSize().height;
		if(TileCanvas.canvasLength == 1) {
			TileCanvas.canvasLength = 4;
			lengthField.setText("4");
		}
		else if(Integer.parseInt(lengthField.getText()) > smaller) {
			TileCanvas.canvasLength = smaller;
			lengthField.setText(Integer.toString(smaller));
		}
		currentScore.setText("0");
		currentHighestTile.setText("0");
		TileCanvas.verticLocs.clear();
		TileCanvas.horizLocs.clear();
		for(int i = 0; i < TileCanvas.canvasLength; i++) {
			List<Tile> temp1 = new ArrayList<Tile>();
			List<Tile> temp2 = new ArrayList<Tile>();
			for(int j = 0; j < TileCanvas.canvasLength; j++) {
				temp1.add(null);
				temp2.add(null);
			}
			TileCanvas.verticLocs.add(temp1);
			TileCanvas.horizLocs.add(temp2);
		}
		game.requestFocusInWindow();
		load();
		TileCanvas.createNewTile();
		TileCanvas.createNewTile();
	}
	
	public JLabel createJLabel(String text, boolean global) {
		JLabel label = new JLabel(global ? "0" : text);
		label.setOpaque(true);
		label.setForeground(Color.WHITE);
		label.setBackground(Color.BLACK);
		if(global) {
			label.setToolTipText(text);
			label.setHorizontalAlignment(JLabel.CENTER);
		}
		return label;
	}
	
	@Override
	public void componentHidden(ComponentEvent arg0) {} 

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentShown(ComponentEvent arg0) {}
}