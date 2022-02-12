import GUI.ApostadorObserver;
import PL.Controller;
import PL.IController;
import Exception.IsNullException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class backup {
    IController c;
    ApostadorObserver ao;
    boolean scripting;
    List<String> script_commands;
    int currentCommand;

    public backup(IController c) {
        this.c = c;
        ao = null;
        scripting = false;
        this.script_commands = new ArrayList<>();
        int currentCommand = 0;
    }

    public void paginator(List<String> strings){
        String input;
        boolean bool;
        int start = 0;
        int max = strings.size();
        int offset = 10;
        if (max > 0) {
            do {
                Scanner sc = new Scanner(System.in);
                for (int i = start; i < offset && i < max; i++) {
                    System.out.println(strings.get(i));
                }
                System.out.println(start+1 + "-" + (start+1 + (offset % max)) + " / "+ max + "\n+ -> " + offset + " para a frente" + "\n- -> " + offset + " para tras\nexit");
                if (scripting && currentCommand < script_commands.size()){
                    input = script_commands.get(currentCommand++);
                    System.out.println("\n" +input + "\n");
                }
                else
                    input = sc.nextLine();
            }
            while (!input.equals("exit"));
        }
        else
            System.out.println("Nada disponivel.");
    }

    public void main_menu() {
        String input;
        boolean bool;
        do {
            Scanner sc = new Scanner(System.in);
            //controller.validaRegisto(em, nome, user, pwd, data)
            System.out.println("\nregisterA email nome username pass birthDate" +
                    "\nloginA email pass" +
                    "\nloginG email pass" +
                    "\nscript command1,command2,..."+
                    "\nexit");
            if (scripting && currentCommand < script_commands.size()){
                input = script_commands.get(currentCommand++);
                System.out.println("\n" +input + "\n");
            }
            else
                input = sc.nextLine();
            String[] inputs = input.split(" ");
            if(inputs.length == 2){
                ;
            }
            if (inputs.length == 3) {
                if (inputs[0].equals("loginA")) {
                    bool = c.verificarLoginApostador(inputs[1], inputs[2]);
                    if (bool) {
                        try {
                            menu_apostador(inputs[1]);
                        } catch (IsNullException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        System.out.println("login falhado");
                }
                if (inputs[0].equals("loginG")) {
                    menu_gestor();
                }
            }
            if (inputs.length == 6){
                if(c.verificaData(inputs[5])){
                    if (!c.validaRegisto(inputs[1],inputs[2],inputs[3],inputs[4],inputs[5]))
                        System.out.println("Registo invalido.");
                }
                else
                    System.out.println("Precisa de ter mais que 18 anos.");
            }
            if (inputs[0].equals("script")) {
                String input2 = input.substring(7, input.length());
                this.script_commands = Arrays.stream(input2.split(",")).collect(Collectors.toList());
                this.scripting = true;
                this.currentCommand = 0;
            }

        }
        while (!input.equals("exit"));
    }

    public void menu_apostador(String emailApostador) throws IsNullException {
        String input;
        boolean bool;
            String idApostador = c.getNomeApostador(emailApostador);
        do {
            Scanner sc = new Scanner(System.in);
            System.out.println("\nlevantarDinheiro quantia moeda" +
                    "\ndepositarDinheiro quantia moeda" +
                    "\nregistarAposta resultado quantia moeda" +
                    "\nevents" +
                    "\nbets eventId" +
                    "\nverApostasRealizadas"+
                    "\nverEstatisticas" +
                    "\nresultado eventId"+
                    "\nsaldo" +
                    "\nexit");
            if (scripting && currentCommand < script_commands.size()){
                input = script_commands.get(currentCommand++);
                System.out.println("\n" +input + "\n");
            }
            else
                input = sc.nextLine();
            String[] inputs = input.split(" ");
            if(inputs.length == 1){
                if (input.equals("events")){
                    paginator(c.getEventos());
                }
                if (input.equals("verApostasRealizadas")){
                    paginator(c.getApostas(emailApostador));
                }
                if (input.equals("verEstatisticas")){
                    System.out.println(c.consultarEstatisticas(emailApostador));
                }
                if (input.equals("saldo")){
                    paginator(c.getSaldo(emailApostador));
                }
            }
            if (inputs.length == 2){
                if (inputs[0].equals("bets"))
                    paginator(c.getResultadosPossiveis(inputs[1]));
                if (inputs[0].equals("resultado")) {
                    paginator(c.getResultadoEvento(inputs[1]));
                }
            }
            if (inputs.length == 3) {
                if (inputs[0].equals("levantarDinheiro")) {
                    Float f = Float.parseFloat(inputs[1]);
                    boolean b = c.levantarSaldo(emailApostador, f, inputs[2]);
                    if (b)
                        System.out.println("Sucesso.");
                    else
                        System.out.println("Falha no levantamento de dinheiro.");
                }
                if (inputs[0].equals("depositarDinheiro")) {
                    Float f = Float.parseFloat(inputs[1]);
                    c.transferirSaldo(emailApostador, f, inputs[2]);
                    System.out.println("Sucesso.");
                }
            }
            if (inputs.length == 4){
                if (inputs[0].equals("registarAposta")) {
                    Float f = Float.parseFloat(inputs[2]);
                    c.registarAposta(emailApostador,f,inputs[1],inputs[3]);
                    System.out.println("Sucesso.");
                }
            }
        }
        while (!input.equals("exit"));
    }

    public void menu_gestor(){
        String input;
        boolean bool;
        do {
            Scanner sc = new Scanner(System.in);
            //String idParticipante,
            //String descricao, boolean ganhou, float odd, String estado, String idResultado, String tipoAposta, String idEvento, String participante
            //adicionarResultadoPossivel(String descricao, boolean ganhou, float odd, String estado, String idResultado, String tipoAposta, String idEvento,String participante);
            System.out.println("addParticipante nome" +
                    "\naddResultadoPossivel descricao odd tipoAposta idEvento participante" +
                    //(String descricao, float odd, String tipoAposta, String evento,String participante)
                    "\naddEvento desporto nome HH.mm.dd.MM.yyyy participante1;participante2,.. " +
                    "\nchangeOdd newOdd aposta" +
                    "\nfinalizarEvento evento" +
                    "\natualizarApostas evento novoEstado tipoApostas" +
                    "\nevents" +
                    "\nbets eventId" +
                    "\nresultado eventoId" +
                    "\natualizarResultado idEvento participante pontuacao"+
                    "\nmarcarResultado idResultado venceu(boolean)"+
                    //    public boolean validarMoeda(String nome, String token, Float ratio, Float imposto) {
                    "\naddMoeda nome token ration imposto"+
                    "\nexit");
            if (scripting && currentCommand < script_commands.size()){
                input = script_commands.get(currentCommand++);
                System.out.println("\n" +input + "\n");
            }
            else
                input = sc.nextLine();
            String[] inputs = input.split(" ");
            Arrays.stream(inputs).forEach(y -> System.out.println(y));
            System.out.println(inputs.length);
            if (inputs.length == 1) {
                if (input.equals("events")) {
                    paginator(c.getEventos());
                }
            }
            if (inputs.length == 2) {
                if (inputs[0].equals("addParticipante")) {
                    c.adicionarParticipante(inputs[1]);
                }
                if (inputs[0].equals("bets"))
                    paginator(c.getResultadosPossiveis(inputs[1]));
                if (inputs[0].equals("finalizarEvento")) {
                    c.finalizarEvento_(inputs[1],"suspenso");
                    System.out.println("Evento finalizado.");
                }
                if (inputs[0].equals("resultado")) {
                    paginator(c.getResultadoEvento(inputs[1]));
                }
            }
            if (inputs.length == 3) {
                if (inputs[0].equals("changeOdd")) {
                    c.atualizarOdd(inputs[2], inputs[1]);
                    System.out.println("Odd atualizada.");
                }
                //"\nmarcarResultado idResultado venceu(boolean)"+
                if (inputs[0].equals("marcarResultado")) {
                    //c.atualizarResultado(listView.getSelectionModel().getSelectedItems(), inputs[1],Boolean.parseBoolean(inputs[2]));
                    System.out.println("Resultado atualizado.");
                }
            }
            if (inputs.length == 4) {
                //"\natualizarResultado idEvento participante pontuacao"+
                if (inputs[0].equals("atualizarResultado")){
                    c.atualizarResultadoEvento(inputs[1],inputs[2],Integer.parseInt(inputs[3]));
                }
                if (inputs[0].equals("atualizarApostas")) {
                    //c.atualizarEvento(inputs[1], inputs[2], inputs[3]);
                    //atualizarApostas evento novoEstado tipoApostas
                    //c.atualizarEvento(inputs[1],);
                    System.out.println("Apostas atualizada.");
                }
            }
                if (inputs.length == 5) {
                    if (inputs[0].equals("addEvento")) {
                        // addEvento desporto nome data participante1,participante2,..  resultadopossivel1,resultadopossivel2,..."+
                        List<String> participantes = Arrays.stream(inputs[4].split(";")).collect(Collectors.toList());
                        c.adicionarEvento(inputs[2], participantes, LocalDateTime.parse(inputs[3], DateTimeFormatter.ofPattern("HH.mm.dd.MM.yyyy")), inputs[1], "aberto", new ArrayList<>());
                    }

                    //                    //    public boolean validarMoeda(String nome, String token, Float ratio, Float imposto) {
                    if(inputs[0].equals("addMoeda"))
                        this.c.validarMoeda(inputs[1],inputs[2],Float.parseFloat(inputs[3]),Float.parseFloat(inputs[4]));
                }
                if (inputs.length == 6){
                    if (inputs[0].equals("addResultadoPossivel")) {
                        //"\naddResultadoPossivel descricao odd tipoAposta idEvento participante" +
                        //                    //(String descricao, float odd, String tipoAposta, String evento,String participante)
                        bool = c.adicionarResultadoPossivel(inputs[1], Float.parseFloat(inputs[2]),  inputs[3], inputs[4], inputs[5]);
                        if (!bool)
                            System.out.println("Resultado possivel invalido.");
                    }
                }
            }
            while (!input.equals("exit")) ;
        }

    public static void main(String[] args){
        String s = "loginG a a\n" +
                "addParticipante mercedes\n" +
                "addParticipante redbull\n" +
                "addParticipante ferrari\n" +
                "addParticipante porto\n" +
                "addParticipante benfica\n" +
                "addParticipante sporting\n" +
                "addEvento formula1 Japan_GP 17.30.01.02.2022 ferrari;mercedes;redbull\n" +
                "addResultadoPossivel mercedes_podio 1.2 resultado 1 mercedes\n" +
                "addResultadoPossivel redbull_podio 2.1 resultado 1 redbull\n" +
                "addResultadoPossivel ferrariPodio 4.0 resultado 1 ferrari\n" +
                "addResultadoPossivel bandeira_vermelha 3 ocorrencia 1 NULL\n" +
                "addMoeda Euro € 1 0.0005\n" +
                "events\n" +
                "exit\n" +
                "bets 1\n" +
                "exit\n" +
                "changeOdd 8.0 3\n" +
                "exit\n" +
                "registerA teste@hah.com henrique sgggg pass 14/15/1987\n" +
                "registerA teste2@gmail.com qu1m quim pass 14/15/2009\n" +
                "registerA teste2@gmail.com qu1m quim pass 14/15/2000\n" +
                "loginA teste2@gmail.com pass\n" +
                "depositarDinheiro 15 Euro\n" +
                "registarAposta 3 12.50 Euro\n" +
                "registarAposta 1 1.0 Euro\n" +
                "exit\n" +
                "loginG a a\n" +
                "atualizarResultado 1 mercedes 1\n" +
                "atualizarResultado 1 redbull 2\n" +
                "atualizarResultado 1 ferrari 3\n" +
                "marcarResultado 1 true\n" +
                "finalizarEvento 1\n" +
                "exit\n" +
                "loginA teste2@gmail.com pass\n" +
                "saldo\n" +
                "exit\n" +
                "verEstatisticas";
        s = s.replace("\n",",");
        System.out.println(s);
        backup b = new backup(new Controller());
        b.main_menu();
    }
}