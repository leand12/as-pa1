package HC.Monitors;

import HC.Data.ERoom;

import HC.Entities.TPatient;
import HC.Logging.Logging;
import HC.Main.GUI;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import static HC.Data.ERoom.MDH;
import static HC.Data.ERoom_CC.MDW;
import static HC.Data.ERoom_CC.WTRi;

public class MMDH implements IMDH_CallCenter, IMDH_Patient, IMDH_Doctor {
    private final ReentrantLock rl;
    private final MRooms adults;
    private final MRooms children;

    private final Logging log;
    private final GUI gui;

    private final int ttm;
    private final int mdt; // evaluation time

    public MMDH(int mdt, int ttm, Logging log, GUI gui) {
        this.mdt = mdt;
        this.ttm = ttm;
        this.log = log;
        this.gui = gui;

        rl = new ReentrantLock();
        adults = new MRooms(rl);
        children = new MRooms(rl);
    }

    @Override
    public void enterPatient(TPatient patient) {
        if (patient.isAdult()) {
            adults.put(patient);
        } else {
            children.put(patient);
        }
    }

    @Override
    public void callPatient(boolean isAdult) {
        if (isAdult) {
            adults.get();
        } else {
            children.get();
        }
    }

    @Override
    public void examinePatient(int room) {
        if (room == 0 || room == 1) {
            children.examine(room);
        } else if (room == 2 || room == 3) {
            adults.examine(room - 2);
        }
    }

    class MRooms {
        private final Condition cNotPermitted;
        private final Condition cNotFull;
        private final Condition[] cNotExamined;

        private boolean permitted = false;
        private final boolean[] examined;

        private final TPatient[] rooms;
        private final int maxPatients = 2;
        private int patientCount = 0;

        MRooms(ReentrantLock rl) {
            cNotPermitted = rl.newCondition();
            cNotFull = rl.newCondition();
            cNotExamined = new Condition[maxPatients];
            for (var i = 0; i < maxPatients; i++)
                cNotExamined[i] = rl.newCondition();

            rooms = new TPatient[maxPatients];
            examined = new boolean[maxPatients];
        }

        void put(TPatient patient) {
            try {
                rl.lock();
                // enter MDW
                log.logPatient(MDH, patient);
                gui.addPatient(MDH, patient);

                while (isFull()) cNotFull.await();
                patient.notifyExit(WTRi);

                while (!permitted) cNotPermitted.await();
                permitted = false;

                // enter MDRi
                patient.notifyExit(MDW);

                for (int i = 0; i < maxPatients; i++) {
                    // patient enters room
                    if (rooms[i] == null) {
                        patientCount++;
                        rooms[i] = patient;

                        var room = ERoom.valueOf("MDR" + (patient.isAdult() ? i + 3 : i + 1));
                        log.logPatient(room, patient);
                        gui.addPatient(room, patient);

                        cNotExamined[i].signal();
                        while (!examined[i]) cNotExamined[i].await();
                        examined[i] = false;

                        log.logPatient(room, patient);
                        gui.updateRoom(room);
                        rl.unlock();

                        // patient moves to PYH
                        Thread.sleep((int) Math.floor(Math.random() * ttm));

                        rl.lock();
                        this.rooms[i] = null;
                        patientCount--;
                        cNotFull.signal();
                        gui.removePatient(room, patient);
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                rl.unlock();
            }
        }

        void get() {
            rl.lock();
//            System.out.println("Sempre siga");
            permitted = true;
            cNotPermitted.signal();
            rl.unlock();
        }

        boolean isFull() {
            return patientCount == maxPatients;
        }

        void examine(int idx) {
            try {
                rl.lock();
                while (rooms[idx] == null || examined[idx])
                    cNotExamined[idx].await();
                rl.unlock();

                // medical appointment time
                Thread.sleep((int) Math.floor(Math.random() * mdt));

                rl.lock();
                examined[idx] = true;
                cNotExamined[idx].signal();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                rl.unlock();
            }
        }
    }
}