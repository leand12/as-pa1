/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Monitors;
import HC.Entities.TPatient;
import HC.Logging.Logging;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.Math;

/**
 *
 * @author guids
 * Entrance Hall Monitor
 */
public class METH {
    
    private Logging log;
    
    private int NoS;
    private int ETN = 0; // Patient Number
    
    private TPatient adultFIFO[]; 
    private TPatient childFIFO[];
    
    private Condition[] carrayAdult;
    private Condition[] carrayChild;
        
    private int putAdultIdx = 0;
    private int putChildIdx = 0;
    
    private int getAdultIdx = 0;
    private int getChildIdx = 0;
    
    private int adultCount = 0;
    private int childCount = 0;
    
    private int ttm = 0;
    
    private ReentrantLock rl;
    private Condition cChild;
    private Condition cAdult;
    
    public METH(int NoS, int ttm, Logging log){
        this.NoS = NoS/2;
        this.ttm = ttm;
        
        adultFIFO = new TPatient[this.NoS];
        childFIFO = new TPatient[this.NoS];
        
        carrayAdult = new Condition[this.NoS];
        carrayChild = new Condition[this.NoS];
        
        this.rl = new ReentrantLock();
        this.cAdult = rl.newCondition();
        this.cChild = rl.newCondition();
        
        this.log = log;
    }
    
    // Used by a patient in order to enter the Hall
    public int put(TPatient patient) throws IOException{
        int pETN;
        try {
            rl.lock();
            
            if(patient.isAdult()){
                while(adultCount == NoS){
                    cAdult.await();
                }
                adultCount++;
                pETN = ETN;
                ETN++;
                
                log.log(String.format("%-4s| %1s%2d %8s|%-21s|%-15s|%-25s|%-4s", " ","A", pETN, " ", " ", " "," ", " "));
                
                // Move from ETH to ETR
                Thread.sleep((int) Math.floor(Math.random()* ttm));
                
                log.log(String.format("%-4s| %3s %1s%2d %4s|%-21s|%-15s|%-25s|%-4s", " "," ", "A", pETN, " ", " ", " "," ", " "));
                
                int tempIdx = putAdultIdx;
                putAdultIdx = (putAdultIdx+1) % NoS;
                adultFIFO[tempIdx] = patient;
                carrayAdult[tempIdx] = rl.newCondition();
                carrayAdult[tempIdx].await();
                
            }
            else{
                while(childCount == NoS){
                    cChild.await();
                }
                childCount++;
                pETN = ETN;
                ETN++;
                log.log(String.format("%-4s| %1s%2d %8s|%-21s|%-15s|%-25s|%-4s", " ","C", pETN, " ", " ", " "," ", " "));
                
                // Move from ETH to ETR
                Thread.sleep((int) Math.floor(Math.random()* ttm));
                
                log.log(String.format("%-4s| %8s%1s%2d |%-21s|%-15s|%-25s|%-4s", " "," ","C", pETN, " ", " ", " "," ", " "));
                
                int tempIdx = putChildIdx;
                putChildIdx = (putChildIdx+1) % NoS;
                childFIFO[tempIdx] = patient;
                carrayChild[tempIdx] = rl.newCondition();
                carrayChild[tempIdx].await();
                
                
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
            if(adultFIFO[(getAdultIdx+NoS-1)%NoS].getETN() < childFIFO[(getAdultIdx+NoS-1)%NoS].getETN()){
                adultCount--;
                carrayAdult[getAdultIdx].signal();
                getAdultIdx = (getAdultIdx+1) % NoS;
                cAdult.signal();
            }
            else{
                childCount--;
                carrayChild[getChildIdx].signal();
                getChildIdx = (getChildIdx+1) % NoS;
                cChild.signal();          
            }
        }
        else if(adultCount != 0){
            adultCount--;
            carrayAdult[getAdultIdx].signal();
            getAdultIdx = (getAdultIdx+1) % NoS;
            cAdult.signal();
        }
        else if(childCount !=0){
            childCount--;
            carrayChild[getChildIdx].signal();
            getChildIdx = (getChildIdx+1) % NoS;
            cChild.signal();
        }
    }
}


