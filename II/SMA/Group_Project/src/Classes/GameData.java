package Classes;

import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

import Exceptions.DeadPlayer;
import Exceptions.InvalidMove;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class GameData extends JPanel implements ActionListener {

	private MapData map;
	//public Teams teams;
	private Map<String,PlayerUI> allPlayers;

	private Timer timer;
	private int delay=8;
	int tempo = 0;
	
	private KeyboardListener keyboardListener;

	private boolean play = true;

	private Image img;
	private final String imagePath = "images/";

	// right side background
	private int mapSize;
	private int blockSize;
	private int length;
	public int redMovements = 0;
    public int blueMovements = 0;
	
	public GameData(int mapSize, int blockSize, int[][] playerPositions) {
		this.mapSize = mapSize;
		this.blockSize = blockSize;
		this.length = mapSize * blockSize;

		try {
			img = ImageIO.read(new File(imagePath + "blockSmall.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.map = new MapData(mapSize, playerPositions);
		
		// Inicializar equipas e players
		this.allPlayers = new HashMap<>();
		
		// Equipa Vermelha
        for (int i = 0; i < 5; i++) {
			String id = "";
			if (i > 0) {
				id = String.valueOf(i);
			}
			id = id + "S";
			PlayerUI p = new PlayerUI("R" + i, imagePath + "tank_redteam" + id + ".png",imagePath + "red_down" + id + ".png",imagePath + "red_left" + id + ".png", imagePath + "red_right" + id + ".png", playerPositions[i][0], playerPositions[i][1], blockSize);
            allPlayers.put(p.getIdPlayer(), p);
        }
		// Equipa Azul
		for (int j = 5; j < playerPositions.length; j++) {
			String id = "";
			if (j-5 > 0) {
				id = String.valueOf(j-5);
			}
			id = id + "S";
			PlayerUI p = new PlayerUI("B" + (j-5), imagePath + "tank_blueteam" + id + ".png",imagePath + "blue_down" + id + ".png", imagePath + "blue_left" + id + ".png", imagePath + "blue_right" + id + ".png", playerPositions[j][0], playerPositions[j][1], blockSize);
			allPlayers.put(p.getIdPlayer(), p);
		}
		
		keyboardListener = new KeyboardListener(); // teclas
		
		setFocusable(true);
		//addKeyListener(this);
		addKeyListener(keyboardListener);
		setFocusTraversalKeysEnabled(false);
        timer=new Timer(delay,this);
		timer.start();
	}

	public Map<String, List<Position>> getVisionFieldList(int[][] args) {
		return map.getVisionFieldList(args);
	}


	public Map<String, List<Position>> getVisionFieldList() {
		Map<String, Position> playerPos = new HashMap<>();
		
		for(Entry<String,PlayerUI> entry : this.allPlayers.entrySet()) {
			playerPos.put(entry.getKey(), new Position(entry.getValue().getPosX(), entry.getValue().getPosY()));
		}
		return map.getVisionFieldList(playerPos);
	}


	public List<Position> getVisionField(Position p) {
		return map.getVisionField(p);
	}
	
	
	public void paint(Graphics g) {
		// Desenhar o background
		for (int x = 0; x < length; x += blockSize) {
			for (int y = 0; y < length; y += blockSize) {
				g.drawImage(img, x, y, null);
			}
		}

		// play background
		/*g.setColor(Color.black);
		g.fillRect(0, 0, 650, 600);*/
		
		// right side background
		g.setColor(Color.DARK_GRAY);
		g.fillRect(mapSize * blockSize, 0, 140, length); //antes estava 600
		
		// Se existirem player

		if(play) {
			for(PlayerUI p : allPlayers.values()){
				if (p.getAlive())
					p.setImg(this, g);
			}
		}

		drawScores(g);
		g.dispose();
	}

	

	public void drawScores(Graphics g) {
		int textPosX = length + 10;
		// the scores 		
		g.setColor(Color.white);
		g.setFont(new Font("serif",Font.BOLD, 15));

		// Pontuação
		//g.drawString("Scores", textPosX ,30);
		//g.drawString("Team Blue:  "+ playerUI1.getScore(), textPosX,60);
		

		// Número players vivos de cada equipa
		g.drawString("Lives", textPosX,30);
		g.drawString("Team Blue:  " + getTeamLives("B") , textPosX,60);
		g.drawString("Blue moves: " + blueMovements, textPosX,90);

		g.drawString("Lives", textPosX,150);
		g.drawString("Team Red:  " + getTeamLives("R") , textPosX,180);
		g.drawString("Red moves: " + redMovements, textPosX,210);


		g.drawString("Match No: " + "1", textPosX,270);
		g.drawString("Time: " + tempo, textPosX,300);
		
		
		if(getTeamLives("R") == 0 || getTeamLives("B") == 0 || (getTeamLives("R") == 1 && getTeamLives("B") == 1)) {
			String vitoryRed = "Team Red Won !";
			String vitoryBlue = "Team Blue Won !";
			String tie = "It's a tie folks !";
			g.setColor(Color.white);
			g.setFont(new Font("serif",Font.BOLD, 60));
			g.drawString("Game Over", 200,300);
			int gameStatus = -1;
			if (getTeamLives("B") == 0){
				g.drawString(vitoryRed, 180,380);
				gameStatus = 2;
			} else if (getTeamLives("R") == 0){
				g.drawString(vitoryBlue, 180,380);
				gameStatus = 1;
			} else {
				g.drawString(tie, 180,380);
				gameStatus = 3;
			}
			play = false;
			g.setColor(Color.white);
			g.setFont(new Font("serif",Font.BOLD, 30));
			g.drawString("(Space to Restart)", 230,430);

			printGameResults(gameStatus);
			
		}
	}

	public void printGameResults(int gameStatus) {
		String winner;
		if (gameStatus == 1) {
			winner = "Blue";
		}
		else if (gameStatus == 2) {
			winner = "Red";
		}
		else {
			winner = "Tie";
		}
		System.out.println("----------- Results -----------");
		System.out.println("Total time: " + tempo);
		System.out.println("Winner: " + winner);
		System.out.println("Red moves: " + redMovements);
		System.out.println("Red lives: " + getTeamLives("R"));
		System.out.println("Blue moves: " + blueMovements);
		System.out.println("Blue lives: " + getTeamLives("B"));
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		timer.start();

		if (play)
			tempo++;

		repaint();
	}

	public void addMoves(String team) {
		if (play) {
			if (team.equals("R")){
				redMovements++;
			}
			else {
				blueMovements++;
			}
		}
	}

	public int getTeamLives(String team) {
		int lives = 0;
		for(PlayerUI p : allPlayers.values()) {
			if (p.getIdTeam().equals(team) && p.getAlive())
				lives++;
		}
		return lives;
	}

	// Lista de ids dos jogadores assassinados
	// quim formação dramaatiiica
	//eu já obtive essa informação
	public Set<String> checkDeadPlayers(Position p) {
		Set<String> deadPlayers = new HashSet<>();
		deadPlayers = map.checkDeadPlayers(p);

		for (String string : deadPlayers) {
			
			// Eliminar player do mapa
			allPlayers.remove(string);
			
		}

		return deadPlayers;
	}


	public Position movePlayer(Position pOld,String idPlayer, int direction) throws DeadPlayer { // add Player
		if (direction == -1) 
			return pOld;
			
		PlayerUI p = allPlayers.get(idPlayer);
		if (p == null) throw new DeadPlayer();
		Position newPos = null;
		try {
			newPos = map.registerMove(idPlayer, pOld, direction);
			allPlayers.get(idPlayer).move(newPos, direction);
			repaint();
		} catch (InvalidMove e) {
			return pOld;
		}

		String team = idPlayer.substring(0,1);
        addMoves(team);
		
		return newPos;
	}

	public Set<String> getIdPlayers() {
		return this.allPlayers.keySet();
	}

	private class KeyboardListener implements KeyListener {
		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}		
		public void keyPressed(KeyEvent e) {	
			if(e.getKeyCode()== KeyEvent.VK_SPACE && (getTeamLives("R") == 0 || getTeamLives("B") == 0)) {
				play = true;
				repaint();
			}

		}
	}

}

