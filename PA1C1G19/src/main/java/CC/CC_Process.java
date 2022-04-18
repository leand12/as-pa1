/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CC;

import CC.GUI.GUI;
import CC.Communication.ClientSocket;


/**
 * The Control Center Process, where configuration, control and supervision takes place.
 *
 * @author guids
 */
public class CC_Process {

    public static void main(String[ ] args) {
        ClientSocket socket = new ClientSocket(5000, "127.0.0.1");
        socket.creatSocket();
        
        GUI gui = new GUI(socket);
        gui.setVisible(true);
        
        System.out.println("CC begining:");
    }
}
