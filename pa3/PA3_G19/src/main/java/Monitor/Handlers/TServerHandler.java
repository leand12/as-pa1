/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Monitor.Handlers;

import Communication.ClientSocket;
import Monitor.Entities.Monitor;
import Monitor.GUI.GUI;
import static Utils.CodeMessages.*;
import Utils.HeartBeatMessage;
import Utils.HelloMessage;
import Utils.Message;
import Utils.RequestMessage;
import Utils.ServerStatusMessage;
import java.util.List;

/**
 *  Representation of the TServerHandler.
 */
public class TServerHandler extends Thread {

    /** The client socket */
    private final ClientSocket socket;

    /** The monitor instance */
    private final Monitor monitor;

    /**
     * Create an instance of TServerHandler.
     * @param socket       the client socket
     * @param monitor       the monitor instance
     */
    public TServerHandler(ClientSocket socket, Monitor monitor) {
        this.socket = socket;
        this.monitor = monitor;
    }

    /**
     * Handle messages received in a loop.
     */
    @Override
    public void run() {

        Object msg;
        while (true) {
            // keep listening to incoming messages
            if ((msg = socket.getMessage()) != null) {

                Message m = (Message) msg;
                switch (m.code()) {
                    case HELLO:
                        HelloMessage hm = (HelloMessage) msg;
                        
                        if (m.type().equals("LB")) {
                            /* Load Balancer HELLO */
                            if (monitor.hasPrimaryLB()) {
                                socket.sendMessage(new HelloMessage(0, null, "S"));
                            }
                            else{
                                socket.sendMessage(new HelloMessage(0, null, "P"));
                            }
                            monitor.setLBUp(hm.ID());
                        } else {
                            /* Server HELLO */
                            monitor.addServer(hm.ID());
                        }
                        
                        monitor.addSocket( hm.ID(), socket);
                        THeartBeatHandler hbsocket = new THeartBeatHandler(socket, monitor, hm.ID());
                        hbsocket.start();
                        
                        break;
                    case STATUS:
                        List<ServerStatusMessage> serversStatus = monitor.getServersStatus();
                        Message res = new Message(STATUS)
                                .serversStatus(serversStatus);
                        socket.sendMessage(res);
                        break;
                    case REQUEST:
                        monitor.forwardingRequest((RequestMessage)msg);
                        break;
                    case PROCESS:
                        monitor.processingRequest(m);
                        break;
                    case REPLY:
                        monitor.replyingRequest((RequestMessage)msg);
                        break;
                    case HEARTBEAT:
                        monitor.addHeartBeat(((HeartBeatMessage)msg).ID());
                        break;
                }
            }
            else{
                break;
            }

        }
    }
}
