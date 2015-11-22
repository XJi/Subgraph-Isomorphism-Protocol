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
import HelperClass.FastFileWriter;


public class Prover {
	 private static Socket socket;
	 static int[][] matrix;
	 public static void main(String args[]){
	     try {
	         socket = new Socket("", 6077);
	        /* JFrame f = new JFrame();
	         JFileChooser fc = new JFileChooser();
	         int ret = fc.showOpenDialog(f);
			 if(ret==JFileChooser.APPROVE_OPTION){
				 File file = fc.getSelectedFile();
				 String name = file.getName();
				 System.out.println(name);
				 matrix = FileReader.readGraph(name);
				 f.setVisible(true);
				 
			 }*/
	         
	         /* Taylor's additions on 11/19 Here I will add in the functionality to generate the matrices G2, G1, as well as the permutations necessary to convert between them
	          *
	          */
//===========================================================================================
	         
	         //Intake size n from user
	         Scanner in = new Scanner(System.in);
	         System.out.println("Enter the size of the adjacency matrix or enter 0 for reading given files\n");
	         int matrixSize =Integer.parseInt(in.nextLine());
//============================================================================================	         
	         // Generate G2
	         int[][] G2 = MatrixOps.fill(new int[matrixSize][matrixSize],0.7);
	         String G2_string = MatrixOps.convertToString(G2);
	         FastFileWriter.WriteToANewFile("G2.txt", G2_string);   //Ugly, will fix later

//===============================================================================================
	        /*
	         * Generate G1 by generating the reduction matrices R and P1 a permutation
	         * Currently we do not need to store these as we will not ever send them directly
	         * 
	         * Naturally however we store P1
	         */
	         int[][] G1 =  MatrixOps.generateRemovalMatrix(G2);
	         String G1_string = MatrixOps.convertToString(G1);
	         FastFileWriter.WriteToANewFile("G1.txt", G1_string);  //Ugly, will fix later
	         Communication.sendBuffer(socket,G1_string);
	         
//=================================================================================================	         
	         // Generate G3, the permuted version of G2
	         int[][] P3 = MatrixOps.perm_mat(matrixSize);
	         int[][] G3 = MatrixOps.permute(new int[matrixSize][matrixSize], P3);
	         
//================================================================================================
	         String G3_string = MatrixOps.convertToString(G3);
	         Communication.sendBuffer(socket, G3_string);
	         //Permutation 3
	         String P3_string = MatrixOps.convertToString(P3);
	         Communication.sendBuffer(socket,G3_string);
	         GraphHash.hash_to_file(G3,"graphcommit.txt");
//===================================================================================================	         
	         //Commit to the permuted subgraph of G3 isomorphic to G1
	         
	         
	         /* Repeat the above essentially but do subgraph commit
	          * 
	          */
//==================================================================================================
	         // Send commits of G3, subgraph
	         
	         
//==================================================================================================
	         //Receive the challenge
	         String bitStr = Communication.receiveBuffer(socket); 
	         
	         /*
	          * If the challenge = 0, then we will send the files containing G3, P3
	          */
	         int bit = Integer.parseInt(bitStr);
	         if(bit == 0){
	        	 // Sending P3^-1
	        	 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	             out.println(MatrixOps.transpose(G3));
	             out.println();	
	         }
	         else{
	        	 // Sending P3^-1 P1
	        	 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	             out.println(MatrixOps.transpose(P3));
	             out.println(G1);
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