package HC.Monitors;

import HC.Entities.TPatient;
import HC.Entities.TNurse;
import HC.Logging.Logging;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MEVH implements IMonitor {
    
    private final ReentrantLock rl;
    private final ReentrantLock rl1;
    private final Condition cRoom;
    private final Condition cNurse;
    private final Logging log;
    
    private TNurse[] nurses;
    private boolean roomOcupied[];
    
    private int patientCount=0;
    private boolean nurseAssign = false;
    
    public MEVH(Logging log){
        this.rl = new ReentrantLock();
        this.rl1 = new ReentrantLock();
        this.cRoom = this.rl.newCondition();
        this.cNurse = this.rl1.newCondition();
        this.nurses = new TNurse[4];
        this.roomOcupied = new boolean[4];
        this.log = log;
    }
    
    public void assignNurse(TNurse nurse){
        try {
            rl1.lock();
            while(nurseAssign){
                cNurse.await();
            }
            nurseAssign = true;
            for(int i=0; i<4; i++){
                if(nurses[i]== null){
                    nurses[i] = nurse;
                    break;
                }
            }
            nurseAssign=false;
            cNurse.signal();
        } catch (InterruptedException ex) {
        }finally{
            rl1.unlock();
        }
        
    }
    
    @Override
    public boolean hasAdults() {
        return this.patientCount>0;
    }

    @Override
    public boolean hasChildren() {
        return this.patientCount>0;
    }

    @Override
    public boolean isFullOfAdults() {
        return this.patientCount == 4;
    }

    @Override
    public boolean isFullOfChildren() {
        return this.patientCount == 4;
    }

    @Override
    public void put(TPatient patient) {
        try {
            rl.lock();

            for(int i=0; i<4; i++){
                // patient enters room
                if(!this.roomOcupied[i]){
                    this.roomOcupied[i] = true;
                    
                    String s = " ";
                    String str = s.repeat(i*5+1);
                    String str1 = s.repeat(((4-i)*4)-i);
                    if(patient.isAdult()){
                        log.log(String.format("%-4s|%13s|%s%1s%2d%s |%-15s|%-25s|%-4s", " ", " ",str, "A", patient.getETN(), str1," ", " ", " ", " ", " "));
                    }
                    else{
                        log.log(String.format("%-4s|%13s|%s%1s%2d%s |%-15s|%-25s|%-4s", " ", " ",str, "C", patient.getETN(),str1, " ", " ", " ", " ", " "));
                    }
                    patientCount++;
                    
                    nurses[i].assignDos(patient);
                    
                    if(patient.isAdult()){
                        log.log(String.format("%-4s|%13s|%s%1s%2d%s%s|%-15s|%-25s|%-4s", " ", " ",str, "A", patient.getETN(), patient.getDos().toString().charAt(0),str1, " ", " ", " ", " "));
                    }
                    else{
                        log.log(String.format("%-4s|%13s|%s%1s%2d%s%s|%-15s|%-25s|%-4s", " ", " ",str, "C", patient.getETN(), patient.getDos().toString().charAt(0),str1," ", " ", " ", " ", " "));
                    }
                    
                    break;
                }
                
            }

        }catch (IOException  | InterruptedException e) {
            System.err.println(e);
        }
        finally{
            rl.unlock();
        }
    }

    @Override
    public void get() {

    }
}
