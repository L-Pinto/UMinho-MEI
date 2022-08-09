package Behaviours;

import java.util.ArrayList;

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
import java.util.List;
import java.util.Random;

public class Explorer extends OneShotBehaviour{

    Player player = new Player();

    public Explorer(Agent a) {
        super(a);
        this.player = (Player) a;
    }
    
	@Override
	public void action() {
		//Pesquisar o serviço
            DFAgentDescription d = new DFAgentDescription();
            ServiceDescription s = new ServiceDescription();
            s.setType("Interface");
            d.addServices(s);
            
            try {
                // Manda a sua nova posição para a interface
				DFAgentDescription[] r = DFService.search(this.myAgent, d);
                for (int i = 0; i < r.length; ++i) {
                    DFAgentDescription d2 = r[i];
                    AID p = d2.getName();
                    //Criar mensagem a enviar
                    ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
                    // Destinatário
                    m.addReceiver(p);
                    
                    // Calcular melhor direcao !!!!!!!!!!!!!!!
                    int direction;
                    List<Integer> wallDirectionL = wallDirection();
                    Random random = new Random();
                    
                    // Escolher posição randomly
                    
                    //if (wallDirectionL.size() > 0) {
                        int high = wallDirectionL.size();
                        int result = random.nextInt(high);
                        direction = wallDirectionL.get(result);
                    //}
                    //else {
                        // Quando estás rodeado de inimigos

                        // Numa borda
                    //      direction = random.nextInt(4);
                    //}
                    m.setContent(this.player.getPos().getPosString() +  ":"  + direction); 
                    myAgent.send(m);
                    player.waiting = true;
                    

                }
			} 
            catch (FIPAException e) {
				e.printStackTrace();
			}
	}

    // lista das direções com e sem paredes
    public List<Integer> wallDirection() {
        List<Integer> wallDirectionsL = new ArrayList<>();
        boolean[] wallDirections = new boolean[4];
        int posX = player.getPos().getX();
        int posY = player.getPos().getY();
        int counter = 0;
        List<Position> posList = player.getVisionField();
        
        // Estás em cima de uma borda
        if(player.getPos().getBorder()) {
            
            // Cima 
            if( posY == 0 ){
                wallDirections[0] = true; // não vás prai
                counter++;
            }
            // Baixo
            else if(posY == player.mapSize - 1 ) {
                wallDirections[1] = true;
                counter++;
            }
            // Esquerda
            if(posX == 0 ) {
                wallDirections[2] = true;
                counter++;
            }
            // Direita
            else if(posX == player.mapSize - 1 ) { 
                wallDirections[3] = true;
                counter++;
            }
        }
        
        if (counter != 2) {
            // Posição Campo Visão
            for(Position p : posList) {
                // Para todas as bordas
                if (p.getBorder()) {
                    // Cima 
                    if(p.getY() < posY && posX == p.getX())
                        wallDirections[0] = true; // não vás prai
                    // Baixo 
                    else if(p.getY() > posY && posX == p.getX())
                        wallDirections[1] = true;
                    // Esquerda 
                    if(p.getX() < posX && posY == p.getY())
                        wallDirections[2] = true;
                    // Direita 
                    else if(p.getX() > posX && posY == p.getY()) {
                        wallDirections[3] = true;
                    }
                }

                // Equipa Amiga
                if (p.getAvailable() == 1){

                }
                // Equipa Inimiga
                
            }
        }

        for (int i = 0; i < wallDirections.length; i++) {
            if (wallDirections[i] == false) 
                wallDirectionsL.add(i);
        }
        
        return wallDirectionsL;
    }
    
}
