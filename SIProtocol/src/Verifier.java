import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

import HelperClass.Communication;
import HelperClass.MatrixOps;
import HelperClass.commitOps;
import HelperClass.graph_hash;
import HelperClass.FileReader;

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
        String g1_string = Communication.receiveBuffer(socket);
        Communication.sendBuffer(socket, "1");
        String g2_string = Communication.receiveBuffer(socket);
        Communication.sendBuffer(socket, "1");
        int[][] g1 = MatrixOps.convertToMatrix(g1_string);
        int[][] g2 = MatrixOps.convertToMatrix(g2_string);
        		
        Number_run = 0;
        try {
            while (Number_run < 100) {
            	Number_run ++;
            	socket = serverSocket.accept();
            	System.out.println("Run " + Number_run + "\nAccept.\n"
            			+ "Request the commitment of Q");
            	
            	/* Receive commitment(Q) */
                String Commitment_q = Communication.receiveBuffer(socket);
                
                /*Send a random bit to prover and wait for prover's reply*/
                int bit = (int)(Math.random()+0.5);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(bit+"\n");
              
                
                if (bit == 0) {
                	String msg1 = Communication.receiveBuffer(socket);	// G3
                	Communication.sendBuffer(socket, "1");
                	String msg2 = Communication.receiveBuffer(socket);  // P3
                	Communication.sendBuffer(socket, "1");
                	int[][] G3 = MatrixOps.convertToMatrix(msg1);
                	int[][] P3 = MatrixOps.convertToMatrix(msg2);
                	boolean didCommit = commitOps.checkCommit(Commitment_q,G3);
                	if (!didCommit) {
                		Communication.sendBuffer(socket, "-1");  
                		fail = true;
                		break;
                	}
                	int[][] Q = MatrixOps.permute(g2,P3);
                	boolean pass = commitOps.areEqual(Q,G3);
                	if (!pass) {
                		Communication.sendBuffer(socket, "-1");  
                		fail = true;
                		break;
                	}
                	
                	/*
                	 * With the new implementation the verifier will recieve:
                	 * G3, P3
                	 * 
                	 * Then they will convert these two different things to strings
                	 * int[][] G3 = ----;
                	 * int[][] P3 = ----;
                	 * 
                	 * -- check that G3 given matches the commitment --
                	 * boolean didCommit = commitOps.checkCommit(Commitment_q,G3);
                	 * if(!didCommit){
                	 *  	break protocol 
                	 *  	}
                	 *  
                	 *  -- now check G2 ~= G3--
                	 *  int[][] Q = MatrixOps.permute(G2,P3);
                	 *  
                	 *  
                	 *  boolean pass = commitOps.areEqual(Q,G3);
                	 *  if(pass){ *print* "pass"}
                	 *  
                	 *  that pass above is essentialy the final pass.
                	 *  
                	 *  
                	 */
                } else {
                	String msg1 = Communication.receiveBuffer(socket);	//Qprime
                	String msg2 = Communication.receiveBuffer(socket);  //Pi
                	int[][] Qprime = MatrixOps.convertToMatrix(msg1);
                	int[][] Pi = MatrixOps.convertToMatrix(msg2);
                	boolean didCommit = commitOps.checkCommit(Commitment_q, Qprime);
                	if (!didCommit) {
                		Communication.sendBuffer(socket, "-1");  
                		fail = true;
                		break;
                	}
                	int[][] Qcheck = MatrixOps.permute(g1, Pi);
                	boolean pass = commitOps.areEqual(Qcheck,Qprime);
                	if (!pass) {
                		Communication.sendBuffer(socket, "-1");  
                		fail = true;
                		break;
                	}
                	
                	/*
                	 * Here is where we recieve the 
                	 * Qprime, A
                	 * 
                	 * int[][] Qprime = -----;
                	 * int[][] A = -------;
                	 * 
                	 * 
                	 * --this part checks the commitment matches--
                	 * 
                	 * boolean didCommit = commitOps.checkCommit(Commitment_q, Qprime);
                	 * if(!didCommit){ break protocol}
                	 * 
                	 * int[][] Qcheck = MatrixOps.permute(G1,A)
                	 * 
                	 * boolean pass = commitOps.areEqual(Qcheck,Qprime);
                	 *if(pass){ print "pass"}
                	 *
                	 * 
                	 */
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