package HC.Monitors;

import HC.Data.ERoom_CC;
import HC.Data.Notification;
import HC.Entities.TPatient;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Call Center Hall Monitor, responsible for managing the movement of all Patients.
 */
public class MCCH implements ICCH_Patient, ICCH_CallCenter {
    private final ReentrantLock rl;
    private final Condition cNotEmpty;

    private final LinkedList<Notification> notifications;   // an unlimited size FIFO of notifications

    public MCCH() {
        rl = new ReentrantLock();
        cNotEmpty = rl.newCondition();
        notifications = new LinkedList<>();
    }

    @Override
    public Notification getNotification() {
        try {
            rl.lock();
            while (notifications.isEmpty()) {
                // wait for patient notification
                cNotEmpty.await();
            }
            return notifications.pollFirst();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public void notifyExit(ERoom_CC room, TPatient patient) {
        rl.lock();
        notifications.addLast(new Notification(room, patient));
        cNotEmpty.signal(); // notify work available
        rl.unlock();
    }
}
