/**
 * In Subgraph Isomorphism Protocol, prover's instruction:
 * 
 * 	1. For each round, create a random permutation
 *  2. Generate commitment(Q), where Q = a(G2)
 *  2. Send a random bit to the Prover(Client)
 *  	0: request permutation a
 *      1: request pi and Q' such that Q' = pi(G1)
 *  3. 	Verify the data received from Prover
 */
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import HelperClass.Communication;
import HelperClass.FileReader;
import HelperClass.GraphHash;
import HelperClass.MatrixOps;
import HelperClass.commitOps;
import HelperClass.FastFileWriter;


public class Prover {
	 private static Socket socket;
	 static int[][] matrix;
	 static int[][] G2;
	 
	 public static void main(String args[]){
	     try {
	         socket = new Socket("", 6077);
	         
	         //Intake size n from user
	         Scanner in = new Scanner(System.in);
	         System.out.println("Enter the size of the adjacency matrix or enter 0 for reading given files\n");
	         int matrixSize =Integer.parseInt(in.nextLine());
	         // Generate G2
	         
	         if (matrixSize == 0) {  // read from file
	        	 String file_G2 = "/g2";
	        	 G2 = FileReader.readGraph(file_G2);
	         } else G2 = MatrixOps.fill(new int[matrixSize][matrixSize],0.7);

	        /*
	         * Generate G1 by generating the reduction matrices R and P1 a permutation
	         * Currently we do not need to store these as we will not ever send them directly
	         */
	         int[][] R = MatrixOps.generateRemovalMatrix(new int[matrixSize][matrixSize]);
	         int[][] P1 = MatrixOps.perm_mat(matrixSize);
	         int[][] G1 = MatrixOps.multiply(R,G2);
	         G1 = MatrixOps.multiply(G1,R);
	         G1 = MatrixOps.permute(G1,P1);
	          
	         /* -- SEND G1, G2 TO VERIFIER -- */
	         
	         String G1_string = MatrixOps.convertToString(G1);
	         FastFileWriter.WriteToFormattedFile("G1_subgraphOfG2.txt", G1_string); 
	         Communication.sendBuffer(socket,G1_string);
	         Communication.receiveBuffer(socket);   //In case any package gets lost during transportation
	         String G2_string = MatrixOps.convertToString(G2);
	         FastFileWriter.WriteToFormattedFile("G2_graph.txt", G2_string);  
	         Communication.sendBuffer(socket,G2_string);	         
	         int Number_run = 0; 
	         
	         while (Number_run < 100) {
	        	 Number_run ++;
	        	 
		         // Generate G3, the permuted version of G2
		         int[][] P3 = MatrixOps.perm_mat(matrixSize);
		         int[][] G3 = MatrixOps.permute(G2,P3);
		         
		         // Send commitment
		         String commitString = commitOps.graphCommit(G3);
		         Communication.sendBuffer(socket,commitString);
		         
		         //Receive the challenge
		         String bitStr = Communication.receiveBuffer(socket); 
		         
		         int bit = Integer.parseInt(bitStr);
		         if(bit == 0){
		        	 /* --Send G3 and P3 --*/
		        	 Communication.sendBuffer(socket,MatrixOps.convertToString(G3));
		        	 Communication.receiveBuffer(socket);
		        	 Communication.sendBuffer(socket, MatrixOps.convertToString(P3));
		        	 Communication.receiveBuffer(socket);
		        	 
		        	 String pass = Communication.receiveBuffer(socket);
		        	 if (pass.equals("-1")) {
		        		 System.out.println("Failed in " + Number_run + ".");
		        		 break;
		        	 }
		         }
		         else{
		        	  /* Send Qprime, Pi */
		        	 int[][] Pi = MatrixOps.multiply(P3,MatrixOps.transpose(P1));
		        	 /* -- Compute Qprime -- */
		        	 int[][] Qprime = MatrixOps.permute(G1, Pi);
		        	 /* -- Send Qprime, Pi --*/
		        	 Communication.sendBuffer(socket, MatrixOps.convertToString(Pi));
		        	 Communication.receiveBuffer(socket);
		        	 Communication.sendBuffer(socket,MatrixOps.convertToString(Qprime)); 
		        	 Communication.receiveBuffer(socket);
		        	 
		        	 String pass = Communication.receiveBuffer(socket);
		        	 if (pass.equals("-1")) {
		        		 System.out.println("Failed in " + Number_run + ".");
		        		 break;
		        	 }
		         }
	         }
	     }
	      catch (Exception exception){
	          exception.printStackTrace();
	      }
	      finally{
	          //Closing the socket
	          try{
	                socket.close();
	          }
	          catch(Exception e){
	                e.printStackTrace();
	          }
	     }
	         /*
	          * If the challenge = 1, then we will compute the subgraph of G3 isomorphic to G1 as follows:
	          * Send Two matrices P3^{-1}RP1 as above, as described in the "ideas" document.
	          * 
	          */
	        
	}
}