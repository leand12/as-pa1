package HC.Data;

import java.util.HashMap;

/**
 * Storage to keep track of the rooms that need to be called
 */
public class Notifications {
    HashMap<Room, Integer> notifications = new HashMap<>();

    public Notifications() {
        for (Room r : Room.values()) {
            notifications.put(r, 0);
        }
    }

    public HashMap<Room, Integer> get() {
        return notifications;
    }

    public void put(Room room) {
        notifications.put(room, notifications.get(room) + 1);
    }

    public void merge(Notifications n) {
        HashMap<Room, Integer> nmap = n.get();
        notifications.forEach((k, v) -> nmap.merge(k, v, Integer::sum));
    }

    public boolean isEmpty() {
        for (int v : notifications.values()) {
            if (v != 0) return false;
        }
        return true;
    }
}
