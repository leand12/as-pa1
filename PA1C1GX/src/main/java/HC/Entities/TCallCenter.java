package HC.Entities;

import HC.Monitors.*;

public class TCallCenter extends Thread {
    private final METH ETH;         // entrance hall
    private final MEVH EVH;         // evaluation hall
    private final MWTH WTH;         // waiting hall
    private final MMDH MDH;         // medical hall
    private final MPYH PYH;         // payment hall

    private boolean auto = true;
    private boolean next = false;

    public TCallCenter(METH ETH, MEVH EVH, MWTH WTH, MMDH MDH, MPYH PYH) {
        this.ETH = ETH;
        this.EVH = EVH;
        this.WTH = WTH;
        this.MDH = MDH;
        this.PYH = PYH;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void allowNextPatient() {
        this.next = true;
    }

    private boolean canCallPatient(IMonitor monitor1, IMonitor monitor2) {
        return (monitor1.hasAdults() && !monitor2.isFullOfAdults()) ||
                (monitor1.hasChildren() && !monitor2.isFullOfChildren());
    }

    @Override
    public void run() {
        while (true) {
            if (canCallPatient(ETH, EVH) && (auto || next)) {
                ETH.get();
                next = false;
            }
            if (canCallPatient(EVH, WTH)) EVH.get();
            if (canCallPatient(WTH, MDH) && (auto || next)) {
                WTH.get();
                next = false;
            }
            if (canCallPatient(MDH, PYH)) MDH.get();
        }
    }
}
