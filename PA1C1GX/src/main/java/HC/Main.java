/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HC;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author guids
 */
public class Main {
    
    final static int portNumber = 5000;
    
    public static void main(String[ ] args) {
        
        System.out.println("HC begining:");
        
        try{ 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            System.out.println(clientSocket.getLocalSocketAddress());
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
            
            String inputLine;
            out.println("Hello, this is HC");
            while ((inputLine = in.readLine()) != null) {               
                System.out.println(inputLine);
            }
        }
        catch(Exception e){

        }
    }
    
    

}
