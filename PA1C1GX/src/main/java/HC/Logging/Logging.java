/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Logging;

import HC.Data.ERoom;
import HC.Entities.TPatient;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author guids
 */
public class Logging {

    private final BufferedWriter logfile;
    private final ReentrantLock rl;
    private final ArrayList<ERoom> roomHeaders = new ArrayList<>(Arrays.asList(ERoom.values()));

    public Logging() throws IOException {
        this.logfile = new BufferedWriter(new FileWriter("src/main/java/HC/Logging/log.txt"));
        this.rl = new ReentrantLock();
    }

    public void logHead() {
        log(" STT | ETH ET1 ET2 | EVR1 EVR2 EVR3 EVR4 | WTH  WTR1 WTR2 | MDH  MDR1 MDR2 MDR3 MDR4 | PYH");
    }

    public void logState(String state) {
        log(String.format(" %-4s|%-13s|%-21s|%-16s|%-26s|%-4s", state, "", "", "", "", ""));
    }

    public void logPatient(ERoom room, TPatient patient) {
        var args = new String[1 + roomHeaders.size()];
        Arrays.fill(args, "");
        int index = 1 + roomHeaders.indexOf(room);
        if (index == -1) {
            throw new IllegalArgumentException("Room not recognized.");
        }
        args[index] = patient.toString();
        log(String.format(" %-4s| %-4s%-4s%-4s| %-5s%-5s%-5s%-5s| %-5s%-5s%-5s| %-5s%-5s%-5s%-5s%-5s| %-4s",
                (Object[]) args));
    }

    public void log(String message) {
        try {
            rl.lock();
            System.out.println(message);
            this.logfile.write(message);
            this.logfile.newLine();
            this.logfile.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            rl.unlock();
        }
    }
}
