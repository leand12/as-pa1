/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Comunication;

import HC.Entities.*;
import HC.Logging.Logging;
import HC.Main.GUI;
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
        GUI gui = null;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;

            int NoA, NoC, NoS, PT, ET, MAT, TTM;
            String mode;

            // Initializing log
            Logging log = new Logging();
            log.logHead();
            log.logState("INI");

            while (true) {
                if ((inputLine = in.readLine()) != null) {
                    String[] clientMessage = inputLine.split(":");

                    switch (clientMessage[0]) {
                        //NoA:NoC:NoS:PT:ET:MAT:TTM:Mode
                        case "CONFIG":

                            //INIT
                            log.logState("RUN");

                            NoA = Integer.parseInt(clientMessage[1]);
                            NoC = Integer.parseInt(clientMessage[2]);
                            NoS = Integer.parseInt(clientMessage[3]);
                            PT = Integer.parseInt(clientMessage[4]);
                            ET = Integer.parseInt(clientMessage[5]);
                            MAT = Integer.parseInt(clientMessage[6]);
                            TTM = Integer.parseInt(clientMessage[7]);
                            mode = clientMessage[8];

                            gui = new GUI(NoA, NoC, NoS);

                            // Create Monitors
                            var cch = new MCCH();
                            var eth = new METH(NoS, TTM, log, gui);
                            var evh = new MEVH(ET, TTM, log, gui);
                            var wth = new MWTH(NoS, TTM, log, gui);
                            var mdh = new MMDH(MAT, TTM, log, gui);
                            var pyh = new MPYH(PT, TTM, log, gui);

                            callCenter = new TCallCenter(NoS, NoA, NoC, cch, eth, wth, mdh);
                            callCenter.start();

                            for (var i = 0; i < 4; i++) {
                                // Create Nurses
                                new TNurse(evh).start();
                                // Create Doctors
                                new TDoctor(mdh).start();
                            }
                            // Create Cashier
                            new TCashier(pyh).start();
                            // Create Adult Patients
                            for (var i = 0; i < NoA; i++) {
                                new TPatient(true, cch, eth, evh, wth, mdh, pyh).start();
                            }
                            // Create Child Patients
                            for (var i = 0; i < NoA; i++) {
                                new TPatient(false, cch, eth, evh, wth, mdh, pyh).start();
                            }
                            break;
                        case "MODE":
                            if (callCenter != null) {
                                if (clientMessage[1].equals("AUT")) {
                                    log.logState("AUT");
                                    callCenter.setAuto(true);
                                } else if (clientMessage[1].equals("MAN")) {
                                    log.logState("MAN");
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
