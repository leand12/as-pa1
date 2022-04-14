/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Entities;

import HC.Data.EDoS;
import HC.Monitors.MEVH;

import java.util.Random;


/**
 * @author guids
 */
public class TNurse extends Thread {

    private final MEVH mevh;
    private final int evt;

    public TNurse(MEVH mevh, int evt) {
        this.mevh = mevh;
        this.evt = evt;
    }

    public void assignDos(TPatient patient) throws InterruptedException {
        EDoS dos = EDoS.NONE;
        while (dos == EDoS.NONE) {
            dos = EDoS.values()[new Random().nextInt(EDoS.values().length)];
        }

        // evaluation time
        Thread.sleep((int) Math.floor(Math.random() * evt));
        patient.setDos(dos);
    }

    @Override
    public void run() {
        mevh.assignNurse(this);
        while (true) {
        }
    }

}
