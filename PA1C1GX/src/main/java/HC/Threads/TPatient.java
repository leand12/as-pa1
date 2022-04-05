/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Threads;

import HC.Monitors.METH;

/**
 *
 * @author guids
 */
public class TPatient extends Thread{
    
    private METH ETH; // entrance hall
    private int ETN // entrance hall number
    
    public TPatient(METH ETH){
        this.ETH = ETH;
    }
    
    public boolean isAdult(){
        return true; 
    }
    
    public int getETN(){
        return ETN;
    }
    
    @Override
    public void run(){
        int id = this.ETH.put(this);
    }
    
}
