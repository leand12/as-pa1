package UC1.PPRODUCER;


import UC1.SensorData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Properties;

/**
 * Kafka Producer thread
 */
public class TProducer extends Thread {

    private static int producerID = 0;
    private final Socket socket;

    /** Producer id */
    private final int id;
    private final Gui gui;
    private BufferedReader in;

    public TProducer(Socket socket, Gui gui) {
        this.socket = socket;
        this.id = producerID++;
        this.gui = gui;
        listenSocket();
    }


    /**
     * Create client socket
     */
    public boolean listenSocket() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (Exception e) {
            System.err.println("Could not create client socket");
            return false;
        }
    }

    @Override
    public void run() {
        
        // Kafka configurations

        //Assign topicName to string variable
        String topicName = "Sensor";
        // create instance for properties to access producer configs
        Properties props = new Properties();
        //Assign localhost id
        props.put("bootstrap.servers", "localhost:9092");
        //Set acknowledgements for producer requests.
        props.put("acks", "0");
        //If the request fails, the producer can automatically retry,
        props.put("retries", 0);
        //Specify buffer size in config
        props.put("batch.size", 16384);
        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);
        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<>(props);

        while (true) {
            try {
                String message = in.readLine();
                String[] messageData = message.split(":");
                SensorData data = new SensorData(messageData[0], Double.parseDouble(messageData[1]), Integer.parseInt(messageData[2]));

                // update GUI
                gui.updateCount(id, data); 
                System.out.println(data);

                // send data
                producer.send(new ProducerRecord<>(topicName, data.getID(), message));
                producer.flush();
            } catch (IOException e) {
                System.err.println("Error getting socket message");
                break;
            }
        }
        producer.close();
    }
}
