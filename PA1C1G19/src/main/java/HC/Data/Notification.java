package HC.Data;

import HC.Entities.TPatient;

/**
 * An encapsulation of the notification items.
 */
public class Notification {
    public ERoom_CC room;       // the room from which a Patient left
    public TPatient patient;

    public Notification(ERoom_CC room, TPatient patient) {
        this.room = room;
        this.patient = patient;
    }

    @Override
    public String toString() {
        return String.format("<%s %s>", room, patient);
    }
}
