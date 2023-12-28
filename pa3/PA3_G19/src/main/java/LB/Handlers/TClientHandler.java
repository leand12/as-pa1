package LB.Handlers;
import Communication.ClientSocket;
import Utils.CodeMessages;
import Utils.PrimaryMessage;
import Utils.HeartBeatMessage;
import Utils.HelloMessage;
import LB.Entities.LoadBalancer;
import Entities.Request;
import LB.Entities.Server;
import LB.GUI.GUI;
import Utils.Message;
import Utils.RequestMessage;

/**
 *
 * Handles client connections
 */
public class TClientHandler extends Thread{
    
    private  ClientSocket socket;
    private LoadBalancer lb;
    private GUI gui;
    
    /**
    *
    * Class constructor
    * 
     * @param socket
     * @param lb Load balancer entity
     * @param gui
    */
    public TClientHandler(ClientSocket socket, LoadBalancer lb, GUI gui){
        this.socket = socket; 
        this.lb = lb;
        this.gui = gui;
    }
    
    @Override
    public void run(){
    
        Object message;
        
        // Hello to Monitor
        socket.sendMessage(new HelloMessage(lb.getLoadBalencerID(),"LB", null));
        
        while (true) {
            // keep listening to incoming messages
            if ((message = (Object) socket.getMessage()) != null) {

                Message m = (Message) message;
                switch(m.code()){
                    
                    // HB message -> HB|LBID
                    case HEARTBEAT:
                        {
                            socket.sendMessage(new HeartBeatMessage(lb.getLoadBalencerID()));
                        }
                        break;
                    // LB HELLO message from monitor -> HELLO|[P or S]
                    case HELLO:
                        {
                            HelloMessage hello = (HelloMessage) message;
                            if(hello.function().equals("P")){                     
                                // setPrimary
                                lb.setPrimary();    
                                gui.setPrimary();
                            }
                        }
                        break;
                    // SERVERs DETAILS
                    case STATUS:
                        {
                            // Update serverStatus                  
                            lb.updateServers(m.serversStatus());
                            
                            // Choose server and  forward request
                            Request r = lb.getFirstRequest();
                            Server s = lb.getBestServer();
                            
                            RequestMessage rm = new RequestMessage(CodeMessages.REQUEST, r.clientID(),
                            r.requestID(), s.getServerId(), 0, r.nIterations(), 0, r.deadline(),0);
                            
                            
                            // forward to monitor
                            socket.sendMessage(rm);
                            
                            // forward to server
                            s.getSocket().sendMessage(rm);
                            
                            // Update GUI
                            gui.addRequest(r.clientID(), r.requestID(), s.getServerId(),
                                    r.nIterations(), r.deadline());
                        }
                        break;
                    // Primary LoadBalancer is down need to start as primary
                    case PRIMARY:  
                        {
                            PrimaryMessage pm = (PrimaryMessage) message;
                            // update Port
                            lb.setLoadBalencerID(pm.ID());
                            
                            // setPrimary
                            lb.setPrimary();
                            gui.setPrimary();
                        }
                        break;
                    case REQUEST: {

                        RequestMessage rm = (RequestMessage) message;

                        // Forward request to monitor
                        socket.sendMessage(new Message(CodeMessages.STATUS));

                        // Add request
                        lb.addRequest(new Request(rm.clientID(), rm.requestID(), rm.serverID(), rm.nIterations(), rm.deadline()));

                    }
                }
            }
            else{
                break;
            }
    
        }
        
    }
    
    /**
    *
    * set new socket connection
    * 
     * @param s
    */
    public void setNewSocket(ClientSocket s)
    {
        this.socket = s;
    }
    
}
