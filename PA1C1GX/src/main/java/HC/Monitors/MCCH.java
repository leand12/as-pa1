package HC.Monitors;

import HC.Data.Room;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Monitor Call Center Hall, responsible for managing the movement of all Patients.
 */
public class MCCH implements IMCCH_Patient, IMCCH_CallCenter {
    private final ReentrantLock rl;
    private final Condition cNotEmpty;

    private final LinkedList<Room> notifications;

    public MCCH() {
        rl = new ReentrantLock();
        cNotEmpty = rl.newCondition();
        notifications = new LinkedList<>();
    }

    @Override
    public Room getNotification() {
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
    public void notifyExit(Room room) {
        rl.lock();
        notifications.addLast(room);
        cNotEmpty.signal();
        notify();   // notify work available
    }
}
