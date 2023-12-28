package UC6.PCONSUMER;


import java.util.Properties;

/**
 * Consumer main process
 */
public class PConsumer {

    /** Number of consumer groups. */
    private static final int N_GROUP_CONSUMERS = 1;
    /** Number of kafka consumers. */
    private static final int N_KAFKA_CONSUMERS = 3;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Gui gui = new Gui(); 
        MAverage monitor = new MAverage();
        var tConsumers = new TConsumer[N_KAFKA_CONSUMERS];

        // set kafka properties
        var props = new Properties();
        props.put("acks", "all");
        props.put("bootstrap.servers", "localhost:9092");
        props.put("enable.auto.commit", "false");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        // initialize consumers work
        for (var gid = 0; gid < N_GROUP_CONSUMERS; gid++) {
            for (var i = 0; i < N_KAFKA_CONSUMERS; i++) {
                props.put("group.id", "Group" + gid);
                tConsumers[i] = new TConsumer((Properties) props.clone(), monitor, gui);
                tConsumers[i].start();
            }
            TConsumer.resetID();
        }

        // wait for consumers to finish
        try {
            for (var i = 0; i < N_KAFKA_CONSUMERS; i++) {
                tConsumers[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("AVG TEMPERATURE: " + monitor.getAvgTemperature());
        gui.updateAvgTemperature(monitor.getAvgTemperature());
    }
}
