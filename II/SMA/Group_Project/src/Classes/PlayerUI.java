package Classes;

import javax.swing.*;
import java.awt.*;

public class PlayerUI {

    private String idPlayer;
    private String idTeam;	
    private ImageIcon[] imageIcons;
    
    // Posicao inicial
	private int posX;
	private int posY;

    // 0:up 1:down 2:left 3:right
    private int direction = 0; 

	private int score = 0;
	private boolean alive = true;
    private int blockSize;


    /* Constructors */

    public PlayerUI(String idPlayer, String up, String down, String left, String right , int posX, int posY, int blocksize) {
        setImageIcons(up,down,left,right);
        this.idPlayer = idPlayer;
        this.idTeam = idPlayer.substring(0,1);
        this.blockSize = blocksize;
        setPosition(posX, posY);
        this.direction = 0;
        this.score = 0;
        this.alive = true;
    }

    public PlayerUI() {}


    /* Methods */

    public void move(Position newPos, int direction) {
        this.direction = direction;
        setPosition(newPos.getX(), newPos.getY());
    }


    /* Getters and Setters*/
    
    public void setImageIcons(String up, String down, String left, String right) {
        imageIcons = new ImageIcon[4];
        imageIcons[0] = new ImageIcon(up);
        imageIcons[1] = new ImageIcon(down);
        imageIcons[2] = new ImageIcon(left);
        imageIcons[3] = new ImageIcon(right);
    }

    public void setImg(Component c, Graphics g) {
        this.imageIcons[direction].paintIcon(c, g, posX, posY);
    }

    public void setPosition(int x, int y) {
        posX = x * blockSize;
        posY = y * blockSize;
    }



    public String getIdPlayer() {
        return this.idPlayer;
    }

    public void setIdPlayer(String idPlayer) {
        this.idPlayer = idPlayer;
    }

    public String getIdTeam() {
        return this.idTeam;
    }

    public void setIdTeam(String idTeam) {
        this.idTeam = idTeam;
    }

    public ImageIcon[] getImageIcons() {
        return this.imageIcons;
    }


    public int getBlockSize() {
        return this.blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }


    public int getPosX() {
        return this.posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public boolean getAlive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public String toString() {
        return "{" +
            " posX='" + getPosX() + "'" +
            ", posY='" + getPosY() + "'" +
            ", direction='" + getDirection() + "'" +
            ", score='" + getScore() + "'" +
            ", alive='" + isAlive() + "'" +
            "}";
    }
    
}
