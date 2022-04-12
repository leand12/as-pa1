package HC.Monitors;

import HC.Entities.TPatient;

public interface IMonitor_Patient {
    /**
     * Attempt to enter a {@code patient} inside a hall.
     *
     * @param patient   the Patient entering the hall
     */
    void enterPatient(TPatient patient);
}
