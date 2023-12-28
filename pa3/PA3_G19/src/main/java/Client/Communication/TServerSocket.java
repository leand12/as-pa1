package Client.Communication;
import Client.GUI.GUI;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import Client.Handlers.TReplyHandler;

/**
 *
 * Client Server socket
 */
public class TServerSocket extends Thread{
    
    private final ServerSocket serverSocket;
    private final GUI gui;
    
    
    /**
    *
    *  Class constructor, receives as argument the socket port and client GUI
    */
    public TServerSocket(int portNumber, GUI gui) throws IOException{     
        this.serverSocket = new ServerSocket(portNumber);
        this.gui = gui;
    }
    
    @Override
    public void run(){
    
        while(true){
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            
                TReplyHandler socket = new TReplyHandler(clientSocket, gui);
                socket.start();
            
            } catch (IOException ex) {
                System.err.println("Socket error");
            }
        }
    }
    
    
}
