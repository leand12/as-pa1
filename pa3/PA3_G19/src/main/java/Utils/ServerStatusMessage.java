package Utils;


import java.io.Serializable;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * The representation of the ServerStatusMessage.
 */
public class ServerStatusMessage implements Serializable {
    /** Server ID. */
    int serverID;
    /** Number of Requests. */
    int nRequests;
    /** Total number of iterations. */
    int totalNIterations;

    /**
     * Create an instance of ServerStatusMessage.
     * @param serverID      the server ID
     * @param nRequests     the number of requests
     * @param totalNIterations  the total number iterations
     */
    public ServerStatusMessage(int serverID, int nRequests, int totalNIterations) {
        this.serverID = serverID;
        this.nRequests = nRequests;
        this.totalNIterations = totalNIterations;
    }

    /* Getters */

    public int serverID() {
        return serverID;
    }
    
    public int nRequests() {
        return nRequests;
    }
    
    public int totalNIterations() {
        return totalNIterations;
    }
}
