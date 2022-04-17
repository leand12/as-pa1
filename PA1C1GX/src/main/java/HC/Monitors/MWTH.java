package HC.Monitors;

import HC.Data.ERoom;
import HC.Data.EDoS;

import HC.Data.ERoom_CC;
import HC.Entities.TPatient;
import HC.Logging.Logging;
import HC.Main.GUI;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static HC.Data.ERoom.*;

public class MWTH implements IWTH_CallCenter, IWTH_Patient {

    private final ReentrantLock rl;

    private final MFIFO redChildFIFO, yellowChildFIFO, blueChildFIFO,
            redAdultFIFO, yellowAdultFIFO, blueAdultFIFO;


    private final Logging log;
    private final GUI gui;
    private final int NoS;
    private final int ttm;
    private int ccount = 0;
    private int acount = 0;
    private int WTN = 0; // Patient Number
    private int nextWTN =1;

    private Condition cNotBothEmpty;
    private final Condition cDoSNotEmpty;
    private Condition cNextWTN;

    private final MFIFO challFIFO;
    private final MFIFO ahallFIFO;



    public MWTH(int NoS, int ttm, Logging log, GUI gui){
        this.NoS = NoS;
        this.ttm = ttm;
        this.log = log;
        this.gui = gui;

        this.rl =  new ReentrantLock();

        this.challFIFO = new MFIFO(rl, NoS/2);
        this.ahallFIFO = new MFIFO(rl, NoS/2);

        this.redAdultFIFO = new MFIFO(rl, NoS);
        this.yellowAdultFIFO = new MFIFO(rl, NoS);
        this.blueAdultFIFO = new MFIFO(rl, NoS);
        this.redChildFIFO = new MFIFO(rl, NoS);
        this.yellowChildFIFO = new MFIFO(rl, NoS);
        this.blueChildFIFO = new MFIFO(rl, NoS);

        this.cDoSNotEmpty = rl.newCondition();
        this.cNotBothEmpty = rl.newCondition();
        this.cNextWTN = rl.newCondition();

    }

    /**
     * @return the FIFO that has the next priority patient
     */
    private MWTH.MFIFO getPriorityFIFO(boolean isAdult) {
        try {
            rl.lock();
            while ((!isAdult && challFIFO.isEmpty()) || (isAdult && ahallFIFO.isEmpty())) {
                cNotBothEmpty.await();
            }
            if (isAdult) {
                return ahallFIFO;
            }
            return challFIFO;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rl.unlock();
        }
    }

    /**
     * @return the FIFO that has the next priority patient
     */
    private MWTH.MFIFO getPriorityDoSFIFO(boolean isAdult) {
        try {
            rl.lock();
            if (isAdult) {
                while (redAdultFIFO.isEmpty() && blueAdultFIFO.isEmpty() && yellowAdultFIFO.isEmpty()) {
                    cDoSNotEmpty.await();
                }
                if (!redAdultFIFO.isEmpty()) {
                    return redAdultFIFO;
                } else if (!yellowAdultFIFO.isEmpty()) {
                    return yellowAdultFIFO;
                }
                return blueAdultFIFO;
            } else {
                while (redChildFIFO.isEmpty() && blueChildFIFO.isEmpty() && yellowChildFIFO.isEmpty()) {
                    cDoSNotEmpty.await();
                }
                if (!redChildFIFO.isEmpty()){
                    return redChildFIFO;
                }
                else if(!yellowChildFIFO.isEmpty()){
                    return yellowChildFIFO;
                }
                return blueChildFIFO;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public void enterPatient(TPatient patient) {
        if (patient.isAdult()) {
            ahallFIFO.put(patient, WTR2);
            switch (patient.getDos()) {
                case RED -> redAdultFIFO.putR(patient);
                case BLUE -> blueAdultFIFO.putR(patient);
                default -> yellowAdultFIFO.putR(patient);
            }
        }
        else {
            challFIFO.put(patient, WTR1);
            switch (patient.getDos()) {
                case RED -> redChildFIFO.putR(patient);
                case BLUE -> blueChildFIFO.putR(patient);
                default -> yellowChildFIFO.putR(patient);
            }
        }

        rl.lock();
        if (patient.isAdult())
            gui.removePatient(WTR2, patient);
        else
            gui.removePatient(WTR1, patient);
        rl.unlock();
    }

    @Override
    public void callPatient(boolean isAdult) {
        getPriorityFIFO(isAdult).get();
    }

    @Override
    public void callPatient2(boolean isAdult) {
        getPriorityDoSFIFO(isAdult).get();
    }

    class MFIFO {
        private final TPatient[] fifo;
        private final Condition[] cond;
        private final ReentrantLock rl;
        private final Condition cNotFull;
        private final Condition cNotEmpty;
        private final int size;
        private int idxPut = 0;
        private int idxGet = 0;
        private int count = 0;
        private final boolean permitted[];    // ensures a Patient keeps running if signal is performed before await

        public MFIFO(ReentrantLock rl, int size) {
            this.size = size;
            fifo = new TPatient[size];
            cond = new Condition[size];
            permitted = new boolean[size];

            this.rl = rl;
            cNotEmpty = rl.newCondition();
            cNotFull = rl.newCondition();
            for (var i = 0; i < cond.length; i++)
                cond[i] = rl.newCondition();
        }

        public void put(TPatient patient, ERoom room) {
            try {
                rl.lock();

                // asign WTN
                patient.setNN(++WTN);
                log.logPatient(ERoom.WTH, patient);
                gui.addPatient(ERoom.WTH, patient);

                // wait for CH call
                while (isFull()) cNotFull.await();

                count++;
                fifo[idxPut] = patient;
                int idx = idxPut;
                idxPut = (++idxPut) % size;

                cNotEmpty.signal();
                cNotBothEmpty.signal();     // signal CallCenter

                while (!permitted[idx]) cond[idx].await();
                permitted[idx] = false;

                patient.notifyExit(ERoom_CC.WTH);
                // Move from WTH to WTR
                log.logPatient(room, patient);
                gui.addPatient(room, patient);

                cNotFull.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                rl.unlock();
            }
        }

        public void putR(TPatient patient){
            try {
                rl.lock();
                count++;
                fifo[idxPut] = patient;
                int idx = idxPut;
                idxPut = (++idxPut) % size;
                cNotEmpty.signal();
                cDoSNotEmpty.signal(); // signal CallCenter
                while (!permitted[idx]) cond[idx].await(); // wait for CC call
                permitted[idx] = false;
                cNotFull.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                rl.unlock();
            }
        }


        public void get() {
            try {
                rl.lock();
                while (isEmpty()) cNotEmpty.await();
                count--;
                fifo[idxGet] = null;
                int idx = idxGet;
                idxGet = (++idxGet) % size;
                rl.unlock();

                // move to next hall/room
                Thread.sleep((int) Math.floor(Math.random() * ttm));

                rl.lock();

                permitted[idx] = true;
                cond[idx].signal();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                rl.unlock();
            }
        }

        /* thread-unsafe access methods */
        /* should be called inside `rl` lock */

        public TPatient peek() {
            return fifo[idxGet];
        }

        public boolean isFull() {
            return count == size;
        }

        public boolean isEmpty() {
            return count == 0;
        }
    }
}