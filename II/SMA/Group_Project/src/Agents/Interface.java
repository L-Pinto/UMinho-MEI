package Agents;

import Classes.Position;
import Exceptions.DeadPlayer;
import Classes.GameData;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.*;


import java.awt.*;

public class Interface extends Agent {
    GameData gameData;


    @Override
    protected void setup() {
        super.setup();
        
        registerService();
        JFrame obj= new JFrame();
        
        // Posições Iniciais
        Object[] args = getArguments();

        // Cast para inteiros
        int[][] newArgs = new int[10][5];
        if (args != null) {
            // 10 Players
			for(int i=0;i<args.length;i++) {
                Object[] ar = (Object[]) args[i];
                newArgs[i][0] = (int) ar[0];
                newArgs[i][1] = (int) ar[1];
                newArgs[i][2] = (int) ar[2];
                newArgs[i][3] = (int) ar[3];
                newArgs[i][4] = (int) ar[4];
            }
		}
        
        int mapSize = 35;
        int blockSize = 22;
        int length = mapSize*blockSize;

        gameData = new GameData(mapSize,blockSize,newArgs); //playerUI

        // Inicializar Player Vision Fields
        Map<String, List<Position>> vFields = gameData.getVisionFieldList(newArgs);

        // Enviar Vision Fields a todos os players
        informPlayers(vFields); 
        
        obj.setBounds(10, 10, length + 140, length + 40);
        obj.setTitle("Tank 2D") ;
        obj.setBackground(Color.gray);
        obj.setResizable(false);

        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.add(gameData);
        obj.setVisible(true);


        /* Behaviours */
        this.addBehaviour(new UpdateView(this));
        //this.addBehaviour(new SendMessage(this,5000));
        
    }

    public void informPlayers(Map<String, List<Position>> vFields) {
        // Campos de Visão Iniciais
        for(Entry<String, List<Position>> entry : vFields.entrySet()) {
            AID p = new AID(entry.getKey(), AID.ISLOCALNAME);
            ACLMessage m = new ACLMessage(ACLMessage.CONFIRM);
            m.addReceiver(p);
            String campoVisao = getFieldViewString(entry.getValue());
            
            m.setContent(campoVisao);
            send(m);
        }
    }
    
    
    public String getFieldViewString(List<Position> entry) {
        String campoVisao = "";
        if (entry.size() > 0) {
            for (Position position : entry) {
                campoVisao = campoVisao + position.getPosString() + ";";
            }
        }
        else {
            campoVisao = "-";
        }
        return campoVisao;
    }


    private void registerService() {
        // Register Agent in Directory Facilitator (Yellow Pages)
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getLocalName());
        sd.setType("Interface");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private class UpdateView extends CyclicBehaviour {
    
        public UpdateView(Agent a) {
            super(a);
        }

        public void action() {  
            ACLMessage msg = receive();
            if(msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                String idAgent = msg.getSender().getLocalName();
                String[] message = msg.getContent().split(":");
                // Direção do movimento
                int direction = Integer.valueOf(message[1]);
                
                // Posicao antiga
                String position = message[0];
                Position pOld = new Position(position);
                
                
                // Nova Posição
                Position p;
                String content = "";
                try {
                    p = gameData.movePlayer(pOld,idAgent,direction);
                    Set<String> deadPlayers = gameData.checkDeadPlayers(p);
                    if (deadPlayers.size() > 0) {
                        myAgent.addBehaviour(new KillPlayer(myAgent,deadPlayers));
                    }
                    List<Position> visionField = gameData.getVisionField(p);
                    String campoVisao = "/" + getFieldViewString(visionField);
                    content = p.getPosString() + campoVisao;
                    
                    
                }
                catch (DeadPlayer d) {
                    
                }
                // atualizar campo visão
                if (!content.equals("")) {
                    ACLMessage resp = msg.createReply();
                    resp.setContent(content);
                    resp.setPerformative(ACLMessage.CONFIRM);
                    myAgent.send(resp);
                }
            }
            else {
                block();
            }     
        }
    }

    private class KillPlayer extends OneShotBehaviour {
        Set<String> deadPlayers;

        public KillPlayer(Agent agent, Set<String> a) {
            super(agent);
            this.deadPlayers = a;
        }

        @Override
        public void action() {
            for(String s : deadPlayers) {
                DFAgentDescription d = new DFAgentDescription();
                ServiceDescription service = new ServiceDescription();
                service.setType("B");
                d.addServices(service);
                try {
                    for (int k = 0; k < 2; k++) {

                        if (k == 1) {
                            service.setType("R");
                            d.addServices(service);
                        }

                        // Manda o seu target para os amigos
                        DFAgentDescription[] r = DFService.search(this.myAgent, d);
                        for (int i = 0; i < r.length; ++i) {
                            DFAgentDescription d2 = r[i];
                            AID p = d2.getName();
                            //Criar mensagem a enviar
                            ACLMessage m = new ACLMessage(ACLMessage.CANCEL);
                            
                                
                            // Envia o target + custo (somatório das distâncias)
                            m.setContent(s);
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
        }
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
