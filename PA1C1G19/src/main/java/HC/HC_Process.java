/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC;

import HC.Comunication.TSocketHandler;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author guids
 */
public class HC_Process {
    
    final static int portNumber = 5000;
    
    public static void main(String[] args) {

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
