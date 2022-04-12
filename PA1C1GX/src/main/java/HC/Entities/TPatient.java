/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Entities;

import HC.Data.DoS;
import HC.Monitors.*;

/**
 *
 * @author guids
 */
public class TPatient extends Thread {
    private final IMETH_Patient eth;         // entrance hall
    private final IMEVH_Patient evh;         // evaluation hall
    private final IMWTH_Patient wth;         // waiting hall
    private final IMMDH_Patient mdh;         // medical hall
    private final IMPYH_Patient pyh;         // payment hall

    private int ETN;            // entrance hall number
    private boolean isAdult;
    private DoS dos = DoS.NONE;            // degree of severity

    public TPatient(boolean isAdult, IMETH_Patient eth, IMEVH_Patient evh, IMWTH_Patient wth, IMMDH_Patient mdh,
                    IMPYH_Patient pyh) {
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
        this.eth.enterPatient(this);
        this.evh.enterPatient(this);
    }

    @Override
    public String toString() {
        return String.format("%s%02d%s", isAdult ? "A" : "C", ETN, dos);
    }
}
