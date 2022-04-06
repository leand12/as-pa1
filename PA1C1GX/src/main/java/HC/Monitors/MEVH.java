package HC.Monitors;

import HC.Entities.TPatient;

public class MEVH implements IMonitor {
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
