/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Monitors;

import HC.Entities.TPatient;
import HC.Logging.Logging;
import HC.Main.GUI;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author guids
 * Entrance Hall Monitor
 */
public class METH implements IMETH_Patient, IMETH_CallCenter {
    private final ReentrantLock rl;
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

        adultFIFO = new MFIFO(this.NoS);
        childFIFO = new MFIFO(this.NoS);
        rl = new ReentrantLock();
    }

    @Override
    public void enterPatient(TPatient patient) {
        Callback callback = () -> {
            // assign ETN to patient
            patient.setETN(++ETN);

            gui.addPatient("ETH", patient);
            log.logPatient("ETH", patient);

            // move from ETH to ETR2
            try {
                Thread.sleep((int) Math.floor(Math.random() * ttm));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        };

        if (patient.isAdult()) {
            adultFIFO.put(patient, () -> {
                callback.call();
                gui.addPatient("ET2", patient);
                log.logPatient("ET2", patient);
            });
        } else {
            childFIFO.put(patient, () -> {
                callback.call();
                gui.addPatient("ET1", patient);
                log.logPatient("ET1", patient);
            });
        }
    }

    @Override
    public void callPatient() {
        rl.lock();
        if (adultFIFO.isEmpty() && childFIFO.isEmpty()) {
            System.err.println("Wrong call by CallCenter");
            return; // useless call, nothing is affected
        }

        if (childFIFO.isEmpty() || adultFIFO.peek().getETN() < childFIFO.peek().getETN()) {
            TPatient a = adultFIFO.peek();
            adultFIFO.get(() -> {
                gui.removePatient("ET2", a);
            });
        } else {
            TPatient c = childFIFO.peek();
            childFIFO.get(() -> {
                gui.removePatient("ET1", c);
            });
        }
        rl.unlock();
    }
}


