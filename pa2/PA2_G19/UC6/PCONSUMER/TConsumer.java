package UC6.PCONSUMER;

import UC1.SensorData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * Kafka Consumer thread
 */
public class TConsumer extends Thread {

    private static int consumerID = 0;
    /** Kafka consumer properties */
    private final Properties props;
    /** monitor */
    private final MAverage monitor;
    /** consumer id */
    private final int id;
    private final Gui gui;

    public TConsumer(Properties props, MAverage monitor, Gui gui) {
        this.props = props;
        this.monitor = monitor;
        this.gui = gui;
        id = consumerID++;
    }

    public static void resetID() {
        consumerID = 0;
    }

    @Override
    public void run() {
        var topic = "Sensor";

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of(topic));

        System.out.println("Consumer " + id + " subscribed to topic " + topic);

        boolean stop = false;
        while (!stop) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            // read data
            for (ConsumerRecord<String, String> record : records) {
                if (record.key().equals("END")) {
                    stop = true;
                    break;
                }
                System.out.printf(id + " -> offset = %d, key = %s, value = %s\n",
                        record.offset(), record.key(), record.value());
                String[] sensorData = record.value().split(":");
                SensorData data = new SensorData(record.key(), Double.parseDouble(sensorData[1]), Integer.parseInt(sensorData[2]));
                
                // update GUI
                gui.updateCount(id, data);

                monitor.putTemperature(data.getTemperature());
                gui.updateAvgTemperature(monitor.getAvgTemperature());
            }
            // force consumer to commit offset, so that records do not need to be reprocessed after a crash
            consumer.commitSync();
        }
    }
}
