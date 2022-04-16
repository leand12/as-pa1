package HC.Entities;

import HC.Monitors.IMDH_Doctor;

public class TDoctor extends Thread {
    private static int id = 0;
    private final IMDH_Doctor mdh;
    private final int roomDedicated;

    public TDoctor(IMDH_Doctor mdh) {
        this.mdh = mdh;
        roomDedicated = id++;
    }

    @Override
    public void run() {
        while (true) {
            mdh.examinePatient(roomDedicated);
        }
    }
}
