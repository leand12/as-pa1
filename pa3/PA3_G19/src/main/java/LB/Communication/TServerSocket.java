package LB.Communication;
import LB.Entities.LoadBalancer;
import Communication.ClientSocket;
import java.io.IOException;
import java.net.ServerSocket;
import LB.Handlers.TServerHandler;

/**
 *
 * Server socket Load Balancer class
 */
public class TServerSocket extends Thread{
    
    private final ServerSocket serverSocket;
    private final ClientSocket monitorSocket;
    private final LoadBalancer lb;
    
    /**
    *
    * Class constructor 
    * @param portNumber the lB id 
    * @param monitorSocket
    * @param lb Load balancer entity
    * @throws java.io.IOException
    */
    public TServerSocket(int portNumber, ClientSocket monitorSocket, LoadBalancer lb) throws IOException{  
        this.serverSocket = new ServerSocket(portNumber);
        this.monitorSocket = monitorSocket;
        this.lb = lb;
    }
    
    @Override
    public void run(){
    
        while(true){
            ClientSocket clientSocket;
            try {
                clientSocket = new ClientSocket(serverSocket.accept());
            
                TServerHandler socket = new TServerHandler(clientSocket, monitorSocket, lb);
                socket.start();
            
            } catch (IOException ex) {
                System.err.println("Socket error");
            }
        }
    }
    
    
}
