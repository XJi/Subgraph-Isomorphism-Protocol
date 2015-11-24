import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import HelperClass.Communication;
import HelperClass.MatrixOps;
import HelperClass.commitOps;

/**
 * In Subgraph Isomorphism Protocol, verifier runs Server 
 * to do the following:
 * 	1. Receive and accept commitment(Q), where Q = a(G2)
 *  2. Send a random bit to the Prover(Client)
 *  	0: request permutation a
 *      1: request pi and Q' such that Q' = pi(G1)
 *  3. 	Verify the data received from Prover
 */
public class Verifier {
	private static Socket socket;
	private static int SERVERPORT = 6077;
	private static int[][] g1;
	private static int[][] g2;
	private static boolean fail;
	private static int Number_run;
	
	
	
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(SERVERPORT);
        socket = serverSocket.accept();
        String g1_string = Communication.receiveBuffer(socket);
        Communication.sendBuffer(socket, "1");
        String g2_string = Communication.receiveBuffer(socket);
        g1 = MatrixOps.convertToMatrix(g1_string);
        System.out.println("Printing G1 now");
        MatrixOps.matrix_print(g1);
        g2 = MatrixOps.convertToMatrix(g2_string);
        System.out.println("Printing G2 now");
        MatrixOps.matrix_print(g2);		
        Number_run = 0;
        try {
            while (Number_run < 5) {
            	Number_run ++;
            	
            	System.out.println("Run " + Number_run + "\nAccept.\n"
            			+ "Request the commitment of Q");
            	
            	/* Receive commitment(Q) */
                String Commitment_q = Communication.receiveBuffer(socket);
                System.out.println("The com_Q is:"+ Commitment_q);
                /*Send a random bit to prover and wait for prover's reply*/
                int bit = 0;
                Communication.sendBuffer(socket, ""+bit);
                /*int bit = (int)(Math.random()+0.5);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(bit);*/
              
                
                if (bit == 0) {
                	String msg1 = Communication.receiveBuffer(socket);	// G3
                	Communication.sendBuffer(socket, "1");
                	String msg2 = Communication.receiveBuffer(socket);  // P3
                	Communication.sendBuffer(socket, "1");
                	int[][] G3 = MatrixOps.convertToMatrix(msg1);
                	int[][] P3 = MatrixOps.convertToMatrix(msg2);
   		         System.out.println("Printing G3");
   		         MatrixOps.matrix_print(G3);
                	boolean didCommit = commitOps.checkCommit(Commitment_q,G3);
                	if (!didCommit) {
                		System.out.println("Failed in checkCommit(Commitment_q,G3) in bit = 0\n");
                		Communication.sendBuffer(socket, "-1");  
                		fail = true;
                		break;
                	}
                	int[][] Q = MatrixOps.permute(g2,P3);
                	boolean pass = commitOps.areEqual(Q,G3);
                	if (!pass) {
                		Communication.sendBuffer(socket, "-1");  
                		System.out.println("Failed in commitOps.areEqual(Q,G3) in bit = 0\n");
                		fail = true;
                		break;
                	}
                	
                } else {
                	String msg1 = Communication.receiveBuffer(socket);	//Qprime
                	Communication.sendBuffer(socket, "1");
                	String msg2 = Communication.receiveBuffer(socket);  //Pi
                	Communication.sendBuffer(socket, "1");
                	int[][] Qprime = MatrixOps.convertToMatrix(msg1);
                	int[][] Pi = MatrixOps.convertToMatrix(msg2);
                	boolean didCommit = commitOps.checkCommit(Commitment_q, Qprime);
                	if (!didCommit) {
                		Communication.sendBuffer(socket, "-1");  
                		System.out.println("Failed in checkCommit(Commitment_q, Qprime) in bit = 1\n");
                		fail = true;
                		break;
                	}
                	int[][] Qcheck = MatrixOps.permute(g1, Pi);
                	boolean pass = commitOps.areEqual(Qcheck,Qprime);
                	if (!pass) {
                		Communication.sendBuffer(socket, "-1");  
                		System.out.println("Failed in areEqual(Qcheck,Qprime) in bit = 1\n");
                		fail = true;
                		break;
                	}
                	
                }
            }
            if(fail) {
            	System.out.println("Failed in run " + Number_run + ".");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            socket.close();
        }
    }
}