package HC.Monitors;

public interface IEVH_Nurse {
    /**
     * Assign a DoS to a patient and allow the Patient to move on.
     *
     * @param idx   the index from the Patient's room
     */
    void evaluatePatient(int idx);
}
