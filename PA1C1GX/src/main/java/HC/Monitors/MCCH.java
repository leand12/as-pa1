package HC.Monitors;

import HC.Data.ERoom;
import HC.Data.Notification;
import HC.Entities.TPatient;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Monitor Call Center Hall, responsible for managing the movement of all Patients.
 */
public class MCCH implements ICCH_Patient, ICCH_CallCenter {
    private final ReentrantLock rl;
    private final Condition cNotEmpty;

    private final LinkedList<Notification> notifications;

    public MCCH() {
        rl = new ReentrantLock();
        cNotEmpty = rl.newCondition();
        notifications = new LinkedList<>();
    }

    @Override
    public Notification getNotification() {
        try {
            rl.lock();
            while (notifications.isEmpty()) cNotEmpty.await();
            return notifications.pollFirst();

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            rl.unlock();
        }
        return null;
    }

    @Override
    public void notifyExit(ERoom room, TPatient patient) {
        rl.lock();
        notifications.addLast(new Notification(room, patient));
        cNotEmpty.signal(); // notify work available
        rl.unlock();
    }
}
