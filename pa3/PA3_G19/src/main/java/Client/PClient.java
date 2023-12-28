package Client;

import Client.GUI.ConfigurationGUI;
import Client.GUI.GUI;
import Communication.ClientSocket;
import Client.Communication.TServerSocket;

/**
 *
 * Client process class
 */
import java.io.IOException;

/**
 *
 * Client process class
 */
public class PClient {
    
    private static int portNumber;
    private static int loadBalancerPortNumber;
    private static boolean  started = false;
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
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
       
       // Connect to Load Balancer
       ClientSocket lbSocket = new ClientSocket(loadBalancerPortNumber, "127.0.0.1");
  
       // Start main GUI
       GUI gui = new GUI();
       gui.setClientInformation(portNumber, loadBalancerPortNumber);
       gui.setLoadBalancerSocket(lbSocket);
       gui.setVisible(true);
       
       // Create Client Server to receive requests
       TServerSocket serverSocket = new TServerSocket(portNumber, gui);
       serverSocket.start();
       
       
       
    }
    
    /**
     * set client port number and load balancer port
     */
    public static void startClient(int portNumber, int loadBalancerPortNumber){
        PClient.portNumber = portNumber;
        PClient.loadBalancerPortNumber = loadBalancerPortNumber;
        PClient.started = true;
    }
    
    /**
     * terminate client process
     */
    public static void terminate(){
        System.exit(0);
    }
}
