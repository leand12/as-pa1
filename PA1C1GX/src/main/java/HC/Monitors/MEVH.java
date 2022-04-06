package HC.Monitors;

import HC.Entities.TPatient;
import HC.Entities.TNurse;
import HC.Logging.Logging;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MEVH implements IMonitor {
    
    private final ReentrantLock rl;
    private final Condition cRoom;
    private final Logging log;
    
    private TNurse[] nurses;
    
    public MEVH(){
        this.rl = new ReentrantLock();
        this.cRoom = this.rl.newCondition();
        this.nurses = new TNurse[4];
    }
    
    public void assignNurse(){
    
    }
    

    @Override
    public boolean hasAdults() {
        return false;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean isFullOfAdults() {
        return true;
    }

    @Override
    public boolean isFullOfChildren() {
        return true;
    }

    @Override
    public void put(TPatient patient) {

    }

    @Override
    public void get() {

    }
}
