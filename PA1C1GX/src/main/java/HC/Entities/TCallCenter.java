package HC.Entities;

import HC.Data.ERoom_CC;
import HC.Data.Notification;
import HC.Monitors.*;

import java.util.HashMap;

import static HC.Data.ERoom_CC.*;


class Room {
    private Occupation both;
    private Occupation children;
    private Occupation adults;
    private int pendingCalls = 0;   // the calls from the CallCenter that were not completed
    private Room next;

    public Room(int occ, int maxOcc) {
        both = new Occupation(occ, maxOcc);
    }

    public Room(int childOcc, int childMaxOcc, int adultOcc, int adultMaxOcc) {
        children = new Occupation(childOcc, childMaxOcc);
        adults = new Occupation(adultOcc, adultMaxOcc);
    }

    private Occupation getAdults() {
        return both == null ? adults : both;
    }

    private Occupation getChildren() {
        return both == null ? children : both;
    }

    private int getMaxOcc() {
        return both == null ? adults.maxOcc + children.maxOcc : both.maxOcc;
    }

    private boolean hasChildren() {
        return getAdults().occ > 0;
    }

    private boolean hasAdults() {
        return getChildren().occ > 0;
    }

    private boolean isFullOfChildren() {
        Occupation c = getChildren();
        return c.occ >= c.maxOcc;
    }

    private boolean isFullOfAdults() {
        Occupation a = getAdults();
        return a.occ >= a.maxOcc;
    }

    private boolean isFullOfPendingCalls() {
        return pendingCalls >= getMaxOcc();
    }

    public void setNext(Room next) {
        this.next = next;
    }

    public Room getNext() {
        return next;
    }

    public boolean canCallPatient() {
        if (next == null)
            throw new IllegalCallerException("Room does not has a next room to move patient.");
        if (isFullOfPendingCalls())
            return false;
        return (hasAdults() && !next.isFullOfAdults()) ||
                (hasChildren() && !next.isFullOfChildren());
    }

    public void addPatient(TPatient patient) {
        Occupation o = patient.isAdult() ? getAdults() : getChildren();
        o.increment();
    }

    public void removePatient(TPatient patient) {
        Occupation o = patient.isAdult() ? getAdults() : getChildren();
        o.decrement();
        if (pendingCalls <= 0)
            throw new IllegalCallerException("Cannot decrement calls when it's empty.");
        pendingCalls--;
    }

    public void callPatient() {
        if (pendingCalls >= getMaxOcc())
            throw new IllegalCallerException("Cannot increment calls when it's full.");
        pendingCalls++;
    }

    class Occupation {
        private final int maxOcc;
        private int occ;

        public Occupation(int occ, int maxOcc) {
            this.occ = occ;
            this.maxOcc = maxOcc;
        }

        public void increment() {
            if (occ < maxOcc)
                throw new IllegalCallerException("Cannot increment occupation when it's full.");
            occ++;
        }

        public void decrement() {
            if (occ <= 0)
                throw new IllegalCallerException("Cannot decrement occupation when it's empty.");
            occ--;
        }
    }
}



public class TCallCenter extends Thread {
    private final ICCH_CallCenter cch;         // call center hall
    private final IETH_CallCenter eth;         // entrance hall
    private final IWTH_CallCenter wth;         // waiting hall
    private final IMDH_CallCenter mdh;         // medical hall
    private final IPYH_CallCenter pyh;         // payment hall

    private HashMap<ERoom_CC, Room> state = new HashMap<>();   // occupation state of the simulation
    private boolean auto = true;
    private boolean next = false;

    public TCallCenter(int NoS, int NoA, int NoC, ICCH_CallCenter cch, IETH_CallCenter eth, IWTH_CallCenter wth,
                       IMDH_CallCenter mdh, IPYH_CallCenter pyh) {
        this.cch = cch;
        this.eth = eth;
        this.wth = wth;
        this.mdh = mdh;
        this.pyh = pyh;

        int seats = NoS / 2;
        int total = NoA + NoC;

        Room reth = new Room(NoC, seats, NoA, seats);
        Room revh = new Room(0, 4);
        Room rwth = new Room(0, total);
        Room rwtri = new Room(0, seats, 0, seats);
        Room rmdw = new Room(0, 1, 0, 1);
        Room rmdri = new Room(0, 2, 0, 2);

        reth.setNext(revh);
        rwth.setNext(rwtri);
        rwtri.setNext(rmdw);
        rmdw.setNext(rmdri);

        state.put(ETH, reth);
        state.put(WTH, rwth);
        state.put(WTRi, rwtri);
        state.put(MDW, rmdw);
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void allowNextPatient() {
        this.next = true;
    }

    @Override
    public void run() {
        while (true) {
            // call patients
            while (state.get(ETH).canCallPatient()) {
                state.get(ETH).callPatient();
                eth.callPatient();
            }
            while (state.get(WTH).canCallPatient()) {
                state.get(WTH).callPatient();
                wth.callPatient();
            }
            while (state.get(WTRi).canCallPatient()) {
                state.get(WTRi).callPatient();
                wth.callPatient2();
            }
            while (state.get(MDW).canCallPatient()) {
                state.get(MDW).callPatient();
                mdh.callPatient();
            }

            // receive notification
            Notification notif = cch.getNotification();
            ERoom_CC roomType = notif.room;
            TPatient patient = notif.patient;

            // update state
            Room room = state.get(roomType);
            room.removePatient(patient);
            room.getNext().addPatient(patient);
        }
    }
}
