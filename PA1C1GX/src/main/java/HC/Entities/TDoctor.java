package HC.Entities;

import HC.Monitors.IMDH_Doctor;

public class TDoctor extends Thread {
    private volatile boolean threadSuspended;
    private boolean exit = false;
    private static int id = 0;
    private final IMDH_Doctor mdh;
    private final int roomDedicated;

    public TDoctor(IMDH_Doctor mdh) {
        this.mdh = mdh;
        roomDedicated = id++;
    }

    @Override
    public void run() {
        while (!exit) {
            mdh.examinePatient(roomDedicated);
        }
        synchronized(this) {
            while (threadSuspended)
                try {
                    wait();
                } catch (InterruptedException ex) {
                    System.err.println(ex);
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
    
    public static void resetId(){
        id=0;
    }
    
    public void exit(){
        exit = true;
    }
}
