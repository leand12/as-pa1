package Client.Handlers;

import Client.GUI.GUI;
import Utils.Message;
import Utils.RequestMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
*
*   Class to handle server replies
*/
public class TReplyHandler extends Thread{
    
    private final Socket socket;
    private final GUI gui;
    
    
    /**
    *
    *   Class constructor, receives a socket connection as argument and the clients GUI
    */
    public TReplyHandler(Socket socket, GUI gui){
        this.socket = socket; 
        this.gui = gui;
    }
    
    @Override
    public void run(){
    
        ObjectOutputStream out;
        ObjectInputStream in;
        
        Message reply = null;
        
        while (true) {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                
                // keep listening to incoming messages
                if ((reply =(Message)in.readObject()) != null) {
                                        
                    switch(reply.code()){
                        
                        // Reply Message
                        case REPLY:
                        {
                           RequestMessage rm = (RequestMessage) reply;
                           gui.addReply(rm.requestID(), rm.serverID(), rm.nIterations(), 
                                   rm.pi(), rm.deadline());
                            
                        }
                        break;  
                    }
                }
                else{
                    break;
                }
            } catch (IOException ex) {
                System.err.println("Socket error");
            } catch (ClassNotFoundException ex) {
                System.err.println(ex);
            }
    
        }
        
    }
    
}
