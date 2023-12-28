/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server.Entities;

import Entities.Request;
import Communication.ClientSocket;
import Utils.CodeMessages;
import Utils.RequestMessage;
import java.util.concurrent.locks.ReentrantLock;
import Server.GUI.GUI;
import Utils.Message;

/**
 * The representation of the Server.
 */
public class Server {

    /** The list of pending requests. */
    private final Request[] pendingRequests;
    /** The number of pending requests */
    private int nPendingRequest = 0;
    /** The number of current requests */
    private int ncurrentRequests = 0;
    /** The total number of iterations */
    private int totalIt = 0;
    /** The monitor socket */
    private ClientSocket monitorSocket;
    /** The threads list with the server workers */
    private final TWorker[] workers;
    /** The lock */
    private final ReentrantLock rl;
    /** The gui */
    private final GUI gui;
    /** The list of current requests */
    private final Request[] currentRequests;

    /**
     * Create an instance of Server.
     * @param socket    the socket
     * @param gui       the gui
     */
    public Server(ClientSocket socket, GUI gui) {
        workers = new TWorker[3];

        workers[0] = new TWorker(0, this);
        workers[1] = new TWorker(1, this);
        workers[2] = new TWorker(2, this);

        workers[0].start();
        workers[1].start();
        workers[2].start();

        pendingRequests = new Request[2];
        currentRequests = new Request[3];

        rl = new ReentrantLock();

        monitorSocket = socket;

        this.gui = gui;
    }

    /**
     * Adds new request
     * @param r     the request
     * @throws InterruptedException an exception
     */
    public void newRequest(Request r) throws InterruptedException {
        
        rl.lock();
        try {
            gui.addRequest(r);
            if ((nPendingRequest == 2 && ncurrentRequests == 3) || totalIt + r.nIterations() > 20) {
                // send reject message
                ClientSocket s = new ClientSocket(r.clientID(), "127.0.0.1");

                RequestMessage rm = new RequestMessage(CodeMessages.REPLY, r.clientID(),
                        r.requestID(), r.serverID(), 0, r.nIterations(), 0, r.deadline(), 3);

                s.sendMessage(rm);

                monitorSocket.sendMessage(rm);

                r.pi(0);
                gui.addReply(r);

            } else if (ncurrentRequests == 3) {
                pendingRequests[nPendingRequest] = r;
                nPendingRequest++;
                totalIt += r.nIterations();
            } else {

                for (int i = 0; i < 3; i++) {
                    if (currentRequests[i] == null) {
                        monitorSocket.sendMessage(new Message(CodeMessages.PROCESS).port(r.clientID()));
                        currentRequests[i] = r;
                        totalIt += r.nIterations();
                        workers[i].newWork(r.nIterations());
                        ncurrentRequests++;
                        break;
                    }
                }

            }
        } finally {
            rl.unlock();
        }
    }

    /**
     * Obtain results of process.
     * @param workerId  the worker ID
     * @param result    the result
     * @throws InterruptedException an exception
     */
    public void result(int workerId, double result) throws InterruptedException {

        rl.lock();
        try {
            Request r = currentRequests[workerId];

            totalIt -= r.nIterations();

            // send message to client
            ClientSocket s = new ClientSocket(r.clientID(), "127.0.0.1");

            RequestMessage rm = new RequestMessage(CodeMessages.REPLY, r.clientID(),
                    r.requestID(), r.serverID(), 0, r.nIterations(), result, r.deadline(), 2);

            s.sendMessage(rm);

            monitorSocket.sendMessage(rm);

            //update gui
            r.pi(result);
            gui.addReply(r);

            // worker is available
            currentRequests[workerId] = null;
            ncurrentRequests--;

            // process pending request
            if (nPendingRequest > 0) {
                int id = 1;
                if (pendingRequests[1] == null || (pendingRequests[1].deadline() > pendingRequests[0].deadline())) {
                    id = 0;
                }
                nPendingRequest--;
                currentRequests[workerId] = pendingRequests[id];
                workers[workerId].newWork(pendingRequests[id].nIterations());
                monitorSocket.sendMessage(new Message(CodeMessages.PROCESS).port(currentRequests[workerId].clientID()));
                ncurrentRequests++;
                pendingRequests[id] = null;
            }
        } finally {
            rl.unlock();

        }

    }

}
