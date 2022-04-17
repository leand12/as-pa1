/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Entities;

import HC.Data.EDoS;
import HC.Data.ERoom_CC;
import HC.Monitors.*;

import static HC.Data.ERoom_CC.*;

/**
 *
 * @author guids
 */
public class TPatient extends Thread {
    private final ICCH_Patient cch;         // call center hall
    private final IETH_Patient eth;         // entrance hall
    private final IEVH_Patient evh;         // evaluation hall
    private final IWTH_Patient wth;         // waiting hall
    private final IMDH_Patient mdh;         // medical hall
    private final IPYH_Patient pyh;         // payment hall

    private int NN;            // entrance hall number
    private boolean isAdult;
    private EDoS dos = EDoS.NONE;            // degree of severity

    public TPatient(boolean isAdult, ICCH_Patient cch, IETH_Patient eth, IEVH_Patient evh, IWTH_Patient wth,
                    IMDH_Patient mdh, IPYH_Patient pyh) {
        this.isAdult = isAdult;
        this.cch = cch;
        this.eth = eth;
        this.evh = evh;
        this.wth = wth;
        this.mdh = mdh;
        this.pyh = pyh;
    }
    
    public boolean isAdult(){
        return isAdult;
    }

    public EDoS getDos() { return dos; }

    public void setDos(EDoS dos) { this.dos = dos; }
    
    public int getNN(){
        return NN;
    }

    public void setNN(int NN) { this.NN = NN; }
    
    @Override
    public void run(){
        eth.enterPatient(this);
        evh.enterPatient(this);
        wth.enterPatient(this); // call notifyExit(WTH) inside
        mdh.enterPatient(this); // call notifyExit(MDW) inside
        pyh.enterPatient(this);
    }
    public void notifyExit(ERoom_CC room) {
        cch.notifyExit(room, this);
    }

    @Override
    public String toString() {
        return String.format("%s%02d%s", isAdult ? "A" : "C", NN, dos);
    }
}
