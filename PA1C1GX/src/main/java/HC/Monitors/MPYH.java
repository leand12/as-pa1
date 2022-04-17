package HC.Monitors;

import HC.Entities.TPatient;
import HC.Logging.Logging;
import HC.Main.GUI;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static HC.Data.ERoom.PYH;
import static HC.Data.ERoom.CSH;
import static HC.Data.ERoom.OUT;
import static HC.Data.ERoom_CC.MDRi;

/**
 * Payment Hall Monitor, where payments take place.
 */
public class MPYH implements IPYH_Patient, IPYH_Cashier {
    private final ReentrantLock rl;
    private final Condition cNotOccupied;
    private final Condition cNotPayed;

    private final Logging log;
    private final GUI gui;

    private final int ttm;      // time to move
    private final int pyt;      // payment time

    private boolean occupied = false;   // whether cashier is occupied with a patient
    private boolean payed = false;

    public MPYH(int pyt, int ttm, Logging log, GUI gui) {
        this.pyt = pyt;
        this.ttm = ttm;
        this.log = log;
        this.gui = gui;

        rl = new ReentrantLock();
        cNotOccupied = rl.newCondition();
        cNotPayed = rl.newCondition();
    }

    @Override
    public void enterPatient(TPatient patient) {
        try {
            rl.lock();
            patient.notifyExit(MDRi);

            // enter PYH
            log.logPatient(PYH, patient);
            gui.addPatient(PYH, patient);

            while (occupied) cNotOccupied.await();
            occupied = true;

            // enter Cashier
            gui.addPatient(CSH, patient);

            cNotPayed.signal();
            while (!payed) cNotPayed.await();
            rl.unlock();

            // exit hospital
            Thread.sleep((int) Math.floor(Math.random() * ttm));

            rl.lock();
            log.logPatient(OUT, patient);
            gui.addPatient(OUT, patient);

            occupied = false;
            cNotOccupied.signal();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            rl.lock();
        } finally {
            rl.unlock();
        }
    }

    @Override
    public void receivePayment() {
        try {
            rl.lock();
            while (!occupied || payed)
                cNotPayed.await();
            rl.unlock();

            // payment time
            Thread.sleep((int) Math.floor(Math.random() * pyt));

            rl.lock();
            // allow Patient to move on
            payed = true;
            cNotPayed.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rl.unlock();
        }
    }
}
