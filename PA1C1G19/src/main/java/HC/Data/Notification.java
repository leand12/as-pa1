package HC.Data;

import HC.Entities.TPatient;

public class Notification {
    public ERoom_CC room;
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
