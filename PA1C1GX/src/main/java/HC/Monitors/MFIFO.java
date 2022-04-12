package HC.Monitors;

import HC.Entities.TPatient;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

interface Callback {
    void call();
}

public class MFIFO {
    private final TPatient[] fifo;
    private final Condition[] cond;
    private final ReentrantLock rl;
    private final Condition cNotFull;
    private final Condition cNotEmpty;
    private final int size;
    private int idxPut = 0;
    private int idxGet = 0;
    private int count = 0;
    private boolean permittedToMove = false;    // ensures a Patient keeps running if signal is performed before await

    public MFIFO(int size) {
        this.size = size;
        fifo = new TPatient[size];
        cond = new Condition[size];
        rl = new ReentrantLock();
        cNotEmpty = rl.newCondition();
        cNotFull = rl.newCondition();
    }

    public void put(TPatient patient, Callback callback) {
        try {
            rl.lock();
            while (isFull()) cNotFull.await();
            count++;
            fifo[idxPut] = patient;
            cNotEmpty.signal();
            rl.unlock();

            callback.call();    // safe execution outside shared zone

            rl.lock();
            cond[idxPut] = rl.newCondition();
            while (!permittedToMove) cond[idxPut].await();
            permittedToMove = false;
            idxPut = (++idxPut) % size;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            rl.unlock();
        }
    }

    public void get(Callback callback) {
        try {
            rl.lock();
            while (isEmpty()) cNotEmpty.await();
            count--;
            fifo[idxGet] = null;
            cNotFull.signal();
            rl.unlock();

            callback.call();    // safe execution outside shared zone

            rl.lock();
            permittedToMove = true;
            cond[idxGet].signal();
            idxGet = (++idxGet) % size;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            rl.unlock();
        }
    }

    public void put(TPatient patient) {
        put(patient, () -> {});
    }

    public void get() {
        get(() -> {});
    }

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
