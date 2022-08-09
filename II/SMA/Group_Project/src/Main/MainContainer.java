package Main;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class MainContainer {
    Runtime rt;
    ContainerController container;

    public void initMainContainerInPlatform(String host, String port, String containerName) {
        // Get the JADE runtime interface (singleton)
        this.rt = Runtime.instance();
        // Create a Profile, where the launch arguments are stored
        Profile prof = new ProfileImpl();
        prof.setParameter(Profile.MAIN_HOST, host);
        prof.setParameter(Profile.MAIN_PORT, port);
        prof.setParameter(Profile.CONTAINER_NAME, containerName);
        prof.setParameter(Profile.MAIN, "true");
        prof.setParameter(Profile.GUI, "true");
        // create a main agent container
        this.container = rt.createMainContainer(prof);
        rt.setCloseVM(true);
    }

    public ContainerController initContainerInPlatform(String host, String port, String containerName) {
        // Get the JADE runtime interface (singleton)
        this.rt = Runtime.instance();
        // Create a Profile, where the launch arguments are stored
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, containerName);
        profile.setParameter(Profile.MAIN_HOST, host);
        profile.setParameter(Profile.MAIN_PORT, port);
        // create a non-main agent container
        ContainerController container = rt.createAgentContainer(profile);
        return container;
    }

    public void startAgentInPlatform(String name, String classpath, Object[] args) {
        try {
            AgentController ac = container.createNewAgent(name, classpath, args);
            ac.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAgentInPlatformContainer(ContainerController input_container, String name, String classpath, Object[] args) {
        try {
            AgentController ac = input_container.createNewAgent(name, classpath, args);
            ac.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MainContainer a = new MainContainer();

        // Main Container Creation
        a.initMainContainerInPlatform("localhost", "9885", "MainContainer");

        // Containers Creation - Create 2 different containers (separated environments) inside the Main container
        Object[] args_input = new Object[] { "Red_team", "Blue_team"};
        ContainerController newcontainer1 = a.initContainerInPlatform("localhost", "9887", args_input[0].toString());
        ContainerController newcontainer2 = a.initContainerInPlatform("localhost", "9888", args_input[1].toString());
        

        // Criar agente Leader no container Main
        //a.startAgentInPlatform("Blue_leader", "Agents.Leader", new Object[] {});
        //a.startAgentInPlatform("Red_leader", "Agents.Leader", new Object[] {});

        // Inicializar Mapa
        Object[][] mapa = new Object[10][3];
        int count = 0;
        int mapSize = 35;

        boolean[][] positions = new boolean[mapSize][mapSize];


        // Criar Equipa Vermelho
        for (int i = 0; i < 5; i++) {
            Object[] aargs1 = new Object[5];
            int x;
            int y;
            while(true) {
                Random r = new Random();
                x = r.nextInt(mapSize);
                y = r.nextInt(mapSize);
                if (!positions[x][y]) {
                    positions[x][y] = true;
                    break;
                }
            }

            aargs1[0] = (int) x; // x
            aargs1[1] = (int) y;  // y
            aargs1[2] = (int) 2;  // equipa
            aargs1[3] = (int) 1; // é borda
            aargs1[4] = (int) i;
            mapa[count] = aargs1;
            count++;

            a.startAgentInPlatformContainer(newcontainer1, "R" + i,  "Agents.Player", aargs1);
        }

        // Criar Equipa Azul
        int id = 0;
        for (int i = 5; i > 0; i--) {
            Object[] aargs2 = new Object[5];

            int x;
            int y;
            while(true) {
                Random r = new Random();
                x = r.nextInt(mapSize);
                y = r.nextInt(mapSize);
                if (!positions[x][y]) {
                    positions[x][y] = true;
                    break;
                }
            }

            aargs2[0] = (int) x;
            aargs2[1] = (int) y;
            aargs2[2] = (int) 1;
            aargs2[3] = (int) 1;  // é borda
            aargs2[4] = (int) id; 
            mapa[count] = aargs2;
            count++;
            a.startAgentInPlatformContainer(newcontainer2, "B" + id, "Agents.Player", aargs2);
            id++;
        }

        // Criar agente Interface no container Main
        a.startAgentInPlatform("Interface","Agents.Interface", mapa);

        // Criar agente Mapa no container Main
        // a.startAgentInPlatform("Map", "Classes.Map", new Object[] {});
    }
}

