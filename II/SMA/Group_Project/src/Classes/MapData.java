package Classes;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import Exceptions.InvalidMove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MapData {

    public Position [][] map;

    public MapData(int length, int[][] args) {

        this.map = new Position[length][length];
        
        // Inicialização tudo a 0s (tudos os blocos estao livre)
        for (int i = 0; i < length; i++) { 
            for (int j = 0; j < length; j++) {
                
                map[i][j] = new Position(i,j);
                if (i == 0 || i == length-1 || j == 0 || j == length-1)
                    map[i][j].setBorder(true);
            }
        }

        // Meter posições iniciais dos players
        for(int elem = 0 ; elem < 10; elem++) {
            int x = args[elem][0];
            int y = args[elem][1];
            int team = args[elem][2];
            int playerid = args[elem][4];
            map[x][y].setAvailable(team);
            if (team == 1) {
                map[x][y].setIdPlayerPos("B" + playerid);
            }
            else {
                map[x][y].setIdPlayerPos("R" + playerid);
            }
        }

    }

    // 
    public Set<String> checkDeadPlayers(Position p) {
        Set<String> deadPlayers = new HashSet<>();

        int xMin = p.getX()-3;
        if (xMin < 0) 
            xMin = 0;
        int xMax = p.getX()+3;
        if (xMax >= map.length) 
            xMax = map.length-1;

        int yMin = p.getY()-3;
        if (yMin < 0) 
            yMin = 0;
        int yMax = p.getY()+3;
        if (yMax >= map.length) 
            yMax = map.length-1;

        // Ver o campo de visão à procura de inimigas invejosas
        // 7x7
        for (int x = xMin; x <= xMax; x++) {
            for(int y = yMin; y <= yMax; y++) {
                Position pos = map[x][y];
                //Check quem é a equipa inimiga
                if (pos.getAvailable() != 0) {
                    int enemyteam = 0;
                    if (pos.getAvailable() == 1)
                        enemyteam = 2;
                    else if (pos.getAvailable() == 2)
                        enemyteam = 1;


                    // Preencher lista com as inimigas
                    if (enemiesAround(pos, enemyteam)) {
                        deadPlayers.add(pos.getIdPlayerPos());
                        pos.setAvailable(0);
                        pos.setIdPlayerPos("-");
                    }
                }
            }
        }
        return deadPlayers;
    }
    
    // Passamos a enemyteam para saber quem são as inimigas invejosas
    public boolean enemiesAround(Position p, int enemyteam) {
        int enemiesCounter = 0;
        int counter = 0;

        // Cima: há bordas ou inimigo
        if (p.getY() == 0 || (p.getY()-1 > 0 && map[p.getX()][p.getY()-1].getAvailable() != 0)) {
            counter++;
            if (p.getY() != 0 && map[p.getX()][p.getY()-1].getAvailable() == enemyteam)
                enemiesCounter++;
        }

        // Baixo: há bordas ou inimigo
        if (p.getY() == map.length-1 || (p.getY()+1 < map.length && map[p.getX()][p.getY()+1].getAvailable() != 0)) {
            counter++;
            if (p.getY() != map.length-1 && map[p.getX()][p.getY()+1].getAvailable() == enemyteam)
                enemiesCounter++;
        }

        // Esquerda: há bordas ou inimigo
        if (p.getX() == 0 || (p.getX()-1 >= 0 && map[p.getX()-1][p.getY()].getAvailable() != 0)) {
            counter++;
            if (p.getX() != 0 && map[p.getX()-1][p.getY()].getAvailable() == enemyteam)
                enemiesCounter++;
        }

        // Direita: há bordas ou inimigo
        if (p.getX() == map.length-1 || (p.getX()+1 < map.length && map[p.getX()+1][p.getY()].getAvailable() != 0)) {
            counter++;
            if (p.getX() != map.length-1 && map[p.getX()+1][p.getY()].getAvailable() == enemyteam)
                enemiesCounter++;
        }
        return (counter == 4 && enemiesCounter >= 1);
    }
    

    public Map<String, List<Position>> getVisionFieldList(Map<String,Position> args) {
        Map<String, List<Position>> visionFields = new HashMap<>();
        for (Entry<String,Position> entry : args.entrySet()) {
            visionFields.put(entry.getKey(), getVisionField(entry.getValue()));
        }
        return visionFields;
    }
    
    
    // Campo de visão de todos os jogadores
    public Map<String, List<Position>> getVisionFieldList(int[][] args) {
        Map<String, List<Position>> visionFields = new HashMap<>();
        for(int elem = 0; elem < args.length; elem++) {
            
            int x = (int) args[elem][0];
            int y = (int) args[elem][1];
            
            Position p = new Position(x, y);
            String team = "R";
            if (elem >= 5)
                team = "B";

            if (team.equals("B")) {
                String idRed = team + (elem-5);
                visionFields.put(idRed, getVisionField(p));
            }
            else {
                String idRed = team + elem;
                visionFields.put(idRed, getVisionField(p));
            }
        }
        return visionFields;
    }

    // Campo Visão 
    public List<Position> getVisionField(Position p) {
        List<Position> res = new ArrayList<>();
        int xMin = p.getX()-3;
        if (xMin < 0) 
            xMin = 0;
        int xMax = p.getX()+3;
        if (xMax >= map.length) 
            xMax = map.length-1;

        int yMin = p.getY()-3;
        if (yMin < 0) 
            yMin = 0;
        int yMax = p.getY()+3;
        if (yMax >= map.length) 
            yMax = map.length-1;
        
        for (int x = xMin; x <= xMax; x++) {
            for(int y = yMin; y <= yMax; y++) {
                // ocupado por um tank  ou borda  // e não for posição do próprio  //
                if ((map[x][y].getAvailable() != 0 || map[x][y].getBorder()) && !(x == p.getX() && y == p.getY())) {
                    res.add(map[x][y]);
                }
            }
        }
        return res;
    }

    // Fazer jogada
    public Position registerMove(String playerid, Position atual, int direction) throws InvalidMove {

        //printMap();

        Position dest = null;
        // Garantir que jogador não sai do mapa
        if(atual.getX() <= map.length-1 && atual.getX()>= 0 && atual.getY()<= map.length-1 && atual.getY() >= 0) {
            // Obter nova posicao
            switch(direction) {
                case 0: // up
                    if(atual.getY() > 0)
                        dest = new Position(atual.getX(), atual.getY()-1);
                    break; 
                case 1: // down
                    if(atual.getY() < map.length-1)
                        dest = new Position(atual.getX(), atual.getY()+1);
                    break;
                case 2: // left
                    if(atual.getX() > 0)
                        dest = new Position(atual.getX()-1, atual.getY());
                    break;
                case 3: // right
                    if(atual.getX() < map.length-1)
                        dest = new Position(atual.getX()+1, atual.getY());
                    break;
            }

            // Verificar se posição está vazia
            if (dest != null && map[atual.getX()][atual.getY()].getIdPlayerPos().equals(playerid) && map[dest.getX()][dest.getY()].getAvailable() == 0) {
                String team = playerid.substring(0,1);
                int t = 2;
                if (team.equals("B"))
                    t = 1;

                map[dest.getX()][dest.getY()].setAvailable(t);
                map[dest.getX()][dest.getY()].setIdPlayerPos(playerid);
                // Acrescentar ocupado
                dest = map[dest.getX()][dest.getY()];

                // Fica atual (antigo) livre
                map[atual.getX()][atual.getY()].setAvailable(0);
                map[atual.getX()][atual.getY()].setIdPlayerPos("-");
            }
            else {
                throw new InvalidMove();
            }
        }
        return dest;
    }



    public void printMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {

                if (map[j][i].getAvailable() == 0)
                    System.out.print(map[j][i].getAvailable() + " ");
                else
                    System.out.print(map[j][i].getIdPlayerPos() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
}



