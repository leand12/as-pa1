/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Entities;
import HC.Monitors.MEVH;

/**
 *
 * @author guids
 */
public class TNurse extends Thread{
    
    private MEVH mevh;
    
    public TNurse(MEVH mevh){
        this.mevh = mevh;
    }
    
    @Override
    public void run(){
        // TODO
    }
    
}
