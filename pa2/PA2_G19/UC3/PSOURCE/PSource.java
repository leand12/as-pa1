package UC3.PSOURCE;

import java.io.IOException;

/**
 * Source main process
 */
public class PSource {
    
    /** Number of kafka producers. */
    private static final int N_KAFKA_PRODUCERS = 3;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        Gui gui = new Gui(); 

        var mSource = new MSource(100);

        // creating one read thread 
        var tRead = new TRead("Sensor.txt", mSource);
        tRead.start();

        // creating send threads one for each producer
        for (var i = 0; i < N_KAFKA_PRODUCERS; i++) {
            var tSend = new TSend(5000, "127.0.0.1", mSource, gui);
            tSend.start();
        }
    }
}
