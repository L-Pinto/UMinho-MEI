package Behaviours;

import java.util.List;

import Agents.Player;
import Classes.Position;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Defender extends OneShotBehaviour {
    Player player = new Player();
    int enemyTeam;

    public Defender(Agent a) {
        super(a);
        this.player = (Player) a;
    }

    @Override
    public void action() {

        int direction = selectDirection(player.getPos());

        
        // Enviar nova posicao a interface
        DFAgentDescription d1 = new DFAgentDescription();
        ServiceDescription s1 = new ServiceDescription();
        s1.setType("Interface");
        d1.addServices(s1);

        try {
            // Manda a sua nova posição para a interface
            DFAgentDescription[] r = DFService.search(this.myAgent, d1);
            for (int i = 0; i < r.length; ++i) {
                DFAgentDescription d2 = r[i];
                AID p = d2.getName();
                ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
                m.addReceiver(p);
                
                m.setContent(this.player.getPos().getPosString() +  ":"  + direction); 

                myAgent.send(m);
                player.waiting = true;
            }

        } catch (FIPAException e) {
            e.printStackTrace();
        }
        
    }

    
    // seleciona a direção com menos inimigos
    public int selectDirection(Position playerPos) {
        if (player.getTeamId().equals("B")) {
            enemyTeam = 2; // Red
        }
        else {
            enemyTeam = 1;
        }

        List<Position> visionField = this.player.getVisionField();
        int[] counterArr = new int[4];
        boolean[] obstacleList = new boolean[4];
        
        // Borda, Amigo ou Inimigo
        for (Position p : visionField) {

            //cima
            if(p.getY() < playerPos.getY() && p.getAvailable()==enemyTeam) {
                counterArr[0]++;
                
            }
            // baixo
            if(p.getY() > playerPos.getY() && p.getAvailable()==enemyTeam) {
                counterArr[1]++;
            }
            // esqueda 
            if(p.getX() < playerPos.getX() && p.getAvailable()==enemyTeam) {
                counterArr[2]++;
            }
            //direita
            if(p.getX() > playerPos.getX() && p.getAvailable()==enemyTeam) {
                counterArr[3]++;
            }
            
            // VER SE ESTÁ BLOQUEADO 

            // cima
            if (p.getY() == playerPos.getY()-1 &&  p.getX() == playerPos.getX() && p.getAvailable() != 0) {
                obstacleList[0] = true;
            }
            // baixo
            if (p.getY() == playerPos.getY()+1 &&  p.getX() == playerPos.getX() && p.getAvailable() != 0) {
                obstacleList[1] = true;
            }
            //esquerda
            if (p.getX() == playerPos.getX()-1 &&  p.getY() == playerPos.getY() && p.getAvailable() != 0) {
                obstacleList[2] = true;
            }
            //direita
            if (p.getX() == playerPos.getX()+1 &&  p.getY() == playerPos.getY() && p.getAvailable() != 0) {
                obstacleList[3] = true;
            }
        }

        int min = 0;
        for (int index = 0; index < counterArr.length; index++) {
            // se for uma posição boa e estiver livre
            if(counterArr[index] <= counterArr[min] && obstacleList[index] == false) {

                min = index;
            }
        }
        
        return min;
    }

    
}
