/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Comunication;

import HC.Logging.Logging;
import HC.Monitors.METH;
import HC.Entities.TPatient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author guids
 */
public class TSocketHandler extends Thread {

    private final Socket socket;

    public TSocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;
            
            int NoA, NoC, NoS, PT, ET, MAT, TTM;
            String mode;
            
            // Initializing log
            Logging log = new Logging();
            log.log("STT | ETH ET1 ET2 | EVR1 EVR2 EVR3 EVR4 | WTH WTR1 WTR2 | MDH MDR1 MDR2 MDR3 MDR4 | PYH");
            log.log(String.format("%-4s|%-13s|%-21s|%-15s|%-25s|%-4s", "INI", " ", " ", " ", " ", " "));

            while (true) {
                if ((inputLine = in.readLine()) != null) {
                    String[] clientMessage = inputLine.split(":");

                    switch (clientMessage[0]) {
                        //NoA:NoC:NoS:PT:ET:MAT:TTM:Mode
                        case "CONFIG":
                            
                            //INIT
                            log.log(String.format("%-4s|%-13s|%-21s|%-15s|%-25s|%-4s", "RUN", " ", " ", " ", " ", " "));
                            
                            NoA = Integer.parseInt(clientMessage[1]);
                            NoC = Integer.parseInt(clientMessage[2]);
                            NoS = Integer.parseInt(clientMessage[3]);
                            PT = Integer.parseInt(clientMessage[4]);
                            ET = Integer.parseInt(clientMessage[5]);
                            MAT = Integer.parseInt(clientMessage[6]);
                            TTM = Integer.parseInt(clientMessage[7]);
                            mode = clientMessage[8];
                            
                            
                            
                            
                            // Create Monitor
                            METH meth = new METH(NoS, TTM, log);
                            
                            // Create Adult Patients
                            for(int i =0; i<NoA; i++){
                                TPatient p = new TPatient(true, meth);
                                p.start();
                            }
                            
                            // Create Child Patients
                            for(int i =0; i<NoA; i++){
                                TPatient p = new TPatient(false, meth);
                                p.start();
                            }
                            
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
