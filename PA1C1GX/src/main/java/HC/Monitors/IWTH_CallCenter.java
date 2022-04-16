package HC.Monitors;

public interface IWTH_CallCenter {
    /**
     * Signal a Patient the CallCenter allows moving on.
     * This method is specifically meant to call Patients in the WTRi.
     */
    void callPatient(boolean isAdult);
    void callPatient2(boolean isAdult);
}
