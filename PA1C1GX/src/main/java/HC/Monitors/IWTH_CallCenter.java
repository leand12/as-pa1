package HC.Monitors;

public interface IWTH_CallCenter extends IHall_CallCenter {
    /**
     * Signal a Patient the CallCenter allows moving on.
     * This method is specifically meant to call Patients in the WTRi.
     */
    void callPatient2();
}
