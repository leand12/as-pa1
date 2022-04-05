/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Monitors;

import HC.Entities.TPatient;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author guids
 * Entrance Hall Monitor
 */
public class METH implements IMonitor {

    private final int NoS;
    private final TPatient[] adultFIFO;
    private final TPatient[] childFIFO;
    private final Condition[] carrayAdult;
    private final Condition[] carrayChild;
    private final ReentrantLock rl;
    private final Condition cChild;
    private final Condition cAdult;
    private int ETN = 0; // Patient Number
    private int putAdultIdx = 0;
    private int putChildIdx = 0;
    private int getAdultIdx = 0;
    private int getChildIdx = 0;
    private int adultCount = 0;
    private int childCount = 0;

    public METH(int NoS) {
        this.NoS = NoS / 2;
        adultFIFO = new TPatient[this.NoS];
        childFIFO = new TPatient[this.NoS];

        carrayAdult = new Condition[this.NoS];
        carrayChild = new Condition[this.NoS];

        this.rl = new ReentrantLock();
        this.cAdult = rl.newCondition();
        this.cChild = rl.newCondition();
    }

    @Override
    public boolean hasAdults() {
        return adultCount > 0;
    }

    @Override
    public boolean hasChildren() {
        return childCount > 0;
    }

    @Override
    public boolean isFullOfAdults() {
        return adultCount == NoS;
    }

    @Override
    public boolean isFullOfChildren() {
        return childCount == NoS;
    }

    // Used by a patient in order to enter the Hall
    @Override
    public void put(TPatient patient) {
        int pETN = ETN;
        try {
            rl.lock();

            if (patient.isAdult()) {
                while (isFullOfAdults()) {
                    cAdult.await();
                }
                adultCount++;
                adultFIFO[putAdultIdx] = patient;
                carrayAdult[putAdultIdx] = rl.newCondition();
                carrayAdult[putAdultIdx].await();
                putAdultIdx = (putAdultIdx + 1) % NoS;
                ETN++;
            } else {
                while (isFullOfChildren()) {
                    cChild.await();
                }
                childCount++;
                childFIFO[putChildIdx] = patient;
                carrayChild[putChildIdx] = rl.newCondition();
                carrayChild[putChildIdx].await();
                putChildIdx = (putChildIdx + 1) % NoS;
                ETN++;

            }
            // assign ETN to patient
            patient.setETN(pETN);

        } catch (InterruptedException ignored) {
            System.err.println("Monitor::put error");
        } finally {
            rl.unlock();
        }
    }

    //A Patient can go to EVH
    @Override
    public void get() {

        if (hasAdults() && hasChildren()) {

            // remove adult
            if (adultFIFO[(getAdultIdx + NoS - 1) % NoS].getETN() < childFIFO[(getAdultIdx + NoS - 1) % NoS].getETN()) {
                adultCount--;
                carrayAdult[getAdultIdx].signal();
                getAdultIdx = (getAdultIdx + 1) % NoS;
                cAdult.signal();
            } else {
                childCount--;
                carrayChild[getChildIdx].signal();
                getChildIdx = (getChildIdx + 1) % NoS;
                cChild.signal();
            }
        } else if (hasAdults()) {
            adultCount--;
            carrayAdult[getAdultIdx].signal();
            getAdultIdx = (getAdultIdx + 1) % NoS;
            cAdult.signal();
        } else if (hasChildren()) {
            childCount--;
            carrayChild[getChildIdx].signal();
            getChildIdx = (getChildIdx + 1) % NoS;
            cChild.signal();
        }
    }
}


