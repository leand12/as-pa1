/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Entities;

import HC.Monitors.METH;

/**
 *
 * @author guids
 */
public class TPatient extends Thread {
    
    private METH ETH;           // entrance hall
    private int ETN;            // entrance hall number
    private boolean isAdult;
    private DoS dos = DoS.NONE;            // degree of severity

    public TPatient(boolean isAdult, METH ETH) {
        this.isAdult = isAdult;
        this.ETH = ETH;
    }
    
    public boolean isAdult(){
        return isAdult;
    }

    public DoS getDos() { return dos; }

    public void setDos(DoS dos) { this.dos = dos; }
    
    public int getETN(){
        return ETN;
    }
    
    @Override
    public void run(){

        int id = this.ETH.put(this);
    }
    
}
