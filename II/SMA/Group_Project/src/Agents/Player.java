package Agents;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import Behaviours.*;
import Classes.Position;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;


public class Player extends Agent {
    /* Informações do player*/
    private String id;
    private String teamId;
    private Position pos;
    public Position targetPos; //(indicates where the robot is going next)
    public int targetCost = Integer.MAX_VALUE;
    private int score;
    public int mapSize = 35;
    public boolean waiting;
    public int helper;          // 0-> no help    1-> help

    private int direction; // -1 = parado 0 = cima 1 = baixo 2 = esquerda 3 = direita
    private List<Position> visionField;
    private List<String> deadPlayers = new ArrayList<>();

    /* Informações dos colegas de equipa */
    // IdPlayer -> Posição Atual + Campo de Visão
    // A primeira posição da lista é a posição do jogador amigo, o resto são inimigos ou parede
    private Map<String,List<Position>> friendVFields;   

    @Override
    protected void setup() {
        super.setup();
        // Identificador
        id = getAID().getLocalName();
        teamId = id.substring(0,1); 
        score = 0;

        friendVFields = new HashMap<>();
        visionField = new ArrayList<>();

        // Posições Iniciais
        Object[] args = getArguments();

        if (args != null) {
            pos = new Position((int) args[0], (int) args[1]);
            pos.setAvailable((int) args[2]);
            if((int) args[3] == 1){
                pos.setBorder(true);
            }
            else {
                pos.setBorder(false);
            }
            
            pos.setIdPlayerPos(id);
		}

        // Register Agent in Directory Facilitator (Yellow Pages)
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getLocalName());
        sd.setType(teamId);
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }  
        
        addBehaviour(new Movement(this,500));
        addBehaviour(new ReceiveMessages());
        addBehaviour(new SendPosition(this, 300));
        

    }

    
    
    // Behaviours
    public class ReceiveMessages extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            
            // Recebe a nova posição + vision field da interface 
            // E faz broadcast
            if (msg != null &&  msg.getPerformative() == ACLMessage.CONFIRM) {
                
                //System.out.println("player " + pos.toString());
                if (!msg.getContent().equals("Invalid Move")) {
                    String[] posVision = msg.getContent().split("/");
                    /* Set da nova posição */
                    String visionField = posVision[0];
                    if (posVision.length > 1) {
                        visionField = posVision[1];
                        pos = new Position(posVision[0]);
                        
                    }
                    setVisionField(visionField);
              
                    /*envia em broadcast a sua posição e o campo de visão*/
                    //sendVisionField(visionField);
                    // Está feito num behaviour à parte
                }
                waiting = false;
            }
            // Recebe mensagem do amigo para atacar o target dele 
            else if(msg != null && msg.getPerformative() == ACLMessage.INFORM_IF) {
                String[] newTarget = msg.getContent().split("/");
                int newCost = Integer.valueOf(newTarget[1]);
                
                
                Position newEnemy = new Position(newTarget[0]);
                targetPos = newEnemy;
                targetCost = newCost;
                helper = 1; //modo ataque
                //if (teamId.equals("R"))      
                //    System.out.println("[" + id + "] " +"BROADCAST " +  targetPos.getIdPlayerPos()+ " " + targetPos.getX() + " " + targetPos.getY());
            } 
            //recebe a mensagem do broadcast + set dos campos de visão dos colegas de equipa
            else if(msg != null &&  msg.getPerformative() == ACLMessage.PROPAGATE) {
                setFriendVFields(msg);
            }
            else if(msg != null &&  msg.getPerformative() == ACLMessage.CANCEL) {
                String idDead = msg.getContent();
                // Se for o próprio... falece.
                if (idDead.equals(id)) {
                    helper = 0;
                    targetCost = Integer.MAX_VALUE;

                    targetPos = null;
                    //System.out.println("MORRIII");
                    takeDown();
                }
                // Se forem amigos... choram.
                // Se forem inimigos... festejam !
                else {
                    deadPlayers.add(idDead);
                    if ( targetPos != null && deadPlayers.contains(targetPos.getIdPlayerPos())) {
                        helper = 0;
                        targetCost = Integer.MAX_VALUE;
                        targetPos = null;
                    }
                }
            }
            else {
                block();
            }
        }
    }
    
    public class SendPosition extends TickerBehaviour {
		public SendPosition(Agent a, long period) {
			super(a, period);
		}
		@Override
		protected void onTick() {
			// envia em broadcast a sua posição e o campo de visão
            // !!!!!!!!! RETIRAR O SEU PRÓPRIO ID
            sendVisionField(getFieldViewString(visionField));
		}
    } 

    public String getFieldViewString(List<Position> entry) {
        String campoVisao = "";
        if (entry.size() > 0) {
            for (Position position : entry) {
                campoVisao = campoVisao + position.getPosString() + ";";
            }
        }
        return campoVisao;
    }

    public void sendVisionField(String vf) {
        /*envia em broadcast a sua posição e o campo de visão*/
        //Pesquisar o serviço
        DFAgentDescription d = new DFAgentDescription();
        ServiceDescription s = new ServiceDescription();
        s.setType(teamId);
        d.addServices(s);
        
        //Enviar mensagem  para todos os agentes do serviço
        try {  
            DFAgentDescription[] r = DFService.search(this, d);
            for (int i = 0; i < r.length; ++i) {
                DFAgentDescription d2 = r[i];
                if (d2.getName().getLocalName() != id && !deadPlayers.contains(d2.getName().getLocalName())) {
                    AID p = d2.getName();
                    ACLMessage msgFriends = new ACLMessage(ACLMessage.PROPAGATE);
                    msgFriends.addReceiver(p);
                    msgFriends.setContent(pos.getPosString() + "/"  + vf);
                    this.send(msgFriends);
                }
            }
        } 
        catch (FIPAException e) {
            e.printStackTrace();
        }
    }


    public class Movement extends TickerBehaviour {
        
        public Movement(Agent a, long period) {
            super(a, period); 
        }

        public void onTick() {

            Position enemy = null;

            if(!waiting) {
                int amigos = 0;
                int inimigos = 0;
                
                if (targetPos != null && deadPlayers.contains(targetPos.getIdPlayerPos())) {
                    targetCost = Integer.MAX_VALUE;
                    targetPos = null;
                    helper = 0;
                }


                // Bordas, Amigos e Inimigos
                for (Position p : visionField) {
                    // Caso tenha um INIMIGO no campo de visão
                    if ((p.getAvailable() == 1 && teamId.equals("R")) ||  (p.getAvailable() == 2 && teamId.equals("B"))) {
                        score--;
                        inimigos++;
                        enemy = p;
                        if (targetPos != null && p.getIdPlayerPos().equals(targetPos.getIdPlayerPos())){
                            targetPos = p;
                        }
                    }
                    // Conta AMIGUINHAS 
                    else if ((p.getAvailable() == 2 && teamId.equals("R")) ||  (p.getAvailable() == 1 && teamId.equals("B"))) {
                        score++;
                        amigos++;
                    }
                }
                // defesa  amigos == 0 && inimigos > 1 
                if(inimigos == 1 || helper == 1) {       
                    myAgent.addBehaviour(new Attacker(myAgent,enemy));
                } 
                // Encontraste um inimigo
                // Ajudas alguém que encontrou inimigo
                // ataque
                else if(inimigos > 1 && inimigos > amigos) {
                    //targetPos = null;
                    //targetCost = Integer.MAX_VALUE;
                    //helper = 0;
                    /*if (teamId.equals("R"))
                        System.out.println("[" + id + "] " +"MODO DEFENDER ");*/
                    myAgent.addBehaviour(new Defender(myAgent));
                }
                // explorer
                else {
                    //targetPos = null;
                    //targetCost = Integer.MAX_VALUE;
                    //helper = 0;
                    /*if (teamId.equals("R"))
                        System.out.println("[" + id + "] " +"MODO EXPLORER ");*/
                    myAgent.addBehaviour(new Explorer(myAgent));
                }
                // explorar -> quando não tem inimigos no campo de visão
                //myAgent.addBehaviour(new Explorer(myAgent, 100));
            }
        }
    }



    /* ---------- Getters and Setters -------------*/
    
    public void setVisionField(String visionfiString) {
        visionField = new ArrayList<>();
        if (!visionfiString.equals("-")) {
            String[] visionFieldString = visionfiString.split(";");
            //Cada Posição é ou Agente ou Borda ou ambos
            for (String field : visionFieldString) {
                Position pos = new Position(field);
                visionField.add(pos);
            } 
        }
        
    }

    public void setFriendVFields(ACLMessage msg) {
        String idFriend = msg.getSender().getLocalName();
        String[] info = msg.getContent().split("/");
        String posFriend = info[0];
        List<Position> vFieldPos = new ArrayList<>();
        Position pos = new Position(posFriend);
        vFieldPos.add(pos);
        if (info.length > 1) {
            String[] visionFieldString = info[1].split(";");
            for (String field : visionFieldString) {
                Position p = new Position(field);
                vFieldPos.add(p);
            }
        }
        friendVFields.put(idFriend, vFieldPos);
    }

    
    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeamId() {
        return this.teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public Position getDirection() {
        return this.targetPos;
    }

    public void setDirection(Position direction) {
        this.targetPos = direction;
    }

    public int getTarget() {
        return this.direction;
    }

    public void setTarget(int target) {
        this.direction = target;
    }


    public List<Position> getVisionField() {
        return this.visionField;
    }


    public Map<String,List<Position>> getFriendVFields() {
        return this.friendVFields;
    }

    
    


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", teamId='" + getTeamId() + "'" +
            ", pos='" + getPos() + "'" +
            ", direction='" + getDirection() + "'" +
            ", target='" + getTarget() + "'" +
            "}";
    }


    @Override
    protected void takeDown() {
        super.takeDown();
        //System.out.println(this.getLocalName() + " a morrer...");
        // De-register Agent from DF before killing it
        try {
            DFService.deregister(this);
            System.out.println(this.getLocalName() + " a morrer...");
        }
        catch (Exception e) {
        }
    }

}




