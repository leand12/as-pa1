package HC.Entities;

import HC.Data.ERoom_CC;
import HC.Monitors.*;

import java.util.HashMap;

import static HC.Data.ERoom_CC.*;


class Room {
    private Occupation both;
    private Occupation children;
    private Occupation adults;
    private int pendingCalls = 0;   // the calls from the CallCenter that were not completed
    private Room next;
    private final boolean needsCall;
    private final String nome;

    public Room(String nome, int occ, int maxOcc, boolean needsCall) {
        this.nome = nome;
        both = new Occupation(occ, maxOcc);
        this.needsCall = needsCall;
    }

    public Room(String nome, int childOcc, int childMaxOcc, int adultOcc, int adultMaxOcc, boolean needsCall) {
        this.nome = nome;
        children = new Occupation(childOcc, childMaxOcc);
        adults = new Occupation(adultOcc, adultMaxOcc);
        this.needsCall = needsCall;
    }

    private Occupation getAdults() {
        return both == null ? adults : both;
    }

    private Occupation getChildren() {
        return both == null ? children : both;
    }

    private boolean hasChildrenToCall() {
        return getChildren().occ > getChildren().pendingCalls;
    }

    private boolean hasAdultsToCall() {
        return getAdults().occ > getAdults().pendingCalls;
    }

    private boolean isFullOfChildren() {
        Occupation c = getChildren();
        return c.occ >= c.maxOcc;
    }

    private boolean isFullOfAdults() {
        Occupation a = getAdults();
        return a.occ >= a.maxOcc;
    }

    private boolean isFullOfAdultPendingCalls() {
        return getAdults().pendingCalls >= next.getAdults().maxOcc - next.getAdults().occ;
    }

    private boolean isFullOfChildPendingCalls() {
        return getChildren().pendingCalls >= next.getChildren().maxOcc - next.getChildren().occ;
    }

    public void setNext(Room next) {
        this.next = next;
    }

    public Room getNext() {
        return next;
    }

    public int canCallPatient() {
        if (next == null)
            throw new IllegalCallerException("Room does not has a next room to move patient.");
        if (hasAdultsToCall() && !next.isFullOfAdults() && !isFullOfAdultPendingCalls())
            return 1;
        if (hasChildrenToCall() && !next.isFullOfChildren() && !isFullOfChildPendingCalls())
            return 2;
        return 0;
    }

    public void addPatient(TPatient patient) {
        Occupation o = patient.isAdult() ? getAdults() : getChildren();
        o.increment();
    }

    public void removePatient(TPatient patient) {
        Occupation o = patient.isAdult() ? getAdults() : getChildren();
        o.decrement();
        if (needsCall) {
            o.decrementCalls();
        }
    }

    public void callPatient(boolean isAdult) {
        if (isAdult)
            getAdults().incrementCalls();
        else
            getChildren().incrementCalls();
    }

    class Occupation {
        private final int maxOcc;
        private int pendingCalls;
        private int occ;

        public Occupation(int occ, int maxOcc) {
            this.occ = occ;
            this.maxOcc = maxOcc;
        }

        public void increment() {
            if (occ >= maxOcc)
                throw new IllegalCallerException("Cannot increment occupation when it's full.");
            occ++;
        }

        public void decrement() {
            if (occ <= 0)
                throw new IllegalCallerException("Cannot decrement occupation when it's empty.");
            occ--;
        }

        public void incrementCalls() {
            if (pendingCalls >= maxOcc)
                throw new IllegalCallerException("Cannot increment calls when it's full.");
            pendingCalls++;
        }

        public void decrementCalls() {
            if (pendingCalls <= 0)
                throw new IllegalCallerException("Cannot decrement calls when it's empty.");
            pendingCalls--;
        }
    }
}


public class TCallCenter extends Thread {
    private final ICCH_CallCenter cch;         // call center hall
    private final IETH_CallCenter eth;         // entrance hall
    private final IWTH_CallCenter wth;         // waiting hall
    private final IMDH_CallCenter mdh;         // medical hall

    private HashMap<ERoom_CC, Room> state = new HashMap<>();   // occupation state of the simulation
    private boolean auto = true;
    private boolean next = false;

    public TCallCenter(int NoS, int NoA, int NoC, ICCH_CallCenter cch, IETH_CallCenter eth, IWTH_CallCenter wth,
                       IMDH_CallCenter mdh) {
        this.cch = cch;
        this.eth = eth;
        this.wth = wth;
        this.mdh = mdh;

        int seats = NoS / 2;
        int total = NoA + NoC;

        Room reth = new Room("eth", NoC + NoA, NoS, true);
        Room revh = new Room("evh", 0, 4, false);
        Room rwth = new Room("wth", 0, total, true);
        Room rwtri = new Room("wtri", 0, seats, 0, seats, true);
        Room rmdw = new Room("mdw", 0, 1, 0, 1, true);
        Room rmdri = new Room("mdri", 0, 2, 0, 2, false);

        reth.setNext(revh);
        revh.setNext(rwth);
        rwth.setNext(rwtri);
        rwtri.setNext(rmdw);
        rmdw.setNext(rmdri);

        state.put(ETH, reth);
        state.put(EVH, revh);
        state.put(WTH, rwth);
        state.put(WTRi, rwtri);
        state.put(MDW, rmdw);
        state.put(MDRi, rmdri);
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
            int callType;

            // call patients
            callType = state.get(ETH).canCallPatient();
            if (callType != 0 && (auto || next)) {
                state.get(ETH).callPatient(callType == 1);
                eth.callPatient();
                next = false;
            }
            callType = state.get(WTH).canCallPatient();
            if (callType != 0 && (auto || next)) {
                state.get(WTH).callPatient(callType == 1);
                wth.callPatient(callType == 1);
                next = false;
            }
            callType = state.get(WTRi).canCallPatient();
            if (callType != 0 && (auto || next)) {
                state.get(WTRi).callPatient(callType == 1);
                wth.callPatient2(callType == 1);
                next = false;
            }
            callType = state.get(MDW).canCallPatient();
            if (callType != 0 && (auto || next)) {
                state.get(MDW).callPatient(callType == 1);
                mdh.callPatient(callType == 1);
                next = false;
            }

            // receive notification
            var notif = cch.getNotification();
            ERoom_CC roomType = notif.room;
            TPatient patient = notif.patient;

            // update state
            var room = state.get(roomType);
            room.removePatient(patient);
            var nextRoom = room.getNext();
            if (nextRoom != null) nextRoom.addPatient(patient);
        }
    }
}
