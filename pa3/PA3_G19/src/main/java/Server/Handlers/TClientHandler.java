/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.Handlers;
import Communication.ClientSocket;
import Utils.HelloMessage;
import Utils.HeartBeatMessage;
import Utils.Message;
/**
 * The representation of the TClientHandler.
 */
public class TClientHandler extends Thread{

    /** The client socket */
    private final ClientSocket socket;
    /** The server ID */
    private final int serverID;

    /**
     * Create an instance of the TClientHandler.
     * @param socket    the server instance
     * @param serverID  the server ID
     */
    public TClientHandler(ClientSocket socket, int serverID){
        this.socket = socket; 
        this.serverID = serverID;
    }

    /**
     * Run thread and handle the client socket messages.
     */
    @Override
    public void run(){
    
        Object inputMessage;
        
        // Hello to Monitor
        socket.sendMessage(new HelloMessage(serverID, "S", null));
        
        while (true) {
            // keep listening to incoming messages
            if ((inputMessage = socket.getMessage()) != null) {
                
                Message m = (Message) inputMessage;
                switch(m.code()){
                    
                    // HB message -> HB|ServerID
                    case HEARTBEAT:
                        {
                            socket.sendMessage(new HeartBeatMessage(serverID));
                        }
                        break;
                    
                }
            }
            else{
                break;
            }
    
        }
    }
    
}
