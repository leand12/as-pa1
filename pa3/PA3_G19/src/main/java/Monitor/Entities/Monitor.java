/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Monitor.Entities;

import Communication.ClientSocket;
import Monitor.GUI.GUI;
import Monitor.Handlers.THeartBeatHandler;
import Utils.Message;
import Utils.RequestMessage;
import Utils.ServerStatusMessage;
import Utils.PrimaryMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Representation of Monitor entity.
 */
public class Monitor {

    /** GUI interface */
    private final GUI gui;

    /** Status of every server by ID */
    private final HashMap<Integer, ServerRequestsInfo> serversInfo;

    /** List of requests of every server by ID */
    private final HashMap<Integer, HashMap<Integer, RequestMessage>> serversRequests;

    /** Requests information of every client by ID */
    private final HashMap<Integer, ClientRequestsInfo> clientsInfo;

    /** List of all requests handled by the Load Balancer */
    private final List<RequestMessage> loadBalancerRequests;

    /** Whether or not the primary Load Balancer is working */
    private final int[] loadBalancers;

    /** Heartbeat list of ports */
    private final List<Integer> heartBeatMessages;

    /** Sockets map. */
    private final HashMap<Integer, ClientSocket> sockets;

    /** Heartbeat threshold (ms). */
    private final int heartbeatThreashold;

    /** Lock */
    private final ReentrantLock rl;

    /** Create instance of Monitor */
    public Monitor(GUI gui, int heartbeatThreashold) {
        this.gui = gui;
        serversInfo = new HashMap<>();
        serversRequests = new HashMap<>();
        clientsInfo = new HashMap<>();
        loadBalancerRequests = new ArrayList<>();
        rl = new ReentrantLock();
        this.heartbeatThreashold = heartbeatThreashold;
        heartBeatMessages = new ArrayList<>();
        loadBalancers = new int[2];
        sockets = new HashMap<>();
    }

    /**
     * Whether the primary Load Balancer is alive.
     */
    public boolean hasPrimaryLB() {
        return loadBalancers[0] != 0;
    }

    /**
     * Ack primary Load Balancer.
     * @param ID    the Load Balancer port/ID
     */
    public void setLBUp(int ID) {
        if (loadBalancers[0] == 0) {
            loadBalancers[0] = ID;
            gui.setLBStatus("UP");
        } else {
            loadBalancers[1] = ID;
        }
    }

    /**
     * Add a new socket to the socket map.
     * @param ID    the socket port
     * @param cs    the client socket
     */
    public void addSocket(int ID, ClientSocket cs) {
        sockets.put(ID, cs);
    }

    /**
     * Handle a Server or Load Balancer going down.
     * @param ID    the port/ID of the entity that just went down
     */
    public void setDown(int ID) {

        // Server Down
        if (serversInfo.containsKey(ID)) {
            serversInfo.remove(ID);
            for (RequestMessage rm : new ArrayList<>(serversRequests.get(ID).values())) {
                sockets.get(loadBalancers[0]).sendMessage(rm);
            }
            serversRequests.remove(ID);
            gui.setServerDown(ID);
        } else {
            if (loadBalancers[0] == ID) {
                gui.setLBStatus("DOWN");

                PrimaryMessage pm = new PrimaryMessage(ID);
                sockets.get(loadBalancers[0]).closeSocket();
                sockets.get(loadBalancers[1]).sendMessage(pm);
                sockets.put(loadBalancers[0], sockets.get(loadBalancers[1]));
                sockets.remove(loadBalancers[1]);
                loadBalancers[1] = 0;

                THeartBeatHandler hbsocket = new THeartBeatHandler(sockets.get(loadBalancers[0]), this, ID);
                hbsocket.start();
                gui.setLBStatus("UP");
            } else {
                loadBalancers[1] = 0;
                sockets.remove(ID);
            }
        }
    }

    /**
     * Add a new server available to receive requests from the LB.
     *
     * @param port the server ID/port
     */
    public void addServer(int port) {
        try {
            rl.lock();
            serversInfo.put(port, new ServerRequestsInfo());
            serversRequests.put(port, new HashMap<>());
            gui.addServerToTable(port);
        } finally {
            rl.unlock();
        }
    }

    /**
     * Return the server requests to the GUI.
     * @param serverID  the server ID
     * @return      the list of server requests
     */
    public List<RequestMessage> getServerRequests(int serverID) {
        try {
            rl.lock();
            return new ArrayList<>(serversRequests.get(serverID).values());
        } finally {
            rl.unlock();
        }
    }

