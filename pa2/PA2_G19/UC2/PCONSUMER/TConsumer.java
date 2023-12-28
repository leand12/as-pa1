package UC2.PCONSUMER;

import java.util.Properties;
import java.time.Duration;
import java.util.Arrays;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import UC1.SensorData;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Kafka Consumer thread
 */
public class TConsumer extends Thread {

    private static int consumerID = 0;
    /** consumer id */
    private int id = 0;
    private Gui gui;


    public TConsumer(Gui gui){
        this.gui = gui;
        id= consumerID;
        consumerID++;
    }

    @Override
    public void run() {

        // Kafka Configurations
        var topic = "Sensor";
        var group = "Group1";
        var props = new Properties();

        props.put("acks", "0");
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", group);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        TopicPartition partition = new TopicPartition(topic, id);
        consumer.assign(Arrays.asList(partition));

        System.out.println("Subscribed to topic " + topic + " and partition " + id);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            // read data
            for (ConsumerRecord<String, String> record : records){
                System.out.printf("offset = %d, key = %s, value = %s\n",
                        record.offset(), record.key(), record.value());
                String sensorData[] = record.value().split(":");
                SensorData data = new SensorData(record.key(), Double.valueOf(sensorData[1]) , Integer.valueOf(sensorData[2]) );

                // update GUI
                gui.updateCount(id, data); 
            }
        }
    }
}
