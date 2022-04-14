package HC.Monitors;

import HC.Data.ERoom;
import HC.Entities.TPatient;

public interface ICCH_Patient {
    /**
     * Notify the CallCenter there is an empty seat for the previous {@code room}.
     *
     * @param room      the room from which a Patient can move on
     * @param patient   the Patient that is leaving the
     */
    void notifyExit(ERoom room, TPatient patient);
}
