/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Entities;

import HC.Monitors.*;

/**
 *
 * @author guids
 */
public class TPatient extends Thread {
    private final METH eth;         // entrance hall
    private final MEVH evh;         // evaluation hall
    private final MWTH wth;         // waiting hall
    private final MMDH mdh;         // medical hall
    private final MPYH pyh;         // payment hall

    private int ETN;            // entrance hall number
    private boolean isAdult;
    private DoS dos = DoS.NONE;            // degree of severity

    public TPatient(boolean isAdult, METH eth, MEVH evh, MWTH wth, MMDH mdh, MPYH pyh) {
        this.isAdult = isAdult;
        this.eth = eth;
        this.evh = evh;
        this.wth = wth;
        this.mdh = mdh;
        this.pyh = pyh;
    }
    
    public boolean isAdult(){
        return isAdult;
    }

    public DoS getDos() { return dos; }

    public void setDos(DoS dos) { this.dos = dos; }
    
    public int getETN(){
        return ETN;
    }

    public void setETN(int ETN) { this.ETN = ETN; }
    
    @Override
    public void run(){
        this.eth.put(this);
        this.evh.put(this);
    }

    @Override
    public String toString() {
        return String.format("%s%02d%s", isAdult ? "A" : "C", ETN, dos);
    }
}
