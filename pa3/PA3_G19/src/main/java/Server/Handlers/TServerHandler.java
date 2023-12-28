/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.Handlers;

import Communication.ClientSocket;
import static Utils.CodeMessages.*;
import Utils.Message;
import Utils.RequestMessage;
import Entities.Request;
import Server.Entities.Server;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The representation of the TServerHandler.
 */
public class TServerHandler extends Thread {

    /** The client socket */
    private final ClientSocket socket;
    /** The server instance */
    private final Server server;

    /**
     * Create an instance of the TServerHandler.
     * @param socket    the client socket
     * @param server    the server instance
     */
    public TServerHandler(ClientSocket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    /**
     * Run thread and handle messages received in client socket.
     */
    @Override
    public void run() {

        Object msg;
        while (true) {
            // keep listening to incoming messages
            if ((msg = socket.getMessage()) != null) {
                
                Message m = (Message) msg;
                switch (m.code()) {
                    case REQUEST: {
                        
                        try {
                            RequestMessage rm = (RequestMessage) msg;
                            server.newRequest(new Request(rm.clientID(), rm.requestID(), rm.serverID(),rm.nIterations(), rm.deadline()));
                        } catch (InterruptedException ex) {
                            System.err.println(ex);
                        }
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
