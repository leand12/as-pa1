/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC.Logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author guids
 */
public class Logging {

    private final BufferedWriter logfile;
    private final ReentrantLock rl;
    private final Condition cWrite;

    private boolean isWriting = false;

    public Logging() throws IOException {
        this.logfile = new BufferedWriter(new FileWriter("src/main/java/HC/Logging/log.txt"));
        this.rl = new ReentrantLock();
        this.cWrite = rl.newCondition();
    }

    public void log(String message) throws IOException {
        try {
            rl.lock();
            while (this.isWriting) {
                cWrite.await();
            }
            this.isWriting = true;
            System.out.println(message);
            this.logfile.write(message);
            this.logfile.newLine();
            this.logfile.flush();
            this.cWrite.signal();
            this.isWriting = false;
        } catch (InterruptedException ex) {
            return;
        } finally {
            rl.unlock();
        }
    }
}
