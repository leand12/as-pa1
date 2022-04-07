/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Entities;
import HC.Monitors.MEVH;
import java.util.Random;


/**
 *
 * @author guids
 */
public class TNurse extends Thread{
    
    private MEVH mevh;
    private int evt;
    
    public TNurse(MEVH mevh, int evt){
        this.mevh = mevh;
        this.evt = evt;
    }
    
    public void assignDos(TPatient patient) throws InterruptedException{
        DoS dos = DoS.NONE;
        while(dos==DoS.NONE){
            dos = DoS.values()[new Random().nextInt(DoS.values().length)];
        } 
        
        // evaluation time
        Thread.sleep((int) Math.floor(Math.random() * evt));
        patient.setDos(dos);
    }
    
    @Override
    public void run(){
        mevh.assignNurse(this);
        while(true){}
    }
    
}
