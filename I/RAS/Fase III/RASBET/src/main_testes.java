import BL.IRasBetFacade;
import BL.RasBetModel;
import DAO.ApostadorDAO;
import DAO.EventoDAO;
import DAO.MoedaDAO;
import DAO.ResultadoPossivelDAO;
import Entidades.*;
import PL.Controller;
import PL.IController;
import Exception.IsNullException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class main_testes {
    public static void main(String[] args) throws IsNullException {
        int i;
        IRasBetFacade rbf = new RasBetModel();
        //inicializar controller
        IController c = new Controller();

        //teste 1: adicionar apostador
        //UC02 - Registo
        //UC02 - Registo
        //UC02 - Registo
        //UC02 - Registo
        //UC02 - Registo
        //UC02 - Registo
        //UC02 - Registo
        //UC02 - Registo
        //UC02 - Registo
        //UC02 - Registo
        //UC02 - Registo
        //UC02 - Registo

        i = 1;
        if (i == 1) {
            c.validaRegisto("h@gmail.com", "hhhh", "uuuuu", "asdasdasd", "17/07/2000");
            c.validaRegisto("2apostas@gmail.com", "qqqqq", "2a", "adda", "17/07/2000");
            c.validaRegisto("2apostas_2@gmail.com", "qqqqq", "2a", "adasdasd", "17/07/2000");
            System.out.println("Fase 1 - adicionar apostador");
        }

        //observer
        Apostador apostador = null;
        try {
            apostador = rbf.getApostador("h@gmail.com");
        } catch (IsNullException e) {
            e.printStackTrace();
        }
        //rbf.addObserver(ao);


        //UC14 - Adicionar Evento
        //UC14 - Adicionar Evento
        //UC14 - Adicionar Evento
//UC14 - Adicionar Evento
//UC14 - Adicionar Evento
//UC14 - Adicionar Evento

        Evento e = new Evento(EventoDAO.getInstance().generateUniqueId(), "futebol", "A vs B", "aberto", "", "2007-12-03T10:15:30");
        Evento e1 = new Evento(EventoDAO.getInstance().generateUniqueId(), "basquetebol", "C1 vs C2", "aberto", "", "2007-12-03T10:15:30");



        //gerar resultados possiveis
        int t = Integer.parseInt(ResultadoPossivelDAO.getInstance().generateUniqueId());
        List<ResultadoPossivel> l_rp = new ArrayList<>();
        List<ResultadoPossivel> l_rp2 = new ArrayList<>();
        //rp e
        ResultadoPossivel rp = new ResultadoPossivel("EQUIPA CASA GANHA",
                false, 2, "Aberto", String.valueOf(t++), "resultado", e.getIdEvento(), "Equipa a");
        ResultadoPossivel rp2 = new ResultadoPossivel("EQUIPA CASA PERDE",
                false, 2, "Aberto", String.valueOf(t++), "resultado", e.getIdEvento(), "Equipa a");
        ResultadoPossivel rp3 = new ResultadoPossivel("EQUIPA CASA EMPATA",
                false, 2, "Aberto", String.valueOf(t++), "resultado", e.getIdEvento(), "Equipa a");
        ResultadoPossivel rp4 = new ResultadoPossivel(">2 golos",
                false, 2, "Aberto", String.valueOf(t++), "metricas", e.getIdEvento(), "Equipa b");
        ResultadoPossivel rp5 = new ResultadoPossivel("5º lugar",
                false, 2, "Aberto", String.valueOf(t++), "metricas", "-1", "metricas");

        ResultadoPossivel rp6 = new ResultadoPossivel("EQUIPA CASA DESISTE",
                false, 2, "Aberto", String.valueOf(t++), "resultado", e1.getIdEvento(), "Equipa a");
        ResultadoPossivel rp7 = new ResultadoPossivel("EQUIPA CASA GANHA",
                false, 2, "Aberto", String.valueOf(t++), "resultado", "2", "Equipa a");
        ResultadoPossivel rp8 = new ResultadoPossivel("EQUIPA CASA PERDE",
                false, 2, "Aberto", String.valueOf(t++), "resultado", "2", "Equipa a");
        ResultadoPossivel rp9 = new ResultadoPossivel("EQUIPA CASA EMPATA",
                false, 2, "Aberto", String.valueOf(t++), "resultado", "2", "Equipa a");
        l_rp.add(rp);
        l_rp.add(rp2);
        l_rp.add(rp3);
        l_rp.add(rp4);
        l_rp.add(rp5);
        l_rp.add(rp6);

        l_rp2.add(rp7);
        l_rp2.add(rp8);
        l_rp2.add(rp9);
        //participantes
        Participante p1 = new Participante("Equipa a", false, 0);
        Participante p2 = new Participante("Equipa b", false, 0);
        List<Participante> participantes = new ArrayList<>();
        participantes.add(p1);
        participantes.add(p2);
        //adicionar eventos e participantes//adicionar eventos e participantes
        //        //adicionar eventos e participantes
        //        //adicionar eventos e participantes
        //        //adicionar eventos e participantes
        //
        i = 0;
        if (i == 1) {
            l_rp.forEach(y -> rbf.addResultadoPossivel(y));
            l_rp2.forEach(y -> rbf.addResultadoPossivel(y));
            participantes.forEach(y -> rbf.addParticipante(y));
        }
        //a assumir q o nome do participante e o seu id
        List<String> r = participantes.stream().map(y -> y.toString()).collect(Collectors.toList());

        //adicionar evento
        i = 0;
        if (i == 1) {
            //rbf.adicionarEvento("A vs B", participantes.stream().map(y -> y.getIdParticipante()).collect(Collectors.toList()),
              //      e.getData(), "futebol", "aberto", l_rp.stream().map(y -> y.getIdResultado()).collect(Collectors.toList()));
            //rbf.adicionarEvento("A vs B", participantes.stream().map(y -> y.getIdParticipante()).collect(Collectors.toList()),
              //      e1.getData(), e1.getDesporto(), "aberto", l_rp2.stream().map(y -> y.getIdResultado()).collect(Collectors.toList()));
        }



        //UC04 - Consultar Eventos
        //UC04 - Consultar Eventos
        //UC04 - Consultar Eventos
        //UC04 - Consultar Eventos

        List<Evento> eventos = rbf.listarEventos();
        System.out.println("UC04 - Consultar Eventos");
        eventos.forEach(y -> System.out.println(y.toString()));


        //UC09 - Depositar Saldo
        //UC09 - Depositar Saldo
        //UC09 - Depositar Saldo
        //UC09 - Depositar Saldo
        i = 1;
        if (i == 1){
        System.out.println("UC09 - Depositar Saldo\nSaldo antes: " + rbf.getApostador("h@gmail.com").getSaldo().saldo());
        apostador = rbf.getApostador("h@gmail.com");
        System.out.println(apostador.toString());
        //isnerir moeda
            Moeda m = new Moeda();
            MoedaDAO.getInstance().put(m);
            rbf.transferirSaldo(apostador.getIdApostador(),15,"Euro");
        //rbf.depositarSaldo(apostador.getIdApostador(), 15);
            Apostador b = ApostadorDAO.getInstance().get(apostador.getIdApostador());
        System.out.println("Saldo depois: " + b.getSaldo().saldo());
        }

    //UC12: Fazer Aposta
    //UC12: Fazer Aposta
    //UC12: Fazer Aposta
    //UC12: Fazer Aposta
    i = 0;
if (i == 1){
        System.out.println("Aposta de 20 euros?");
        System.out.println("Aposta valida? : " + rbf.validaTransferencia(apostador.getEmail(),20.0f,"Euro"));

    System.out.println("Aposta de 13 euros?");
    System.out.println("Aposta valida? : "+rbf.validaTransferencia(apostador.getEmail(),13.0f,"Euro"));
    rbf.registaAposta(apostador.getEmail(),"1",15,"Euro");
}

       // UC13: Atualizar Odds
        // UC13: Atualizar Odds
        // UC13: Atualizar Odds
        // UC13: Atualizar Odds
        i = 0;
        if (i == 1) {
            ResultadoPossivel p = ResultadoPossivelDAO.getInstance().get("1");
            System.out.println("Resultado possivel antes:\n" + p.toString());
            rbf.atualizarOdd("1", p.getOdd() + 1.0f);
            System.out.println("Resultado possivel depois:\n" + ResultadoPossivelDAO.getInstance().get("1").toString());
        }


        //UC15 - Atualizar Aposta
        //UC15 - Atualizar Aposta
        //UC15 - Atualizar Aposta
        //UC15 - Atualizar Aposta
        //UC15 - Atualizar Aposta


        //observer
        /*
        try {
            apostador = rbf.getApostador("h@gmail.com");
        } catch (IsNullException ee) {
            ee.printStackTrace();
        }
        ApostadorObserver ao = new ApostadorObserver(apostador);
        Subject.getInstance().addApostador(ao);*/

        i = 0;
        if (i == 1) {
            rbf.atualizarEstadoApostas("1", "aberto", "resultado");
            System.out.println("Antes:");
            List<ResultadoPossivel> rps = ResultadoPossivelDAO.getInstance().searchEvento("1");
            ResultadoPossivelDAO.getInstance().searchEvento("1")
                    .stream().filter(y -> y.getTipoAposta().equals("resultado")).forEach(y -> System.out.println("Estado :" + y.getEstado()));
            rbf.atualizarResultadoPossivel("1",true);
            rbf.atualizarEstadoApostas("1", "fechado", "resultado");
            System.out.println("Depois:");
            ResultadoPossivelDAO.getInstance().searchEvento("1")
                    .stream().filter(y -> y.getTipoAposta().equals("resultado")).forEach(y -> System.out.println("Estado :" + y.getEstado()));
        }
        i = 0;
        //alterar resultado




    }




}
