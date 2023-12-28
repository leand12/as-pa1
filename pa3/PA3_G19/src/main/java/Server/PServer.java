/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import Communication.ClientSocket;
import Server.Communication.TServerSocket;
import Server.Entities.Server;
import Server.Handlers.TClientHandler;
import Server.GUI.ConfigurationGUI;
import Server.GUI.GUI;
import java.io.IOException;

/**
 * Server process class
 */
public class PServer {
    
    private static int portNumber;
    private static int monitorPortNumber;
    private static boolean started = false;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        // Configuration GUI
        ConfigurationGUI configurationGUI =  new ConfigurationGUI();
        configurationGUI.setVisible(true);
        
       // Wait for configuration GUI
       while(!started){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
       }
       
       // Start main GUI
       GUI gui = new GUI();
       gui.setServerInformation(portNumber, monitorPortNumber);
       gui.setVisible(true);
       

       ClientSocket monitorSocket = new ClientSocket(monitorPortNumber, "127.0.0.1");
       
       // Handle monitor messages
       TClientHandler monitorHandler = new TClientHandler(monitorSocket, portNumber);
       monitorHandler.start();
       
       Server server = new Server(monitorSocket, gui);
       
       // Create Server
       TServerSocket serverSocket = new TServerSocket(portNumber, server);
       serverSocket.start();
    }

    /**
     * Start server and its GUI after configuration.
     * @param portNumber            the port number
     * @param monitorPortNumber    th monitor port number
     */
    public static void startServer(int portNumber, int monitorPortNumber){
        PServer.portNumber = portNumber;
        PServer.monitorPortNumber = monitorPortNumber;
        PServer.started = true;
    }

    /**
     * Terminate server process and GUI.
     */
    public static void terminate(){
        System.exit(0);
    }
}
