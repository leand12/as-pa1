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
public class METH implements IMonitor {
    private final TPatient[] adultFIFO;
    private final TPatient[] childFIFO;
    private final Condition[] carrayAdult;
    private final Condition[] carrayChild;
    private final ReentrantLock rl;
    private final Condition cChild;
    private final Condition cAdult;
    private final Logging log;
    private final GUI gui;
    private final int NoS;

    private int ETN = 0; // Patient Number
    private int ttm = 0;
    private int putAdultIdx = 0;
    private int putChildIdx = 0;
    private int getAdultIdx = 0;
    private int getChildIdx = 0;
    private int adultCount = 0;
    private int childCount = 0;
    private int adultAwaitCount = 0;
    private int childAwaitCount = 0;

    public METH(int NoS, int ttm, Logging log, GUI gui) {
        this.NoS = NoS / 2;
        this.ttm = ttm;
        this.log = log;
        this.gui = gui;

        adultFIFO = new TPatient[this.NoS];
        childFIFO = new TPatient[this.NoS];

        carrayAdult = new Condition[this.NoS];
        carrayChild = new Condition[this.NoS];

        rl = new ReentrantLock();
        cAdult = rl.newCondition();
        cChild = rl.newCondition();
    }

    @Override
    public boolean hasAdults() {
        return this.adultCount > 0;
    }

    @Override
    public boolean hasChildren() {
        return this.childCount > 0;
    }

    @Override
    public boolean isFullOfAdults() {
        return this.adultCount == this.NoS;
    }

    @Override
    public boolean isFullOfChildren() {
        return this.childCount == this.NoS;
    }

    // Used by a patient in order to enter the Hall
    @Override
    public void put(TPatient patient) {
        try {
            rl.lock();

            if (patient.isAdult()) {
                while (this.adultAwaitCount == NoS) {
                    cAdult.await();
                }
                this.adultAwaitCount++;

                // assign ETN to patient
                patient.setETN(++ETN);

                gui.addPatient("ETH", patient);
                log.logPatient("ETH", patient);

                // Move from ETH to ETR
                Thread.sleep((int) Math.floor(Math.random() * ttm));
                gui.removePatient("ETH", patient);
                gui.addPatient("ET2", patient);
                log.logPatient("ET2", patient);

                int tempIdx = putAdultIdx;
                putAdultIdx = (putAdultIdx + 1) % NoS;
                this.adultCount++;
                adultFIFO[tempIdx] = patient;
                carrayAdult[tempIdx] = rl.newCondition();
                carrayAdult[tempIdx].await();

            } else {
                while (this.childAwaitCount == NoS) {
                    cChild.await();
                }
                this.childAwaitCount++;
                // assign ETN to patient
                patient.setETN(++ETN);

                gui.addPatient("ETH", patient);
                log.logPatient("ETH", patient);

                // Move from ETH to ETR
                Thread.sleep((int) Math.floor(Math.random() * ttm));
                gui.removePatient("ETH", patient);
                gui.addPatient("ET1", patient);
                log.logPatient("ET1", patient);

                int tempIdx = putChildIdx;
                putChildIdx = (putChildIdx + 1) % NoS;
                this.childCount++;
                childFIFO[tempIdx] = patient;
                carrayChild[tempIdx] = rl.newCondition();
                carrayChild[tempIdx].await();
            }
        } catch (InterruptedException err) {
            System.err.println(err);
        } finally {
            rl.unlock();
        }
    }

    //A Patient can go to EVH
    @Override
    public void get() {
        try {
            rl.lock();
            // TODO: make condition empty
            if (hasAdults() && hasChildren()) {
                // remove adult
                if (adultFIFO[getAdultIdx].getETN() < childFIFO[getChildIdx].getETN()) {
                    this.adultCount--;
                    this.adultAwaitCount--;
                    carrayAdult[getAdultIdx].signal();
                    gui.removePatient("ET2", adultFIFO[getAdultIdx]);
                    getAdultIdx = (getAdultIdx + 1) % NoS;
                    cAdult.signal();
                } else {
                    this.childCount--;
                    this.childAwaitCount--;
                    carrayChild[getChildIdx].signal();
                    gui.removePatient("ET1", childFIFO[getChildIdx]);
                    getChildIdx = (getChildIdx + 1) % NoS;
                    cChild.signal();
                }
            } else if (hasAdults()) {
                this.adultCount--;
                this.adultAwaitCount--;
                carrayAdult[getAdultIdx].signal();
                gui.removePatient("ET2", adultFIFO[getAdultIdx]);
                getAdultIdx = (getAdultIdx + 1) % NoS;
                cAdult.signal();
            } else if (hasChildren()) {
                this.childCount--;
                this.childAwaitCount--;
                carrayChild[getChildIdx].signal();
                gui.removePatient("ET1", childFIFO[getChildIdx]);
                getChildIdx = (getChildIdx + 1) % NoS;
                cChild.signal();
            }
        } catch (Exception err) {
            System.err.println(err);
        } finally {
            rl.unlock();
        }
    }
}


