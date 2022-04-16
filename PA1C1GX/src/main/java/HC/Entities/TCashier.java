package HC.Entities;

import HC.Monitors.IPYH_Cashier;

public class TCashier extends Thread {
    private final IPYH_Cashier pyh;

    public TCashier(IPYH_Cashier pyh) {
        this.pyh = pyh;
    }

    @Override
    public void run() {
        while (true) {
            pyh.receivePayment();
        }
    }
}
