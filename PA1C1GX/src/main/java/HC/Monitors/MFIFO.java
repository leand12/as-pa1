package HC.Monitors;

import HC.Entities.TPatient;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

interface Callback {
    default void before() {};
    default void work() {};
    default void after() {};
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

    public void put(TPatient patient, Callback callback) {
        try {
            rl.lock();
            while (isFull()) cNotFull.await();
            count++;
            fifo[idxPut] = patient;
            int idx = idxPut;
            idxPut = (++idxPut) % size;
            cNotEmpty.signal();
            callback.before();
            rl.unlock();

            callback.work();

            rl.lock();
            callback.after();
            while (!permitted[idx]) cond[idx].await();
            permitted[idx] = false;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
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
            cNotFull.signal();

            permitted[idx] = true;
            cond[idx].signal();
            System.out.println("#fiz isto");
        } catch (InterruptedException ex) {
            System.err.println("ERRO");
            ex.printStackTrace();
        } finally {
            System.out.println("#e dps fiz isto");
            rl.unlock();
        }
    }

    public void put(TPatient patient) {
        put(patient, new Callback() {});
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
