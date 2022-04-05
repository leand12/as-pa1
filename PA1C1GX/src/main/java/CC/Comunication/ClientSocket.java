/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CC.Comunication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author guids
 */
public class ClientSocket {
    
    private int portNumber;
    private String hostName;
    private Socket socket;
    private PrintWriter out; 
    private BufferedReader in;
    
    public ClientSocket(int portNumber, String hostName){
        this.portNumber = portNumber;
        this.hostName = hostName;
    }
    
    public boolean creatSocket(){
        try{ 
            this.socket = new Socket(this.hostName, this.portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            return true;
        }
        catch(Exception e){
           System.err.println("Could not create client socket");
           return false;
        }
    }
    
    public String getMessage(){
        try{
            return in.readLine();
        }
        catch(IOException e){
            System.err.println("Error getting socket message");
            return null;
        }
        
    }
    
    public void sendMessage(String message){
        out.println(message);
    }
    
    public void closeSocket(){
        try{
            this.socket.close();
        }
        catch(IOException e){
            System.err.println("Error closing socket");
        }
        
    }
    
    
}
