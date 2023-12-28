package UC3.PCONSUMER;

import java.util.List;
import java.util.Properties;
import java.time.Duration;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import UC1.SensorData;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;


/**
 * Kafka Consumer thread
 */
public class TConsumer extends Thread {

    private static int consumerID = 0;
    private int lastTimestamp = -1;
    /** consumer id */
    private int id;
    private Gui gui;
    /** Kafka consumer properties */
    private final Properties props;

    public TConsumer(Properties props, Gui gui) {
        this.props = props;
        this.gui = gui;
        id = consumerID++;
    }

    @Override
    public void run() {
        var topic = "Sensor";

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.assign(List.of(new TopicPartition(topic, id)));

        System.out.println("Consumer " + id + " subscribed to topic " + topic);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            // read data
            for (ConsumerRecord<String, String> record : records){
                System.out.printf(id + " -> offset = %d, key = %s, value = %s\n",
                        record.offset(), record.key(), record.value());
                String sensorData[] = record.value().split(":");
                SensorData data = new SensorData(record.key(), Double.parseDouble(sensorData[1]) , Integer.parseInt(sensorData[2]));

                // update GUI
                gui.updateCount(id, data); 

                // check records are ordered
                int timestamp = data.getTimestamp();
                if (timestamp < lastTimestamp) {
                    throw new RuntimeException("Unordered data: " + timestamp + " > " + lastTimestamp);
                }
                lastTimestamp = timestamp;
            }
        }
    }
}