    /**
     * Lock the monitor status and update it according to the forwarded request.
     *
     * @param request the forwarded request
     */
    public void forwardingRequest(RequestMessage request) {
        int clientID = request.clientID();
        int serverID = request.serverID();
        int requestID = request.requestID();

        try {
            rl.lock();
            serversInfo.putIfAbsent(serverID, new ServerRequestsInfo());
            serversInfo.get(serverID).receiveRequest(request.nIterations());

            serversRequests.putIfAbsent(serverID, new HashMap<>());
            serversRequests.get(serverID).put(requestID, request);

            clientsInfo.putIfAbsent(clientID, new ClientRequestsInfo(clientID));
            clientsInfo.get(clientID).forwardingRequest();

            loadBalancerRequests.add(request);

            gui.addRequestToLBTable(request);
            gui.addRequestToTableRequest(request);
            gui.setNRequestsServer(serverID, serversRequests.get(serverID).size());
        } finally {
            rl.unlock();
        }
    }

    /**
     * Lock the monitor status and update it according to the message.
     *
     * @param msg the message containing the request's client ID
     */
    public void processingRequest(Message msg) {
        int clientID = msg.port();

        try {
            rl.lock();
            clientsInfo.putIfAbsent(clientID, new ClientRequestsInfo(clientID));
            clientsInfo.get(clientID).processingRequest();
        } finally {
            rl.unlock();
        }
    }

    /**
     * Lock the monitor status and update it according to the replied message.
     *
     * @param request the replied message
     */
    public void replyingRequest(RequestMessage request) {
        int clientID = request.clientID();
        int serverID = request.serverID();
        int requestID = request.requestID();

        try {
            rl.lock();
            serversInfo.get(serverID).completeRequest(request.nIterations());

            ClientRequestsInfo requestInfo = clientsInfo.get(clientID);
            if (request.requestCode() == 2) {
                requestInfo.replyingRequest();
            } else {
                requestInfo.rejectingRequest();
            }

            serversRequests.get(serverID).remove(requestID);

            gui.setNRequestsServer(serverID, serversRequests.get(serverID).size());
        } finally {
            rl.unlock();
        }
    }

    /**
     * Build the status message list of all requests for every server.
     *
     * @return the status message list
     */
    public List<ServerStatusMessage> getServersStatus() {
        List<ServerStatusMessage> serversStatus = new ArrayList<>();

        try {
            rl.lock();
            for (Map.Entry<Integer, ServerRequestsInfo> entry : serversInfo.entrySet()) {
                int serverID = entry.getKey();
                int nRequests = entry.getValue().nRequests;
                int totalIterations = entry.getValue().totalIterations;
                serversStatus.add(new ServerStatusMessage(serverID, nRequests, totalIterations));
            }
            return serversStatus;

        } finally {
            rl.unlock();
        }
    }

    public void addHeartBeat(int ID) {
        heartBeatMessages.add(ID);
    }

    public void removeHeartBeat(int ID) {
        heartBeatMessages.remove(Integer.valueOf(ID));
    }

    public boolean hasHeartBeat(int ID) {
        return heartBeatMessages.contains(ID);
    }

    public int getHeartbeatThreashold() {
        return heartbeatThreashold;
    }

    /**
     * Representation of the Client's requests information
     */
    class ClientRequestsInfo {

        private int clientID;
        private int pending = 0;
        private int beingProcessed = 0;
        private int rejected = 0;
        private int processed = 0;

        ClientRequestsInfo(int clientID) {
            this.clientID = clientID;
        }

        void forwardingRequest() {
            pending++;
            update();
        }

        void processingRequest() {
            pending--;
            beingProcessed++;
            update();
        }

        void rejectingRequest() {
            pending--;
            rejected++;
            update();
        }

        void replyingRequest() {
            beingProcessed--;
            processed++;
            update();
        }

        void update() {
            gui.updateClientTable(clientID, pending, beingProcessed,
                    rejected, processed);
        }
    }

    /**
     * Representation of the Server's requests information
     */
    class ServerRequestsInfo {

        int nRequests = 0;
        int totalIterations = 0;

        void receiveRequest(int iterations) {
            nRequests++;
            totalIterations += iterations;
        }

        void completeRequest(int iterations) {
            nRequests--;
            totalIterations -= iterations;
        }
    }

}
