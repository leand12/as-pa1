/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC;

import HC.GUI.GUI;
import HC.SocketServer.TSocketServer;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;

/**
 *
 * @author guids
 */
public class Main {
    
    final static int portNumber = 5000;
    
    public static void main(String[ ] args) {
        
        JFrame frame = new GUI();
        frame.setVisible(true);
        
        System.out.println("HC begining:");
        
        try{ 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            
            // wait for clients to join
            while(true){
                Socket clientSocket = serverSocket.accept();
                TSocketServer socket = new TSocketServer(clientSocket);
                socket.start();
            }
            
           
        }
        catch(Exception e){
            System.err.println("Socket error");
        }
    }
    
    
}
