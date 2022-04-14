package HC.Monitors;

import HC.Data.ERoom;
import HC.Entities.TNurse;
import HC.Entities.TPatient;
import HC.Logging.Logging;
import HC.Main.GUI;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MEVH implements IEVH_Patient {
    private final ReentrantLock rl;
    private final ReentrantLock rl1;
    private final Condition cRoom;
    private final Condition cNurse;
    private final Logging log;
    private final GUI gui;

    private final TNurse[] nurses;
    private final boolean[] roomOcupied;

    private int patientCount = 0;
    private boolean nurseAssign = false;
    private int ttm = 0;

    public MEVH(int ttm, Logging log, GUI gui) {
        this.rl = new ReentrantLock();
        this.rl1 = new ReentrantLock();
        this.cRoom = this.rl.newCondition();
        this.cNurse = this.rl1.newCondition();
        this.nurses = new TNurse[4];
        this.roomOcupied = new boolean[4];
        this.log = log;
        this.gui = gui;
        this.ttm = ttm;
    }

    public void assignNurse(TNurse nurse) {
        try {
            rl1.lock();
            while (nurseAssign) {
                cNurse.await();
            }
            nurseAssign = true;
            for (int i = 0; i < 4; i++) {
                if (nurses[i] == null) {
                    nurses[i] = nurse;
                    break;
                }
            }
            nurseAssign = false;
            cNurse.signal();
        } catch (InterruptedException ex) {
            System.err.println(ex);
        } finally {
            rl1.unlock();
        }
    }

    @Override
    public void enterPatient(TPatient patient) {
        try {
            rl.lock();
            for (int i = 0; i < 4; i++) {
                // patient enters room
                if (!this.roomOcupied[i]) {
                    this.roomOcupied[i] = true;

                    ERoom room = ERoom.valueOf("EVR" + (i + 1));
                    log.logPatient(room, patient);
                    gui.addPatient(room, patient);

                    patientCount++;
                    nurses[i].assignDos(patient);

                    log.logPatient(room, patient);
                    gui.updateRoom(room);

                    // patient moves to WTH
                    Thread.sleep((int) Math.floor(Math.random() * ttm));
                    this.roomOcupied[i] = false;
                    patientCount--;
                    break;
                }
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        } finally {
            rl.unlock();
        }
    }
}
