package HC.Monitors;

import HC.Data.Room;

public interface IMCCH_CallCenter {
    /**
     * Return the last notification collected by the monitor to the CallCenter
     */
    Room getNotification();
}
