package LB.Entities;

import Communication.ClientSocket;

/**
*
* Server entity class
* 
*/
public class Server {
    
    private final int serverID;
    private int njobs = 0;
    private int ni = 0;
    private ClientSocket socket;
    
    /**
    *
    * class constructor 
    * 
    * @param serverID
    * @param socket
    */
    public Server(int serverID, ClientSocket socket){
        this.serverID = serverID; 
        this.socket = socket;
    }
    
    /**
    *
    * Set number of requests
    * 
     * @param njobs
    */
    public void setNJobs(int njobs){
        this.njobs = njobs;
    }
    
    /**
    *
    * get server connection socket
    * 
    * @return socket
    */
    public ClientSocket getSocket(){
        return socket;
    }
    
    /**
    *
    * get server ID
    * 
    * @return ID
    */
    public int getServerId(){
        return serverID;
    }
    
    /**
    *
    * get number of requests
    * 
    * @return number of requests
    */
    public int getNJobs(){
        return njobs;
    }
    
    /**
    *
    * set server total number of iterations ID
     * @param ni
    */
    public void setNI(int ni){
        this.ni = ni;
    }
    
    /**
    *
    * get number of iterations
    * 
    * @return number of iterations
    */
    public int getNI(){
        return ni;
    }
}
