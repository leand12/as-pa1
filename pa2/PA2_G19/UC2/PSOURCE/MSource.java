package UC2.PSOURCE;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Monitor to store the information in a thread-safe region to store records to be send to PProducer
 * 
 */
public class MSource {

    private final ReentrantLock rl;
    /** Condition to signal that the fifo has records */
    private Condition cNotEmpty;
    /** Condition to signal that more records can be put */
    private Condition cNotFull;

    private final String[] fifo;
    private final int size;
    private int idxPut = 0;
    private int idxGet = 0;
    private int count = 0;

    public MSource(int size){
        this.size = size;
        fifo = new String[size];
        rl = new ReentrantLock();
        cNotEmpty = rl.newCondition();
        cNotFull = rl.newCondition();
    }

    /**
     * Puts a new record in the fifo
     *
     * @param record   the record to be put.
     */
    public void putRecord(String record){
        try {
            rl.lock();
            while (isFull()) cNotFull.await();
            count++;
            fifo[idxPut] = record;
            idxPut = (++idxPut) % size;
            cNotEmpty.signal();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rl.unlock();
        }
    }

    /**
     * Retrieves a record from the fifo
     *
     */
    public String getRecord(){
        String record = "";
        try {
            rl.lock();
            while (isEmpty()) cNotEmpty.await();
            count--;
            record = fifo[idxGet];
            idxGet = (++idxGet) % size;
            cNotFull.signal();
            
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            rl.unlock();
        }
        return record;
    }

    /**
     * Verifies whether the fifo is full or not
     *
     */
    public boolean isFull() {
        return count >= size;
    }

    /**
     * Verifies whether the fifo is empty or not
     *
     */
    public boolean isEmpty() {
        return count <= 0;
    }

}
