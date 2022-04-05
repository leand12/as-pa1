/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC;

import HC.Main.GUI;
import HC.Comunication.TSocketHandler;
import HC.Entities.TPatient;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;

/**
 *
 * @author guids
 */
public class HC_Process {
    
    final static int portNumber = 5000;
    
    public static void main(String[] args) {
        
        GUI frame = new GUI(2, 2, 10);

        System.out.println("HC begining:");
        
        try{ 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            
            // wait for clients to join
            while(true){
                Socket clientSocket = serverSocket.accept();
                TSocketHandler socket = new TSocketHandler(clientSocket);
                socket.start();
            }
            
           
        }
        catch(Exception e){
            System.err.println("Socket error");
        }
    }
    
}
