package UC4.PCONSUMER;

/**
 * Consumer main process
 */
public class PConsumer {

    /** Number of kafka consumers. */
    private static final int N_KAFKA_CONSUMERS = 6;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Gui gui = new Gui();

        // creating consumer threads
        for (var i = 0; i < N_KAFKA_CONSUMERS; i++) {
            var tConsumer = new TConsumer(gui);
            tConsumer.start();
        }
    }
}
