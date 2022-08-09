package Classes;

public class Position {

    private int x;
    private int y;
    private int available; // 0 = livre   &     1 = azul   &   2 =vermelho
    private boolean border; // 0 = sem borda  1 = c/ borda
    private String idPlayerPos; //id do player que está nesta posição



    public Position(String posString) {
        String[] visionFieldNew = posString.split(" ");
        x = Integer.valueOf(visionFieldNew[0]);
        y = Integer.valueOf(visionFieldNew[1]); 
        available = Integer.valueOf(visionFieldNew[2]); // Equipa
        border = Boolean.valueOf(visionFieldNew[3]);
        idPlayerPos = visionFieldNew[4];
    }


    // Player
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.available = 0;
        this.border = false;
    }

    public Position(){}

    /*
    public Position(int x, int y, boolean border) {
        this.x = x;
        this.y = y;
        this.available = 0;
        this.border = border;
    }*/

    public String getPosString() {
        return x + " " + y + " " + available + " " + border + " " + idPlayerPos;
    }
    

    public boolean getBorder() {
        return this.border;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAvailable(){ return available;}

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setAvailable(int av){ this.available = av;}


    public boolean isBorder() {
        return this.border;
    }

    public String getIdPlayerPos() {
        return this.idPlayerPos;
    }

    public void setIdPlayerPos(String idPlayerPos) {
        this.idPlayerPos = idPlayerPos;
    }



    @Override
    public String toString() {
        return "{" +
            " x='" + getX() + "'" +
            ", y='" + getY() + "'" +
            ", available='" + getAvailable() + "'" +
            ", border='" + isBorder() + "'" +
            ", idPlayerPos='" + getIdPlayerPos() + "'" +
            "}";
    }
    
    
}


