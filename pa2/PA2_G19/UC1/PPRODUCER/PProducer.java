package UC1.PPRODUCER;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


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

        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (Exception e) {
            System.err.println("Could not create server socket");
        }

        try {
            // wait for clients to join
            while (true) {
                Socket clientSocket = serverSocket.accept();
                var tProducer = new TProducer(clientSocket, gui);
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
