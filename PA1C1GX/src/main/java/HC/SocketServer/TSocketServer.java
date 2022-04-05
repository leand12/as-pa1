/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.SocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author guids
 */
public class TSocketServer extends Thread {

    private final Socket socket;

    public TSocketServer(Socket socket) {
        this.socket = socket;
        System.out.println("Aqui");
    }

    @Override
    public void run() {

        try {
            System.out.println("Ali");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;

            while (true) {
                if ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    String[] clientMessage = inputLine.split(":");

                    switch (clientMessage[0]) {
                        case "CONFIG":
                            //TODO
                            break;
                        case "MODE":
                            //TODO
                            break;
                        case "NEXT":
                            //TODO
                            break;
                        case "END":
                            socket.close();
                            System.exit(0);
                            break;
                    }
                }

            }
        } catch (IOException e) {
            System.err.println("Socket error");
        }
    }
}
