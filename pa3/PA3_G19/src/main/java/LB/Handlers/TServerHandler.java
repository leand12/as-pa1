package LB.Handlers;

import Utils.CodeMessages;
import Communication.ClientSocket;
import LB.Entities.LoadBalancer;
import Entities.Request;
import Utils.Message;
import Utils.RequestMessage;



/**
*
* Handle server connections
* 
*/
public class TServerHandler extends Thread {

    private final ClientSocket socket;
    private final ClientSocket monitorSocket;
    private final LoadBalancer lb;

    /**
    *
    * Class constructor
    * 
     * @param socket
     * @param monitorSocket
     * @param lb Load Balancer entity
    */
    public TServerHandler(ClientSocket socket, ClientSocket monitorSocket, LoadBalancer lb) {
        this.socket = socket;
        this.monitorSocket = monitorSocket;
        this.lb = lb;
    }

    @Override
    public void run() {
        
        
        Object message = null;
        while (true) {
            // keep listening to incoming messages
            if ((message = socket.getMessage()) != null) {

                Message m = (Message) message;
                switch (m.code()) {

                    // Request message -> REQUEST | client id | request id | 00 | 01 | number of iterations | 00 | deadline |
                    case REQUEST: {

                        RequestMessage rm = (RequestMessage) message;

                        // Forward request to monitor
                        monitorSocket.sendMessage(new Message(CodeMessages.STATUS));

                        // Add request
                        lb.addRequest(new Request(rm.clientID(), rm.requestID(), rm.serverID(), rm.nIterations(), rm.deadline()));

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
