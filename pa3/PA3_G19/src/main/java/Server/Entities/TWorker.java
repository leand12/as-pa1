/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.Entities;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Representation of the worker thread.
 */
public class TWorker extends Thread{

    /** The value of PI */
    public static final double PI = 3.1415926589793;
    /** The worker ID */
    private final int ID;
    /** The server instance */
    private final Server server;
    /** The lock */
    private final ReentrantLock rl;
    /** A lock work condition */
    private final Condition cWork;
    /** Whether the worker has work */
    private boolean hasWork = false;
    /** The number of iterations */
    private int ni;

    /**
     * Create an instance of TWorker.
     * @param ID    the worker ID
     * @param server    the server instance
     */
    public TWorker(int ID, Server server){
        this.ID = ID;
        this.server = server;
        rl = new ReentrantLock();
        cWork = rl.newCondition();

    }

    /**
     * Run thread to process request.
     */
    @Override
    public void run() {
        while(true){
        
            try {
                rl.lock();
                while(!hasWork)
                    cWork.await();
                
                // Has work, will calculate pi 
                Thread.sleep(ni*5000);
                
                double pi = Math.round(PI* Math.pow(10,ni))/Math.pow(10, ni);
                
                this.hasWork = false;
                
                server.result(ID, pi);
                
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
            finally{
                rl.unlock();
            }
        }
       
    }

    /**
     * Handle new request to process.
     * @param ni    the number of iterations
     * @throws InterruptedException an exception
     */
    public void newWork(int ni) throws InterruptedException{
        try{
            rl.lock();
            this.ni = ni;
            this.hasWork = true;
            cWork.signal();
        }
        finally{
            rl.unlock();
            
        }
        
    }
}
