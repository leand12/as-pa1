/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Entities;

import HC.Monitors.IEVH_Nurse;

/**
 * The thread entity that evaluates patients regarding their DoS (degree of severity).
 *
 * @author guids
 */
public class TNurse extends Thread {
    
    private volatile boolean threadSuspended;
    private boolean exit = false;
    private static int id = 0;
    private final IEVH_Nurse evh;
    private final int roomDedicated;

    public TNurse(IEVH_Nurse evh) {
        this.evh = evh;
        roomDedicated = id++;
    }

    @Override
    public void run() {
        while (!exit) {
            evh.evaluatePatient(roomDedicated);
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
