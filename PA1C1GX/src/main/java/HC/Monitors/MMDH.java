package HC.Monitors;

import HC.Data.EDoS;
import HC.Data.ERoom;
import HC.Data.ERoom_CC;
import HC.Entities.TPatient;
import HC.Logging.Logging;
import HC.Main.GUI;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MMDH implements IMDH_CallCenter, IMDH_Patient, IMDH_Doctor {

    private static ReentrantLock rl;
    private Condition cAdultNotFull;
    private Condition cBothNotEmpty;
    private Condition cChildNotFull;

    private Condition cNotAppointed[];
    private TPatient rooms[];
    private boolean appointed[];
    private final Logging log;
    private final GUI gui;
    private final int ttm;
    private final int mat;
    private int ccount = 0;
    private int acount = 0;

    private TPatient aPatient = null;
    private TPatient cPatient = null;

    public MMDH(int mat, int ttm, Logging log, GUI gui) {
        this.mat = mat;
        this.ttm = ttm;
        this.gui = gui;
        this.log = log;

        this.rl = new ReentrantLock();
        this.cAdultNotFull = rl.newCondition();
        this.cChildNotFull = rl.newCondition();
        this.cBothNotEmpty = rl.newCondition();


        cNotAppointed = new Condition[4];
        for (var i = 0; i < cNotAppointed.length; i++)
            cNotAppointed[i] = rl.newCondition();

        rooms = new TPatient[4];
        appointed = new boolean[4];
    }

    @Override
    public void enterPatient(TPatient patient) {
        try {
            rl.lock();
            log.logPatient(ERoom.MDH, patient);
            gui.addPatient(ERoom.MDH, patient);
            if (patient.isAdult()) {
                aPatient = patient;
                cBothNotEmpty.signal();
                do {
                    cAdultNotFull.await();
                }
                while (isFull(patient));  // allways wait for CC call
                aPatient = null;
                for (int i = 2; i < 4; i++) {
                    // patient enters room
                    if (rooms[i] == null) {
                        acount++;
                        rooms[i] = patient;
                        patient.notifyExit(ERoom_CC.MDW);
                        var room = ERoom.valueOf("MDR" + (i + 1));
                        log.logPatient(room, patient);
                        gui.addPatient(room, patient);
                        cNotAppointed[i].signal();
                        while (!appointed[i]) cNotAppointed[i].await();
                        appointed[i] = false;
                        this.rooms[i] = null;
                        acount--;
                        break;
                    }
                }
            } else {
                cPatient = patient;
                cBothNotEmpty.signal();
                do {
                    cChildNotFull.await();
                }
                while (isFull(patient));  // allways wait for CC call
                cPatient = null;
                for (int i = 0; i < 2; i++) {
                    // patient enters room
                    if (rooms[i] == null) {
                        ccount++;
                        rooms[i] = patient;
                        patient.notifyExit(ERoom_CC.MDW);
                        var room = ERoom.valueOf("MDR" + (i + 1));
                        log.logPatient(room, patient);
                        gui.addPatient(room, patient);
                        cNotAppointed[i].signal();
                        while (!appointed[i]) cNotAppointed[i].await();
                        appointed[i] = false;
                        this.rooms[i] = null;
                        ccount--;
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rl.unlock();
        }
    }

    @Override
    public void callPatient(boolean isAdult) {
        try {
            rl.lock();
            while ((isAdult && aPatient == null) || (!isAdult && cPatient == null)) cBothNotEmpty.await(); // wait for a patient

            if (isAdult) {
                System.out.println("Calling adult to room");

                cAdultNotFull.signal();
            } else {
                System.out.println("Calling child to room");

                cChildNotFull.signal();
            }
            rl.unlock();

            // move to next room
            Thread.sleep((int) Math.floor(Math.random() * ttm));

            rl.lock();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public void examinePatient(int idx) {
        try {
            rl.lock();
            while (rooms[idx] == null)
                cNotAppointed[idx].await();
            rl.unlock();

            // evaluation time
            Thread.sleep((int) Math.floor(Math.random() * mat));

            rl.lock();
            appointed[idx] = true;
            cNotAppointed[idx].signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rl.unlock();
        }

    }

    private boolean isFull(TPatient patient) {

        if (patient.isAdult()) {
            return acount >= 2;
        } else {
            return ccount >= 2;
        }

    }


}