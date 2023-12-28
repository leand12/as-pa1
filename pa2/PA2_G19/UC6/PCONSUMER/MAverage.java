package UC6.PCONSUMER;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Monitor to store the information in a thread-safe region to calculate average temperature of the records that are
 * read from the Kafka Cluster.
 */
public class MAverage {

    /** Number of consumer groups. */
    private final int N_GROUPS = 3;
    /** The sum of all records' temperature received. */
    private int sumTemperature = 0;
    /** The count of all records received. */
    private int recordsCount = 0;

    private final ReentrantLock rl = new ReentrantLock();

    public MAverage() {}

    /**
     * Add a new record temperature to the temperature sum.
     *
     * @param temperature   the record temperature.
     */
    public void putTemperature(double temperature) {
        rl.lock();
        sumTemperature += temperature;
        recordsCount += 1;
        rl.unlock();
    }

    /**
     * Return the average temperature.
     */
    public double getAvgTemperature() {
        return sumTemperature / recordsCount;
    }
}


