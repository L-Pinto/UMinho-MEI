package Behaviours;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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



public class Attacker extends OneShotBehaviour {

    Player player = new Player();
    Position enemy;

    public Attacker(Agent a, Position enemy) {
        super(a);
        this.player = (Player) a;
        this.enemy = enemy;
    }
    
	@Override
	public void action() {
		//Pesquisar o serviço
        DFAgentDescription d = new DFAgentDescription();
        ServiceDescription s = new ServiceDescription();
        s.setType(player.getTeamId());
        d.addServices(s);

        
        int custoEnemy;

        // VAI AJUDAR
        if (this.enemy == null /*|| player.helper == 1*/) {
            custoEnemy = Integer.MAX_VALUE;
        }
        else { // Detetou alguém
            custoEnemy = enemyCostGlobal(this.enemy);
        }
        // Atualizar custo do target
        if (player.targetPos != null)
            player.targetCost = enemyCostGlobal(player.targetPos);

        // Calcular a custo de atualizar target

        // Se compensar mudar de target
        if (custoEnemy <= this.player.targetCost || player.helper == 0) {
            this.player.targetCost = custoEnemy;
            this.player.targetPos = enemy;
            player.helper = 0;
            try {
                // Manda o seu target para os amigos
                DFAgentDescription[] r = DFService.search(this.myAgent, d);
                for (int i = 0; i < r.length; ++i) {
                    DFAgentDescription d2 = r[i];
                    AID p = d2.getName();
                    // Verificar se nao é o proprio
                    if (!p.getLocalName().equals(player.getId())) {
                        //Criar mensagem a enviar
                        ACLMessage m = new ACLMessage(ACLMessage.INFORM_IF);

                        // Envia o target + custo (somatório das distâncias)
                        m.setContent(enemy.getPosString() + "/" + custoEnemy);
                        // Destinatário 
                        m.addReceiver(p);
                        myAgent.send(m);
                    }
                }
            }
            catch (FIPAException e) {
                e.printStackTrace();
            }
        }
        else {
            // Ignora target do campo de visão,target antigo era melhor
            // Se não tiver nada no campo de visão e vai ajudar o amigo
        }
        
        
        //System.out.println("[" + player.getId() + "] " +"MODO ATTACKER " +  player.targetPos.getIdPlayerPos()+ " " + player.targetPos.getX() + " " + player.targetPos.getY());
        
        // Como é que os amigos a quem manda mensagem de broadcast entram em modo attacker ?
        // Será que se devia movimentar na mesma independentemente de ter atualizado o target ou não?
        // 
        //
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
                //Criar mensagem a enviar
                ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
                // Destinatário
                m.addReceiver(p);
                
                
                Position newPos = selectPositionToMove(this.player.targetPos);
                // Direction que minimiza a distância ao target 
                //  pelo lado certo
                int direction = moveToTarget(newPos);

                m.setContent(this.player.getPos().getPosString() +  ":"  + direction); 

                //System.out.println("[" + player.getId() + "]" + " MOVE: " + direction);
                myAgent.send(m);
                player.waiting = true;
            }

        } catch (FIPAException e) {
            e.printStackTrace();
        }
      
	}


    public int enemyCostGlobal(Position enemyPlayer) {
        int cost = 0;
        int x = enemyPlayer.getX();
        int y = enemyPlayer.getY();

        Map<String,List<Position>> vfFriends = this.player.getFriendVFields();
        for(List<Position> lp : vfFriends.values()) {
            // A 1ª posição da lista corresponde à posição atual do agente
            Position p = lp.get(0);
            cost += calcDistance(x, y, p.getX(), p.getY());
        }
        return cost;
    }

    public double calcDistance(int x0, int y0, int x1, int y1) {
        //return Math.abs(x1-x0) + Math.abs(y1-y0);
        return Math.sqrt((y0 - y1) * (y0 - y1) + (x0 - x1) * (x0 - x1));
    }

    public int moveToTarget(Position enemyPlayer) { // Position p
        int res = -1;
        // Mexer com base na posição e não ver obstaculos
        boolean[] obstacles = directionObstacles(this.player.getPos());

        if (enemyPlayer.getY() < player.getPos().getY() && !obstacles[0]) {
            res = 0;
        }
        else if (enemyPlayer.getY() > player.getPos().getY() && !obstacles[1]) {
            res = 1;
            
        }
        
        if (res == -1) {
            if (enemyPlayer.getX() < player.getPos().getX() && !obstacles[2]) {
                res = 2;
            }
            else if (enemyPlayer.getX() > player.getPos().getX() && !obstacles[3]) {
                res = 3;
            }
        }
        
        
        List<Integer> directions = new ArrayList<>();
        if (res == -1 && !(player.getPos().getX() == enemyPlayer.getX() && player.getPos().getY() == enemyPlayer.getY())) {
            boolean[] dirObs = directionObstacles(player.getPos());

            for (int i = 0; i < dirObs.length; i++) {
                if (!dirObs[i]) {
                    directions.add(i);
                }
            }   
        
            Random r = new Random();
            int index = r.nextInt(directions.size());
            res = directions.get(index);
        }
        return res; 
    }

    



    public boolean[] directionObstacles(Position playerPos) {
        boolean[] obstacleList = new boolean[4];

        List<Position> vf = this.player.getVisionField();
        for(Position p : vf) {
            
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

        return obstacleList;
    }


    public Position selectPositionToMove(Position enemy) {
        
        Position res = new Position();
        double[] list = new double[4];
        int x = this.player.getPos().getX();
        int y = this.player.getPos().getY();

        boolean[] dirObs = directionObstacles(enemy);
        

        if(!dirObs[0] && enemy.getY()-1 >= 0)
            list[0] = calcDistance(x, y, enemy.getX(), enemy.getY()-1);
        else 
            list[0] = Integer.MAX_VALUE;//-1;

        if(!dirObs[1] && enemy.getY()+1 < player.mapSize)
            list[1] = calcDistance(x, y, enemy.getX(), enemy.getY()+1);
        else 
            list[1] = Integer.MAX_VALUE;//-1;

        if(!dirObs[2] && enemy.getX()-1 >= 0)
            list[2] = calcDistance(x, y, enemy.getX()-1, enemy.getY());
        else 
            list[2] = Integer.MAX_VALUE;//-1;

        if(!dirObs[3] && enemy.getX()+1 < player.mapSize)
            list[3] = calcDistance(x, y, enemy.getX()+1, enemy.getY());
        else 
            list[3] = Integer.MAX_VALUE;//-1;

            
        int min = 0;
        for (int index = 0; index < list.length; index++) {
            if(list[index] <= list[min]) {
                min = index;
            }
        }

        if(min == 0)
            res = new Position(enemy.getX(), enemy.getY()-1);
        else if(min==1)
            res = new Position(enemy.getX(), enemy.getY()+1);
        else if (min==2)
            res = new Position(enemy.getX()-1, enemy.getY());
        else if (min==3)
            res = new Position(enemy.getX()+1, enemy.getY());
        
        return res;
    }

}

