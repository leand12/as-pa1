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
 * Handle the client socket messages to manipulate the simulation
 * according to the client's commands and configurations.
 *
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
        boolean callCenterState = true;
        TCashier cashier = null;
        TPatient[] patients = null;
        TNurse[] nurses = null;
        TDoctor[] doctors = null;
        
        GUI gui = null;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;

            int NoA =0, NoC=0, NoS, PT, ET, MAT, TTM;
            String mode;

            // Initializing log
            Logging log = new Logging();
            log.logHead();
            log.logState("INI");

            while (true) {
                // keep listening to incoming messages
                if ((inputLine = in.readLine()) != null) {
                    String[] clientMessage = inputLine.split(":");

                    switch (clientMessage[0]) {
                        //NoA:NoC:NoS:PT:ET:MAT:TTM:Mode
                        case "CONFIG":  // Start button
                            log.logState("RUN");

                            NoA = Integer.parseInt(clientMessage[1]);   // number of adults
                            NoC = Integer.parseInt(clientMessage[2]);   // number of children
                            NoS = Integer.parseInt(clientMessage[3]);   // number of seats
                            PT = Integer.parseInt(clientMessage[4]);    // payment time
                            ET = Integer.parseInt(clientMessage[5]);    // evaluation time
                            MAT = Integer.parseInt(clientMessage[6]);   // medical appointment time
                            TTM = Integer.parseInt(clientMessage[7]);   // time to move
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
                            callCenter.setAuto(callCenterState);
                            callCenter.start();
                            
                            nurses = new TNurse[4];
                            doctors = new TDoctor[4];
                            for (var i = 0; i < 4; i++) {
                                // Create Nurses
                                nurses[i] = new TNurse(evh);
                                nurses[i].start();
                                // Create Doctors
                                doctors[i] = new TDoctor(mdh);
                                doctors[i].start();
                            }
                            // Create Cashier
                            cashier = new TCashier(pyh);
                            cashier.start();
                            
                            patients = new TPatient[NoA + NoC];
                            // Create Adult Patients
                            for (var i = 0; i < NoA; i++) {
                                patients[i]= new TPatient(true, cch, eth, evh, wth, mdh, pyh);
                                patients[i].start();
                            }
                            // Create Child Patients
                            for (var i = 0; i < NoC; i++) {
                                patients[NoA+i] = new TPatient(false, cch, eth, evh, wth, mdh, pyh);
                                patients[NoA+i].start();
                            }
                            break;
                        case "MODE":     // Mode selection
                             {
                                if (clientMessage[1].equals("AUTO")) {
                                    log.logState("AUT");
                                    callCenterState = true;
                                    if (callCenter != null)
                                        callCenter.setAuto(true);
                                } else if (clientMessage[1].equals("MANUAL")) {
                                    log.logState("MAN");
                                    callCenterState = false;
                                    if (callCenter != null)
                                        callCenter.setAuto(false);
                                }
                            }
                            break;
                        case "NEXT":    // Move patient button
                            if (callCenter != null) {
                                callCenter.allowNextPatient();
                            }
                            break;
                        case "RUN":
                            callCenter.res();
                            cashier.res();
                            for(int i =0; i< 4; i++){
                                nurses[i].res();
                                doctors[i].res();
                            }
                            for(int i =0; i< NoA+NoC; i++){
                                patients[i].res();
                            }
                            
                            break;
                        case "SUS":     // Suspend button
                            for(int i =0; i< NoA+NoC; i++){
                                patients[i].sus();
                            }
                            for(int i =0; i< 4; i++){
                                nurses[i].sus();
                                doctors[i].sus();
                            }
                            callCenter.sus();
                            cashier.sus();
                            break;
                        case "STO":     // Stop button
                            callCenter.exit();
                            cashier.exit();
                            for(int i =0; i< 4; i++){
                                nurses[i].exit();
                                doctors[i].exit();
                            }
                            for(int i =0; i< NoA+NoC; i++){
                                patients[i].interrupt();
                            }
                            gui.dispose();
                            TNurse.resetId();
                            TDoctor.resetId();
                            break;
                        case "END":     // End button
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

/* http://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html */
