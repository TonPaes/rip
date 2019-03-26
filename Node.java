/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rip;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author Gustavo
 */
public class Node {
    // Node id
    private final int id;
    
    // Neighbours
    private final int[] neighbours;
    
    // Distance matrix
    private int[][] distMatrix;
    
    // Client socket writers
    private BufferedWriter[] writers;

    public Node(int id, int[] neighbours, int[] nodeCosts) {
        this.id = id;
        this.neighbours = neighbours;
        
        // Create matrix
        this.distMatrix = new int[4][4];
        
        // Clear matrix
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
                this.distMatrix[i][j] = 999;
        
        // Initialize node costs
        this.distMatrix[this.id] = nodeCosts;
        for (int i = 0; i < 4; ++i)
            this.distMatrix[i][this.id] = nodeCosts[i];
    }
    
    public void initServer() {
        new ServerThread(this).start();
    }
    
    public void initClients() throws IOException {
        Socket[] sockets = new Socket[RIP.NUM_NODES];
        for (int i = 0; i < RIP.NUM_NODES; ++i)
            sockets[i] = new Socket("localhost", 3031 + i);
        
        OutputStreamWriter[] streams = new OutputStreamWriter[RIP.NUM_NODES];
        for (int i = 0; i < RIP.NUM_NODES; ++i)
            streams[i] = new OutputStreamWriter(sockets[i].getOutputStream());
        
        writers = new BufferedWriter[RIP.NUM_NODES];
        for (int i = 0; i < RIP.NUM_NODES; ++i)
            writers[i] = new BufferedWriter(streams[i]);
    }
    
    public void ready() throws IOException {
        for (int i = 0; i < 4; ++i) {
            writers[i].write(0);
            writers[i].write(this.id);
            writers[i].flush();
        }
    }
    
    private void sendPacket(Packet packet) throws IOException {
        writers[packet.destId].write(1);
        
        writers[packet.destId].write(packet.sourceId);
        writers[packet.destId].write(packet.destId);
        
        for (int i = 0; i < 4; ++i)
            writers[packet.destId].write(packet.minCosts[i]);
        
        writers[packet.destId].flush();
    }
    
    private void notify(int destId) throws IOException {
        Packet packet = new Packet();
        
        packet.sourceId = this.id;
        packet.destId = destId;
        
        packet.minCosts = distMatrix[this.id];
        
        sendPacket(packet);
    }
    
    public void update(Packet packet) throws IOException {
        boolean changed = false;
        
        for (int i = 0; i < 4; ++i) {
            int distance = distMatrix[this.id][packet.destId] + packet.minCosts[i];
            
            if (distance < distMatrix[this.id][i]) {
                distMatrix[this.id][i] = distance;
                changed = true;
            }
        }
        
        if (!changed)
            return;
        
        for (int neighbour : neighbours)
            notify(neighbour);
    }

    public int getId() {
        return this.id;
    }
}
