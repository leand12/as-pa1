package HC.Entities;

import HC.Data.Notifications;
import HC.Monitors.*;

import java.util.concurrent.TimeUnit;

public class TCallCenter extends Thread {
    private final IMCCH_CallCenter cch;         // call center hall
    private final IMETH_CallCenter eth;         // entrance hall
    private final IMWTH_CallCenter wth;         // waiting hall
    private final IMMDH_CallCenter mdh;         // medical hall
    private final IMPYH_CallCenter pyh;         // payment hall

    private Notifications notifications = new Notifications();
    private boolean auto = true;
    private boolean next = false;

    public TCallCenter(IMCCH_CallCenter cch, IMETH_CallCenter eth, IMWTH_CallCenter wth,
                       IMMDH_CallCenter mdh, IMPYH_CallCenter pyh) {
        this.cch = cch;
        this.eth = eth;
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

//    private boolean canCallPatient(IMonitor m1, IMonitor m2) {
//        return (m1.hasAdultrenReady() && !m2.isFullOfAdults()) ||
//                (m1.hasChildrenReady() && !m2.isFullOfChildren());
//    }

    // hasChildrenReady wasNotified

    @Override
    public void run() {
//        while (true) {
//            if (canCallPatient(eth, evh) && (auto || next)) {
//                eth.get();
//                next = false;
//            }
//            if (canCallPatient(evh, wth)) evh.get();
//            if (canCallPatient(wth, mdh) && (auto || next)) {
//                wth.get();
//                next = false;
//            }
//            if (canCallPatient(mdh, pyh)) mdh.get();
//
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        // merge monitor's notifications with call center's
        cch.getNotification();
    }
}
