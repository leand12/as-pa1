package HC.Monitors;

import HC.Data.Room;

public interface IMCCH_Patient {
    /**
     * Notify the CallCenter there is an empty seat for the previous {@code room}.
     *
     * @param room  the room that from which a patient can move on
     */
    void notifyExit(Room room);
}
