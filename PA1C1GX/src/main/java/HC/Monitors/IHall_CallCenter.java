package HC.Monitors;


public interface IHall_CallCenter {
    /**
     * Signal a Patient the CallCenter allows moving on
     */
    void callPatient(boolean isAdult);
}
