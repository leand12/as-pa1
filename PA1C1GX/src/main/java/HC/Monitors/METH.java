/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Monitors;
import HC.Threads.TPatient;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author guids
 * Entrance Hall Monitor
 */
public class METH {
    
    private int NoS;
    private int ETN = 0; // Patient Number
    
    private TPatient adultFIFO[]; 
    private TPatient childFIFO[];
    
    private Condition[] carrayAdult;
    private Condition[] carrayChild;
        
    private int adultIdx = 0;
    private int childIdx = 0;
    
    private int adultCount = 0;
    private int childCount = 0;
    
    private ReentrantLock rl;
    private Condition cChild;
    private Condition cAdult;
    
    public METH(int NoS){
        this.NoS = NoS/2;
        this.rl = new ReentrantLock();
        this.cAdult = rl.newCondition();
        this.cChild = rl.newCondition();
    }
    
    // Used by a patient in order to enter the Hall
    public int put(TPatient patient){
        
        int pETN = ETN;
        try {
            rl.lock();
            
            if(patient.isAdult()){
                while(adultCount == NoS){
                    cAdult.await();
                }
                adultFIFO[adultIdx] = patient;
                carrayAdult[adultIdx] = rl.newCondition();
                carrayAdult[adultIdx].await();
                adultIdx = (adultIdx+1) % NoS;
                ETN++;
                adultCount++;
            }
            else{
                while(childCount == NoS){
                    cChild.await();
                }
                childFIFO[childIdx] = patient;
                carrayChild[childIdx] = rl.newCondition();
                carrayChild[childIdx].await();
                childIdx = (childIdx+1) % NoS;
                ETN++;
                childCount++;
            }
            return pETN;
        } catch ( InterruptedException ex ) {return -1;}
        finally {
            rl.unlock();
            
        }
        
    }
    
    //A Patient can go to EVH
    public void get(){
        
        if(adultCount != 0 && childCount != 0){
            
            // remove adult
            if(adultFIFO[(adultIdx+NoS-1)%NoS].getETN() < childFIFO[(childIdx+NoS-1)%NoS].getETN()){
                adultCount--;
                carrayAdult[(adultIdx+NoS-1)%NoS].signal();
                cAdult.signal();
            }
            else{
                childCount--;
                carrayChild[(childIdx+NoS-1)%NoS].signal();
                cChild.signal();          
            }
        }
        else if(adultCount != 0){
            adultCount--;
            carrayAdult[(adultIdx+NoS-1)%NoS].signal();
            cAdult.signal();
        }
        else if(childCount !=0){
            childCount--;
            carrayChild[(childIdx+NoS-1)%NoS].signal();
            cChild.signal();
        }
    }
}


// TODO: Waiting times