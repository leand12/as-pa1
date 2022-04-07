package HC.Entities;

import HC.Monitors.*;

public class TCallCenter extends Thread {
    private final METH eth;         // entrance hall
    private final MEVH evh;         // evaluation hall
    private final MWTH wth;         // waiting hall
    private final MMDH mdh;         // medical hall
    private final MPYH pyh;         // payment hall

    private boolean auto = true;
    private boolean next = false;

    public TCallCenter(METH eth, MEVH evh, MWTH wth, MMDH mdh, MPYH pyh) {
        this.eth = eth;
        this.evh = evh;
        this.wth = wth;
        this.mdh = mdh;
        this.pyh = pyh;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void allowNextPatient() {
        this.next = true;
    }

    private boolean canCallPatient(IMonitor m1, IMonitor m2) {
        /*return (m1.hasAdults() && !m2.isFullOfAdults()) ||
                (m1.hasChildren() && !m2.isFullOfChildren());*/
        return true;
    }

    @Override
    public void run() {
        while (true) {
            if (canCallPatient(eth, evh) && (auto || next)) {
                eth.get();
                next = false;
            }

            /*
            if (canCallPatient(evh, wth)) evh.get();
            if (canCallPatient(wth, mdh) && (auto || next)) {
                wth.get();
                next = false;
            }
            if (canCallPatient(mdh, pyh)) mdh.get();
            */
        }
    }
}
