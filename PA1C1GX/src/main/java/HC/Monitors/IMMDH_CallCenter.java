package HC.Monitors;

public interface IMMDH_CallCenter extends IMonitor_CallCenter {
    /**
     * Signal a Patient the CallCenter allows moving on.
     * This method is specifically meant to call Patients in the MDRi.
     */
    void callPatient2();
}
