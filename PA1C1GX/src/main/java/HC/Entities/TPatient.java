/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Entities;

import HC.Data.EDoS;
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

    private int ETN;            // entrance hall number
    private boolean isAdult;
    private EDoS dos = EDoS.NONE;            // degree of severity

    public TPatient(boolean isAdult, ICCH_Patient cch, IETH_Patient eth, IEVH_Patient evh, IWTH_Patient wth, IMDH_Patient mdh,
                    IPYH_Patient pyh) {
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
    
    public int getETN(){
        return ETN;
    }

    public void setETN(int ETN) { this.ETN = ETN; }
    
    @Override
    public void run(){
        eth.enterPatient(this);
        cch.notifyExit(ETH, this);
        evh.enterPatient(this);
        wth.enterPatient(this);
        cch.notifyExit(EVH, this);
        // ...
    }

    @Override
    public String toString() {
        return String.format("%s%02d%s", isAdult ? "A" : "C", ETN, dos);
    }
}
