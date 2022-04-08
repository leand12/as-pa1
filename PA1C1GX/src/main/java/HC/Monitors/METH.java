/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Monitors;

import HC.Entities.TPatient;
import HC.Logging.Logging;

import java.io.IOException;
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

    public METH(int NoS, int ttm, Logging log) {
        this.NoS = NoS / 2;
        this.ttm = ttm;
        this.log = log;

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
        int pETN;
        try {
            rl.lock();

            if (patient.isAdult()) {
                while (this.adultAwaitCount == NoS) {
                    cAdult.await();
                }
                this.adultAwaitCount++;
                pETN = ETN;
                ETN++;

                log.log(String.format("%-4s| %1s%2d %8s|%-21s|%-15s|%-25s|%-4s", " ", "A", pETN, " ", " ", " ", " ", " "));

                // Move from ETH to ETR
                Thread.sleep((int) Math.floor(Math.random() * ttm));

                log.log(String.format("%-4s| %3s %1s%2d %4s|%-21s|%-15s|%-25s|%-4s", " ", " ", "A", pETN, " ", " ", " ", " ", " "));

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
                pETN = ETN;
                ETN++;
                log.log(String.format("%-4s| %1s%2d %8s|%-21s|%-15s|%-25s|%-4s", " ", "C", pETN, " ", " ", " ", " ", " "));

                // Move from ETH to ETR
                Thread.sleep((int) Math.floor(Math.random() * ttm));

                log.log(String.format("%-4s| %8s%1s%2d |%-21s|%-15s|%-25s|%-4s", " ", " ", "C", pETN, " ", " ", " ", " ", " "));

                int tempIdx = putChildIdx;
                putChildIdx = (putChildIdx + 1) % NoS;
                this.childCount++;
                childFIFO[tempIdx] = patient;
                carrayChild[tempIdx] = rl.newCondition();
                carrayChild[tempIdx].await();
                
            }
            // assign ETN to patient
            patient.setETN(pETN);

        } catch (InterruptedException | IOException err) {
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
            if (hasAdults() && hasChildren()) {
                // remove adult
                if (adultFIFO[getAdultIdx].getETN() < childFIFO[getAdultIdx].getETN()) {
                    this.adultCount--;
                    this.adultAwaitCount--;
                    carrayAdult[getAdultIdx].signal();
                    getAdultIdx = (getAdultIdx + 1) % NoS;
                    cAdult.signal();
                } else {
                    this.childCount--;
                    this.childAwaitCount--;
                    carrayChild[getChildIdx].signal();
                    getChildIdx = (getChildIdx + 1) % NoS;
                    cChild.signal();
                }
            } else if (hasAdults()) {
                this.adultCount--;
                this.adultAwaitCount--;
                carrayAdult[getAdultIdx].signal();
                getAdultIdx = (getAdultIdx + 1) % NoS;
                cAdult.signal();
            } else if (hasChildren()) {
                this.childCount--;
                this.childAwaitCount--;
                carrayChild[getChildIdx].signal();
                getChildIdx = (getChildIdx + 1) % NoS;
                cChild.signal();
            }
        }catch(Exception e){
         
        } finally {
            rl.unlock();
        }

    }
}


