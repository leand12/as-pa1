/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Entities;

import HC.Data.EDoS;
import HC.Monitors.IEVH_Nurse;
import HC.Monitors.MEVH;

import java.util.Random;


/**
 * @author guids
 */
public class TNurse extends Thread {
    private static int id = 0;
    private final IEVH_Nurse evh;
    private final int roomDedicated;

    public TNurse(IEVH_Nurse evh) {
        this.evh = evh;
        roomDedicated = id++;
    }

    @Override
    public void run() {
        while (true) {
            evh.evaluatePatient(roomDedicated);
        }
    }

}
