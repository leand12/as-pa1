package UC3.PSOURCE;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Monitor to store the information in a thread-safe region to store records to be send to PProducer
 * 
 */
public class MSource {

    /** Number of partitions */
    private final int N_PARTITIONS = 3;
    /** Fifos, one for each partition */
    private final FIFO[] fifos = new FIFO[N_PARTITIONS];

    public MSource(int size) {
        for (var i = 0; i < N_PARTITIONS; i++)
            fifos[i] = new FIFO(size);
    }

    /**
     * Puts a new record in the fifo
     *
     * @param record   the record to be put.
     */
    public void putRecord(String record) {
        int id = Integer.parseInt(record.split(":")[0]) % N_PARTITIONS;
        fifos[id].putRecord(record);
    }

    /**
     * Retrieves a record from the fifo
     *
     */
    public String getRecord(int partID) {
        return fifos[partID].getRecord();
    }

    /**
     * Simple FIFO implementation
     * 
     */
    class FIFO {
        final ReentrantLock rl;
        Condition cNotEmpty;
        Condition cNotFull;

        final String[] fifo;
        final int size;
        int idxPut = 0;
        int idxGet = 0;
        int count = 0;

        FIFO(int size){
            this.size = size;
            fifo = new String[size];
            rl = new ReentrantLock();
            cNotEmpty = rl.newCondition();
            cNotFull = rl.newCondition();
        }

        void putRecord(String record){
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

        String getRecord(){
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

        boolean isFull() {
            return count >= size;
        }

        boolean isEmpty() {
            return count <= 0;
        }
    }
}
