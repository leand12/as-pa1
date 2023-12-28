package LB.Entities;

import Entities.Request;
import java.util.concurrent.locks.ReentrantLock;
import Communication.ClientSocket;
import Utils.ServerStatusMessage;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;


/**
 *
 * Load Balancer entity class
 */
public class LoadBalancer {
    
    private boolean isPrimary = false;
    
    private final HashMap<Integer,Server> servers;
    private Server bestServer; 
    
    private final LinkedList<Request> requests;
    
    private int lbId;
    
    private final ReentrantLock rl;
    
    /**
    *
    * Load Balancer  class constructor
    * @param lbId the load balancers ID
    */
    public  LoadBalancer(int lbId){
        this.lbId = lbId;
        this.rl = new ReentrantLock();
        this.requests = new LinkedList<>();
        this.servers = new HashMap<>();
    }
    
    /**
    *
    * Set Load balancer as primary
    * 
    */
    public void setPrimary(){
        this.isPrimary = true;    
    }
    
    /**
    *
    * see if Load balancer is primary
    * 
    * @return boolean isPrimary
    */
    public boolean getPrimary(){
        return this.isPrimary;  
    }
    
    /**
    *
    * get Load balancer iD
    * 
    * @return int ID
    */
    public int getLoadBalencerID(){
        return lbId;
    }
    
    /**
    *
    * Set Load balancer ID
    * 
    * @param lbId, the load balancer ID
    */
    public void setLoadBalencerID(int lbId){
        this.lbId = lbId;
    }   

    /**
    *
    * See if the load balancer is connected to server
    * 
     * @param serverId
     * @return boolean
    */
    public boolean hasServer(int serverId){
        return servers.containsKey(serverId);
    }
    
    /**
    *
    * Get a server entity
    * 
     * @param serverId
     * @return server object
    */
    public Server getServer(int serverId){
        
        return servers.get(serverId);
    }
    
    /**
    *
    * Make a new server connection
    * 
    * @param serverId
    * @return server object
    */
    public Server addServer(int serverId){
        
        // new server, needs to create a new connection
        // Fist loadbalancer should connect to the monitor at the respective port
        
        ClientSocket serverSocket = new ClientSocket(serverId, "127.0.0.1");
        
        Server s = new Server(serverId, serverSocket);
        
        servers.put(serverId, s);
        
        return s;
    }
    
    /**
    *
    * Update servers 
    * 
    * @param status list of servers to be updated
    */
    public void updateServers(List<ServerStatusMessage> status){
        
        int bNI = 100; // max ni slhoud not be more than 20
        int bNJ = 10; // servers cannot have more that 5 requests
        
        // delete and update unexisting servers
        for(int serverID : servers.keySet()){
            boolean exists = false;
            for(ServerStatusMessage s : status){
                if(s.serverID() == serverID){
                    System.out.println(String.valueOf(s.totalNIterations()));
                    System.out.println(String.valueOf(s.nRequests()));
                    Server si = getServer(serverID);
                    si.setNI(s.totalNIterations());
                    si.setNJobs(s.nRequests());
                    exists = true;
                    if(bNJ>s.nRequests() || (bNJ==s.nRequests() && bNI>s.totalNIterations())){
                    
                        bestServer = si;
                        bNJ = s.nRequests();
                        bNI = s.totalNIterations();
                    }
                    break;
                }
            }
            
            if(!exists){
                deleteServer(serverID);
            }
            
        }
        
        // add new ones
        for(ServerStatusMessage s : status){
            if(!servers.containsKey(s.serverID())){
                System.out.println(String.valueOf(s.totalNIterations()));
                System.out.println(String.valueOf(s.nRequests()));
                Server si = addServer(s.serverID());
                si.setNI(s.totalNIterations());
                si.setNJobs(s.nRequests());
                if(bNJ>s.nRequests() || (bNJ==s.nRequests() && bNI>s.totalNIterations())){
                    
                    bestServer = si;
                    bNJ = s.nRequests();
                    bNI = s.totalNIterations();
                }
            }
        }
    
       
    }
    
    /**
    *
    * Get best server to send request
    * 
    * @return server object
    */
    public Server getBestServer(){
        return bestServer;
    }
    
    /**
    *
    * add a new Request
    * 
     * @param request
    */
    public void addRequest(Request request){
        rl.lock();
        try {
            requests.add(request);
        } finally {
            rl.unlock();
        }
        
    }
    

    public Request getFirstRequest(){
        Request request;
        
        rl.lock();
        try {
            request = requests.pollFirst();
        } finally {
            rl.unlock();
        }
        return request;
    }
    
    /**
    *
    * Delete server
    * 
    * @param serverId
    */
    public void deleteServer(int serverId){
        servers.remove(serverId);
    }  
    

}
