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
/* TODO: WILL CLEAN UP THIS MESS TOMORROW AFTERNOON; 
 * DO NOT TOUCH THIS CODE ON MASTER BEFORE THE MEETING; 
 * TRY TO CALL HELPER FUNCTIONS AS MUSH AS YOU CAN TO REDUCE
 *     THE LENGTH OF THE CODE AND MAKE IT MORE READABLE, BECAUSE WE ARE LAZY :P;
 * I'M GONNA GO TO SLEEP ZZZ...
 */
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import HelperClass.Communication;
import HelperClass.FileReader;
import HelperClass.GraphHash;
import HelperClass.MatrixOps;


public class Prover {
	 private static Socket socket;
	 static int[][] matrix;
	 /* TODO: change Q to the "real" adjacency matrix( OR
	  * any graph structure based on isomorphism implementations)
	  * variable called "perm" is the permutation, which doesn't 
	  * need to be String type
	  *  Here's one possible solution: 
	  *     1. A function of computing the permutation, which 
	  *        uses passed parameters in computation. This 
	  *        function is like an one-to-one mapping(Same pair 
	  *        of parameters should give the same permuting result),
	  *        otherwise Prover may have issues of verification. 
	  *        This function is available to both prover can verifier 
	  *        
	  *     2. After receiving either 0 or 1 from the verifier, 
	  *        prover can send a string which contains those parameters.
	  *        With the knowledge of those parameters, verifier can 
	  *        obtain Q     
	  *        
	  */
	 static int[][] Q = new int[10][10]; //TODO
	 static String perm = "Way of generating a random permutation"; //TODO
	 static String pi = "random stuff..."; //TODO pi for computing Q'
	 static String subQ = "subgraph of Q"; //TODO
	 
	 
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
	         int[][] pre_G2 = new int[matrixSize][matrixSize];
	         int[][] G2 = MatrixOps.fill(pre_G2,0.7);
	         String G2_string = MatrixOps.convertToString(G2);
	         Communication.sendBuffer(socket,G2_string);

//===============================================================================================
	        /*
	         * Generate G1 by generating the reduction matrices R and P1 a permutation
	         * Currently we do not need to store these as we will not ever send them directly
	         * 
	         * Naturally however we store P1
	         */
	         
//===============================================================================================	         
	         // Send G1 G2 to Verifier for storage.
	         //
	         
//=================================================================================================	         
	         // Generate G3, the permuted version of G2
	         int[][] pre_G3 = new int[matrixSize][matrixSize];
	         int[][] P3 = MatrixOps.perm_mat(matrixSize);
	         int[][] G3 = MatrixOps.permute(pre_G3, P3);
	         
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
	         
//==================================================================================================
	         /*
	          * If the challenge = 0, then we will send the files containing G3, P3
	          */
	         
	         /*
	          * If the challenge = 1, then we will compute the subgraph of G3 isomorphic to G1 as follows:
	          * Send Two matrices P3^{-1}RP1 as above, as described in the "ideas" document.
	          * 
	          */
//==================================================================================================
	         
	         matrix = FileReader.readGraph("/g1");
	         String buffer = MatrixOps.convertToString(matrix);
	         String sendMessage = buffer + "\n";
	         OutputStream os = socket.getOutputStream();
	         OutputStreamWriter osw = new OutputStreamWriter(os);
	         BufferedWriter bw = new BufferedWriter(osw);
	         bw.write(sendMessage);
	         bw.flush();
	         System.out.println("Message sent to the verifier : "+sendMessage);
	         //Get the return message from the server
	         InputStream is = socket.getInputStream();
	         InputStreamReader isr = new InputStreamReader(is);
	         BufferedReader br = new BufferedReader(isr);
	         String bitStr = br.readLine();
	         
	         // Getting the Challenge
	         System.out.println("Message received from the verifier : " + bitStr);
	         int bit = Integer.parseInt(bitStr);
	         if(bit == 0){
	        	 // Sending P3^-1
	        	 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	             out.println(perm);
	             out.println(Q);	
	         }
	         else{
	        	 // Sending P3^-1 P1
	        	 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	             out.println(pi);
	             out.println(subQ);
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
	}
}