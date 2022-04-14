/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Monitors;

import HC.Data.ERoom;
import HC.Entities.TPatient;
import HC.Logging.Logging;
import HC.Main.GUI;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static HC.Data.ERoom.*;

/**
 * @author guids
 * Entrance Hall Monitor
 */
public class METH implements IETH_Patient, IETH_CallCenter {
    private final ReentrantLock rl;
    private Condition cNotBothEmpty;
    private final MFIFO adultFIFO;
    private final MFIFO childFIFO;
    private final Logging log;
    private final GUI gui;
    private final int NoS;

    private int ETN = 0; // Patient Number
    private int ttm;

    public METH(int NoS, int ttm, Logging log, GUI gui) {
        this.NoS = NoS / 2;
        this.ttm = ttm;
        this.log = log;
        this.gui = gui;

        rl = new ReentrantLock();
        cNotBothEmpty = rl.newCondition();
        adultFIFO = new MFIFO(rl, this.NoS);
        childFIFO = new MFIFO(rl, this.NoS);
    }

    /**
     * @return  the FIFO that has the next priority patient
     */
    private MFIFO getPriorityFIFO() {
        try {
            rl.lock();
            while (adultFIFO.isEmpty() && childFIFO.isEmpty()) {
                cNotBothEmpty.await();
            }
            if (childFIFO.isEmpty() || adultFIFO.peek().getETN() < childFIFO.peek().getETN()) {
                return adultFIFO;
            }
            return childFIFO;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            rl.unlock();
        }
        return null;
    }

    private Callback buildCallBack(TPatient patient, ERoom room) {
        return new Callback() {
            public void before() {
                // assign ETN to patient
                patient.setETN(++ETN);
                cNotBothEmpty.signal();     // signal CallCenter

                gui.addPatient(ETH, patient);
                log.logPatient(ETH, patient);
            }

            public void work() {
                // move from ETH to ETR2
                try {
                    Thread.sleep((int) Math.floor(Math.random() * ttm));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            public void after() {
                gui.addPatient(room, patient);
                log.logPatient(room, patient);
            }
        };
    }

    @Override
    public void enterPatient(TPatient patient) {
        if (patient.isAdult()) {
            adultFIFO.put(patient, buildCallBack(patient, ET2));
        } else {
            childFIFO.put(patient, buildCallBack(patient, ET1));
        }
    }

    @Override
    public void callPatient() {
        getPriorityFIFO().get();
    }
}


