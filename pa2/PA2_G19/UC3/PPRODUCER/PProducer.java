package UC3.PPRODUCER;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * Producer main process
 */
public class PProducer {

    private static ServerSocket serverSocket;
    private static final int portNumber = 5000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Gui gui = new Gui(); 

        // set kafka configurations 

        // create instance for properties to access producer configs
        var props = new Properties();
        // assign localhost id
        props.put("bootstrap.servers", "localhost:9092");
        // set acknowledgements for producer requests.
        props.put("acks", "all");
        // if the request fails, the producer can automatically retry,
        props.put("retries", 0);
        // specify buffer size in config
        props.put("batch.size", 16384);
        // reduce the no of requests less than 0
        props.put("linger.ms", 1);
        // the buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (Exception e) {
            System.err.println("Could not create server socket");
        }

        try {
            // wait for clients to join
            while (true) {
                Socket clientSocket = serverSocket.accept();
                var tProducer = new TProducer(props, clientSocket, gui);
                tProducer.start();
            }
        } catch (Exception e) {
            System.err.println("Socket error");
        }
    }

    /**
     * Close socket connection
     */
    public void closeSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket");
        }
    }
}
