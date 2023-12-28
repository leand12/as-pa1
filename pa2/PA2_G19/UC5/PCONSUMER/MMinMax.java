package UC5.PCONSUMER;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Monitor to store the maximum and minimum temperatures of the records that are read from the Kafka Cluster.
 */
public class MMinMax {

    /** Number of consumer groups. */
    private final int N_GROUPS = 3;
    /** Shared regions with the maximum and minimum temperatures of each consumer group. */
    private final SharedRegion[] regions = new SharedRegion[N_GROUPS];

    public MMinMax() {
        for (var i = 0; i < N_GROUPS; i++)
            regions[i] = new SharedRegion();
    }

    /**
     * Attempt to change the minimum temperature of a given shared region of a consumer group.
     *
     * @param groupID       the ID of the consumer group
     * @param temperature   a suggestion for a new minimum temperature
     */
    public void putMinTemperature(int groupID, double temperature) {
        regions[groupID].putMinTemperature(temperature);
    }

    /**
     * Attempt to change the maximum temperature of a given shared region of a consumer group.
     *
     * @param groupID       the ID of the consumer group
     * @param temperature   a suggestion for a new maximum temperature
     */
    public void putMaxTemperature(int groupID, double temperature) {
        regions[groupID].putMaxTemperature(temperature);
    }

    /**
     * Retrieve the minimum temperature across all consumer groups following a Voting Replication Tactic.
     *
     * @return  the most common minimum or the true minimum temperature in case all proposed temperatures are different.
     */
    public double getMinTemperature() {
        // collect all minimum temperatures
        double[] values = new double[regions.length];
        for (var i = 0; i < regions.length; i++) {
            values[i] = regions[i].minTemperature;
        }
        // return the most common temperature if existent
        var mostVoted = findMostVoted(values);
        if (mostVoted != null) return mostVoted;

        // return the minimum of all proposed temperatures
        return Arrays.stream(values).min().orElseThrow();
    }

    /**
     * Retrieve the maximum temperature across all consumer groups following a Voting Replication Tactic.
     *
     * @return  the most common maximum or the true maximum temperature in case all proposed temperatures are different.
     */
    public double getMaxTemperature() {
        // collect all maximum temperatures
        double[] values = new double[regions.length];
        for (var i = 0; i < regions.length; i++) {
            values[i] = regions[i].maxTemperature;
        }
        // return the most common temperature if existent
        var mostVoted = findMostVoted(values);
        if (mostVoted != null) return mostVoted;

        // return the minimum of all proposed temperatures
        return Arrays.stream(values).max().orElseThrow();
    }

    /**
     * Return the most common value of an array or null if non-existent.
     *
     * @param a     the array evaluated.
     * @return      the most common value or null.
     */
    private Double findMostVoted(double[] a) {
        if (a == null || a.length == 0)
            return null;

        Arrays.sort(a);

        double previous = a[0];
        double popular = a[0];
        int count = 1;
        int maxCount = 1;

        for (int i = 1; i < a.length; i++) {
            if (a[i] == previous)
                count++;
            else {
                if (count > maxCount) {
                    popular = a[i-1];
                    maxCount = count;
                }
                previous = a[i];
                count = 1;
            }
        }
        return count > maxCount ? null : popular;
    }

    /**
     * A thread-safe shared region to store a minimum temperature and a maximum temperature.
     */
    class SharedRegion {
        final ReentrantLock rl;

        double minTemperature = Double.POSITIVE_INFINITY;
        double maxTemperature = Double.NEGATIVE_INFINITY;

        SharedRegion() {
            rl = new ReentrantLock();
        }

        /**
         * Change the minimum temperature if lower than the one stored.
         *
         * @param temperature   the new temperature
         */
        void putMinTemperature(double temperature){
            rl.lock();
            if (temperature < minTemperature) {
                minTemperature = temperature;
            }
            rl.unlock();
        }

        /**
         * Change the maximum temperature if higher than the one stored.
         *
         * @param temperature   the new temperature
         */
        void putMaxTemperature(double temperature){
            rl.lock();
            if (temperature > maxTemperature) {
                maxTemperature = temperature;
            }
            rl.unlock();
        }
    }
}


