package HC.Monitors;

import HC.Data.Notification;

public interface ICCH_CallCenter {
    /**
     * Return the last Patient notification saved in the monitor to the CallCenter.
     */
    Notification getNotification();
}
