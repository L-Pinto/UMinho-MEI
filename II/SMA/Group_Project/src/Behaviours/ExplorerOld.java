package Behaviours;

import Agents.Player;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class ExplorerOld extends TickerBehaviour {
    int direction = 0;
    Player player = new Player();

    public ExplorerOld(Agent a, long period) {
        super(a, period);
        this.player = (Player) a;
    }

    protected void onTick(){
        try{
            //Pesquisar o serviço
            DFAgentDescription d = new DFAgentDescription();
            ServiceDescription s = new ServiceDescription();
            s.setType("Interface");
            d.addServices(s);
            
            //Enviar mensagem  para todos os agentes do serviço
            DFAgentDescription[] r = DFService.search(this.myAgent, d);

            for (int i = 0; i < r.length; ++i) {
                DFAgentDescription d2 = r[i];
                AID p = d2.getName();
                //Criar mensagem a enviar
                ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
                // Destinatário
                m.addReceiver(p);
                
                // Analise Campo Visão
                // decidir direção

                //direction = leaderChoice(explorador, campoVisao);
                direction = 0;
                
                m.setContent(this.player.getPos().getX() + " " + this.player.getPos().getY() + ":"  + direction);
                myAgent.send(m);
            }
        }
        catch (Exception e) {}
    }
}