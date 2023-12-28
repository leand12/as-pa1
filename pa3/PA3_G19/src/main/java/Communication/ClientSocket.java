package Communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
*
*   Class to Create and manage a socket connection
*/
public class ClientSocket {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
    *
    *   Class constructor, receives a port number and a hostname to create a socket connection
    */
    public ClientSocket(int portNumber, String hostName) {
        try {
            this.socket = new Socket(hostName, portNumber);
            createSocket();
        } catch (IOException e) {
            System.err.println("Could not create client socket");
        }
    }

    /**
    *
    *   Class constructor, receives a socket connection
    */
    public ClientSocket(Socket socket) {
        this.socket = socket;
        try {
            createSocket();
        } catch (IOException e) {
            System.err.println("Could not create client socket");
        }
    }

    private void createSocket() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    public int getPort(){
        return socket.getLocalPort();
    }
    
    /**
     * Receive an object message.
     * @return message received
     */
    public Object getMessage(){
        Object reply = null;
        try{
            reply = in.readObject();
        } catch (NullPointerException | IOException | ClassNotFoundException ex) {
            System.err.println("Error receiving socket message");
        }
        return reply;
    }
    
    /**
     * Send an object message.
     * @param obj   the message object 
     * @return      true if successfully sent, false otherwise
     */
    public boolean sendMessage(Object obj){
        try{
            out.reset();
            out.writeObject(obj);
        } catch (NullPointerException | IOException e) {
            System.err.println("Error sending message");
            return false;
        }
        return true;
    }

    /**
    *
    *   Close the socket connection
    */
    public void closeSocket() {
        try {
            this.socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket");
        }
    }
}
