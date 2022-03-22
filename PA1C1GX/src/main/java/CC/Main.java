/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CC;

import CC.GUI.GUI;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 *
 * @author guids
 */
public class Main {
    
    final static int portNumber = 5000;
    final static String hostName = "127.0.0.1";
    
    public static void main(String[ ] args) {
        
        GUI gui = new GUI();
        gui.setVisible(true);
        
        System.out.println("CC begining:");
        
        try{ 
            Socket sock = new Socket(hostName, portNumber);
            System.out.println(sock.getInetAddress());
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader in = new BufferedReader( new InputStreamReader(sock.getInputStream()));
            
            String inputLine;
            out.println("Hello I'm CC");
            while ((inputLine = in.readLine()) != null) {               
                System.out.println(inputLine);
            }
        }
        catch(Exception e){

        }
    }
    
}
