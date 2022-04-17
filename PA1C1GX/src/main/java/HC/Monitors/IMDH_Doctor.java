package HC.Monitors;

public interface IMDH_Doctor {
    /**
     * Take the time to do the medical appointment and allow the Patient to move on.
     *
     * @param idx   the index from the Patient's room
     */
    void examinePatient(int idx);
}
