package Utils;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * The representation of the RequestMessage.
 */
public class RequestMessage extends Message {
    
    /** Client ID. */
    int clientID;
    /** Request ID. */
    int requestID;
    /** Server ID. */
    int serverID;
    /** Number of requests. */
    int nRequests;
    /** Number of iterations. */
    int nIterations;
    /** Priority in number of seconds to handle the request. */
    int deadline;
    /** Value of pi calculated. */
    double pi;
    /** Request code. */
    int requestCode;

    /**
     * Create an instance of RequestMessage.
     * @param code  the code message
     * @param clientID  the client ID
     * @param requestID the request ID
     * @param serverID  the server ID
     * @param nRequests the number of requests
     * @param nIterations   the number of iterations
     * @param pi    the pi value
     * @param deadline  the deadline
     * @param requestCode   the request code
     */
    public RequestMessage(CodeMessages code, 
            int clientID, int requestID, int serverID, int nRequests, 
            int nIterations, double pi, int deadline, int requestCode) {
        super(code);
        this.clientID = clientID;
        this.requestID = requestID;
        this.serverID = serverID;
        this.nRequests = nRequests;
        this.nIterations = nIterations;
        this.pi = pi;
        this.deadline = deadline;
        this.requestCode = requestCode;
    }

    /* Getters and setters */
    
     public Message clientID(int clientID) {
        this.clientID = clientID;
        return this;
    }

    public int clientID() {
        return clientID;
    }
    
    public Message requestID(int requestID) {
        this.requestID = requestID;
        return this;
    }

    public int requestID() {
        return requestID;
    }
    
    public Message serverID(int serverID) {
        this.serverID = serverID;
        return this;
    }

    public int serverID() {
        return serverID;
    }
    
    public Message nRequests(int nRequests) {
        this.nRequests = nRequests;
        return this;
    }

    public int nRequests() {
        return nRequests;
    }
    
    public Message nIterations(int nIterations) {
        this.nIterations = nIterations;
        return this;
    }
    
    public int nIterations() {
        return nIterations;
    }
    
    public Message pi(double pi) {
        this.pi = pi;
        return this;
    }
    
    public double pi() {
        return pi;
    }
    
    public Message deadline(int deadline) {
        this.deadline = deadline;
        return this;
    }
    
    public int deadline() {
        return deadline;
    }
    
    public Message requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }
    
    public int requestCode() {
        return requestCode;
    }
}
