/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rip;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Gustavo
 */
public class RIP {
    public static final int NUM_NODES = 4;
    
    private static final int[][] ADJ_LIST = {
        {1, 2, 3},
        {0, 2},
        {0, 1, 3},
        {0, 2}
    };
    
    private static final int[][] INITIAL_COSTS = {
        {0, 1, 3, 7},
        {1, 0, 1, 999},
        {3, 1, 0, 2},
        {7, 999, 2, 0}
    };
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // Read node id
        Scanner scanner = new Scanner(System.in);
        int nodeId = scanner.nextInt();
        scanner.nextLine();
        
        // Create node
        Node node = new Node(nodeId, ADJ_LIST[nodeId], INITIAL_COSTS[nodeId]);
        
        // Initialize server
        node.initServer();
        
        // Wait for all servers to be initialized
        System.out.println("Esperando inicialização dos servidores.");
        System.out.print("Digite enter para continuar.");
        scanner.nextLine();
        
        // Initialize clients later
        node.initClients();
        
        // Send ready notification
        node.ready();
    }
    
}
