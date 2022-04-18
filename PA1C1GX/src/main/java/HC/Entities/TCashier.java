package HC.Entities;

import HC.Monitors.IPYH_Cashier;


/**
 * The thread entity that accepts the payment.
 */
public class TCashier extends Thread {
    private volatile boolean threadSuspended;
    private  boolean exit = false;
    private final IPYH_Cashier pyh;

    public TCashier(IPYH_Cashier pyh) {
        this.pyh = pyh;
    }

    @Override
    public void run() {
        while (!exit) {
            pyh.receivePayment();
            synchronized(this) {
            while (threadSuspended)
                try {
                    wait();
                } catch (InterruptedException ex) {
                    System.err.println(ex);
                }
            }
        }
        
    }
    
    public synchronized void sus(){
        threadSuspended = true;
    }
    
    public synchronized void res(){
        threadSuspended = false;
        notify();
    }
    
    public void exit(){
        exit = true;
    }
}
