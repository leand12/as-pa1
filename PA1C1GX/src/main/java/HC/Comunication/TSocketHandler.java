/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Comunication;

import HC.Entities.TCallCenter;
import HC.Entities.TPatient;
import HC.Monitors.*;

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
        TCallCenter callCenter = null;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;

            int NoA, NoC, NoS, PT, ET, MAT, TTM;
            String mode;

            while (true) {
                if ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    String[] clientMessage = inputLine.split(":");

                    switch (clientMessage[0]) {
                        //NoA:NoC:NoS:PT:ET:MAT:TTM:Mode
                        case "CONFIG":
                            NoA = Integer.parseInt(clientMessage[1]);
                            NoC = Integer.parseInt(clientMessage[2]);
                            NoS = Integer.parseInt(clientMessage[3]);
                            PT = Integer.parseInt(clientMessage[4]);
                            ET = Integer.parseInt(clientMessage[5]);
                            MAT = Integer.parseInt(clientMessage[6]);
                            TTM = Integer.parseInt(clientMessage[7]);
                            mode = clientMessage[8];

                            // Create Monitors
                            var eth = new METH(NoS);
                            var evh = new MEVH();
                            var wth = new MWTH();
                            var mdh = new MMDH();
                            var pyh = new MPYH();

                            callCenter = new TCallCenter(eth, evh, wth, mdh, pyh);

                            // Create Adult Patients
                            for (var i = 0; i < NoA; i++) {
                                var p = new TPatient(true, eth, evh, wth, mdh, pyh);
                                p.start();
                            }

                            // Create Child Patients
                            for (var i = 0; i < NoA; i++) {
                                var p = new TPatient(false, eth, evh, wth, mdh, pyh);
                                p.start();
                            }

                            break;
                        case "MODE":
                            if (callCenter != null) {
                                if (clientMessage[1].equals("AUT")) {
                                    callCenter.setAuto(true);
                                } else if (clientMessage[1].equals("MAN")) {
                                    callCenter.setAuto(false);
                                }
                            }
                            break;
                        case "NEXT":
                            if (callCenter != null) {
                                callCenter.allowNextPatient();
                            }
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
