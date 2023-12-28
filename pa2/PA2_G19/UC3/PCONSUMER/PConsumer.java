package UC3.PCONSUMER;


import java.util.Properties;

/**
 * Consumer main process
 */
public class PConsumer {

    /** Number of kafka consumers. */
    private static final int N_KAFKA_CONSUMERS = 3;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Gui gui = new Gui(); 

        // setting kafka configurations
        var group = "Group";
        var props = new Properties();
        props.put("group.id", group);
        props.put("acks", "all");
        props.put("retries", "1"); 
        // when using retries, set this to avoid the possibility that a re-tried message could be delivered out of order
        props.put("max.in.flight.requests.per.connection", "10");
        props.put("bootstrap.servers", "localhost:9092");
        // when enabled, the consumer commits its current offset for the partitions it is reading from back to Kafka
        // recommended to only set to false if required to do this manually
        props.put("enable.auto.commit", "true");
        // how often updated consumed offsets are written to ZooKeeper
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        // creating consumer threads
        for (var i = 0; i < N_KAFKA_CONSUMERS; i++) {
            var tConsumer = new TConsumer(props, gui);
            tConsumer.start();
        }
    }
}
