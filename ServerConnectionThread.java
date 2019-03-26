/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gustavo
 */
public class ServerConnectionThread extends Thread {
    private final Socket socket;
    private final Node node;
    
    private int[] 
    
    private BufferedReader bufferedReader;
    
    public ServerConnectionThread(Socket socket, Node node) {
        this.socket = socket;
        this.node = node;
    }
    
    @Override
    public void run() {
        try {
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            bufferedReader = new BufferedReader(streamReader);
            
            while (this.isAlive()) {
                receive();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void receive() throws IOException {
        int isPacket = bufferedReader.read();
        
        if (isPacket == 0) {
            
        } else {
            // Receive packet
            Packet packet = new Packet();
            
            packet.sourceId = bufferedReader.read();
            packet.destId = bufferedReader.read();
            
            for (int i = 0; i < 4; ++i)
                packet.minCosts[i] = bufferedReader.read();
            
            // Update node
            synchronized (node) {
                node.update(packet);
            }
        }
    }
}
