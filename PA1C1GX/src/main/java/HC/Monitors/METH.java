/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Monitors;
import HC.Entities.TPatient;
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
        
    private int putAdultIdx = 0;
    private int putChildIdx = 0;
    
    private int getAdultIdx = 0;
    private int getChildIdx = 0;
    
    private int adultCount = 0;
    private int childCount = 0;
    
    private ReentrantLock rl;
    private Condition cChild;
    private Condition cAdult;
    
    public METH(int NoS){
        this.NoS = NoS/2;
        adultFIFO = new TPatient[this.NoS];
        childFIFO = new TPatient[this.NoS];
        
        carrayAdult = new Condition[this.NoS];
        carrayChild = new Condition[this.NoS];
        
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
                adultCount++;
                adultFIFO[putAdultIdx] = patient;
                carrayAdult[putAdultIdx] = rl.newCondition();
                carrayAdult[putAdultIdx].await();
                putAdultIdx = (putAdultIdx+1) % NoS;
                ETN++;
            }
            else{
                while(childCount == NoS){
                    cChild.await();
                }
                childCount++;
                childFIFO[putChildIdx] = patient;
                carrayChild[putChildIdx] = rl.newCondition();
                carrayChild[putChildIdx].await();
                putChildIdx = (putChildIdx+1) % NoS;
                ETN++;
                
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


