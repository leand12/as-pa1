package HC.Monitors;

import HC.Data.Notification;

public interface ICCH_CallCenter {
    /**
     * Return the last notification collected by the monitor to the CallCenter
     */
    Notification getNotification();
}
