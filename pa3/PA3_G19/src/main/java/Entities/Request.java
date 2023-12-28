package Entities;

/**
*
*   Class request, contains request information
*/
public class Request {
    
    private final int clientID;
    private final int requestID;
    private  int serverID;
    private final int nIterations;
    private final int deadline;
    private double pi;
    
    
    /**
    *
    *   Class constructor, receives information to create request
    */
    public Request(int clientID, int requestID, int serverID, int nIterations, int deadline){
    
        this.clientID = clientID;
        this.requestID = requestID;
        this.serverID = serverID;
        this.nIterations = nIterations;
        this.deadline = deadline;
        
    }
    
    /**
    *
    *   get client ID
    *   @return clientID
    */
    public int clientID() {
        return clientID;
    }
    
    /**
    *
    *   get request ID 
    *   @return requestID
    */
    public int requestID() {
        return requestID;
    }
    
    /**
    *
    *   get server ID 
    *   @param serverID
    */
    public void serverID(int serverID) {
        this.serverID = serverID;

    }

    /**
    *
    *   get server ID 
    *   @return serverID
    */
    public int serverID() {
        return serverID;
    }
    
    /**
    *
    *   get number of iterations
    *   @return nIterations
    */
    public int nIterations() {
        return nIterations;
    }
    
    /**
    *
    *   get deadline
    *   @return deadline
    */
    public int deadline() {
        return deadline;
    }
    
    /**
    *
    *   get pi
    *   @return pi
    */
    public double pi(){
        return pi;
    }
    
    /**
    *
    *   get pi 
    *   @param pi
    */
    public void pi(double pi){
        this.pi = pi;
    }
}
