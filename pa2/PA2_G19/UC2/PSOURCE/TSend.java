package UC2.PSOURCE;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * Thread responsible to send records to the producer via sockets
 */
public class TSend extends Thread {

    /**Monitor*/
    private final MSource mSource;
    private final Gui gui;
    private final String hostName;
    private final int portNumber;
    private PrintWriter out;


    public TSend(int portNumber, String hostName, MSource mSource, Gui gui) {
        this.gui = gui;
        this.mSource = mSource;
        this.portNumber = portNumber;
        this.hostName = hostName;
        createSocket();
    }


    @Override
    public void run() {
        while (true) {
            String record = mSource.getRecord();
            out.println(record);
            String record_id = record.split(":")[0];
            gui.updateCount(record_id);
        }
    }


    /**
     * Establishes a connection with the server socket in the producer
     */
    public boolean createSocket() {
        try {
            var socket = new Socket(this.hostName, this.portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not create client socket");
            return false;
        }
    }


}
