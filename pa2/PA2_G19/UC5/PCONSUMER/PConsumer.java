package UC5.PCONSUMER;


import java.util.Properties;

/**
 * Consumer main process
 */
public class PConsumer {

    /** Number of consumers groups */
    private static final int N_GROUP_CONSUMERS = 3;
    /** Number of kafka consumers. */
    private static final int N_KAFKA_CONSUMERS = 3;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Gui gui = new Gui(); 
        MMinMax monitor = new MMinMax();
        var tConsumers = new TConsumer[N_KAFKA_CONSUMERS];

        // setting kafka configurations
        var props = new Properties();
        props.put("acks", "all");
        props.put("bootstrap.servers", "localhost:9092");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        // initialize consumers work
        for (var gid = 0; gid < N_GROUP_CONSUMERS; gid++) {
            for (var i = 0; i < N_KAFKA_CONSUMERS; i++) {
                props.put("group.id", "Group" + gid);
                tConsumers[i] = new TConsumer((Properties) props.clone(), monitor, gui, gid);
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

        // print final result and update gui
        System.out.println("MIN TEMPERATURE: " + monitor.getMinTemperature());
        gui.updateMinTemperature(monitor.getMinTemperature());
        System.out.println("MAX TEMPERATURE: " + monitor.getMaxTemperature());
        gui.updateMaxTemperature( monitor.getMaxTemperature());
    }
}
