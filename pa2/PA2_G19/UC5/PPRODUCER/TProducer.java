package UC5.PPRODUCER;


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
    /** Producer properties */
    private final Properties props;
    private final Socket socket;

    /** Producer id */
    private final int id;
    /** Producer group id */
    private final Gui gui;
    private BufferedReader in;

    public TProducer(Properties props, Socket socket, Gui gui) {
        this.props = props;
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
        //Assign topicName to string variable
        String topicName = "Sensor";

        Producer<String, String> producer = new KafkaProducer<>(props);

        while (true) {
            try {
                String message = in.readLine();
                if (message.equals("END")) {
                    producer.send(new ProducerRecord<>(topicName, id, "END", ""));
                    break;
                }
                String[] messageData = message.split(":");
                SensorData data = new SensorData(messageData[0], Double.parseDouble(messageData[1]), Integer.parseInt(messageData[2]));

                // update GUI
                gui.updateCount(id, data);
                System.out.println(id + " -> " + data);

                // send message
                producer.send(new ProducerRecord<>(topicName, id, data.getID(), message));
                producer.flush();
            } catch (IOException e) {
                System.err.println("Error getting socket message");
                break;
            }
        }
        producer.close();
    }
}
