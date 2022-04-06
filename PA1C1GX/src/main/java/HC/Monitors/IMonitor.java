package HC.Monitors;

import HC.Entities.TPatient;

public interface IMonitor {
    boolean hasAdults();
    boolean hasChildren();

    boolean isFullOfAdults();
    boolean isFullOfChildren();

    void put(TPatient patient);
    void get();
}
