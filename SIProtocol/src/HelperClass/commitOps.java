package HelperClass;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;

//import MatrixOps;


public class commitOps {
	public static String convertToString(int[][] matrix){
		 String buffer = "";
		 int length = matrix[0].length;
		 for(int i = 0; i < length; i++){
			 for(int j = 0; j < length; j++)
				 buffer += matrix[i][j];
		 }
		 return buffer;
	 }
	public static String convertRowToString(int[][] matrix, int row){
		String buffer = "";
		int length = matrix[0].length;
		for (int i =0; i < length; i++){
			buffer += matrix[row][i];
		}
		return buffer;
	}
	
	
	/*public static int[][] generateQPrime(int[][] G2, int[][] R, int[][] P3){
		int[][] out = new int[G2[0].length][G2[0].length];
		out = MatrixOps.multiply(R,G2);
		out = MatrixOps.multiply(P3,out);
		out = MatrixOps.multiply(out,R);
		out = MatrixOps.multiply(out,MatrixOps.transpose(P3));
		
		return out;
	}
	*/

	//============================================================================	
	
	// Generates the rows of Q' so that we know which row commitments to open
	// Note that Q' must be computed first to be passed to this function
	public static int[] openList(int[][] QP){
		int t = QP[0].length;
		int[] rowList = new int[t];

		for(int i = 0; i < rowList.length; i++){
			boolean useRow = false;
			for(int j = 0; j < rowList.length; j++){
				if(QP[i][j]==1){useRow = true;}
			}
			if(useRow){rowList[i]=1;}
		}
		return rowList;
	}
	
	// hashes a single row of a matrix
	public static BigInteger singleRowHash(int[][] A, int n){
		try{
		String row = Integer.toString(n) + convertRowToString(A,n);
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(row.getBytes("UTF-8"));
		byte[] H = md.digest();
		
		BigInteger Hash = new BigInteger(H);
		return Hash;
		}
		catch (Exception e){
			e.printStackTrace();
			BigInteger fail = BigInteger.ZERO;
			return fail;
		}
	}
	
	
	// Generate row by row commitment string.
	public static String graphCommit(int[][] G){
		
		int len = G[0].length;
		String out = "";
		for(int i =0; i < len;i++){
			BigInteger I = singleRowHash(G,i);
			out = out+ I;
			
		}
		return out;
		
		
	}
	public static boolean areEqual(int[][] A, int[][] B){
		boolean equal = true;
		for(int i = 0; i < A[0].length;i++){
			for(int j=0; j < A[0].length;j++){
				if(A[i][j]!=B[i][j]){equal = false;}
			}
		}
		return equal;
		}
	
	
	// the BIG KAHUNA
	public static boolean checkCommit(String commit, int[][] QP){
		boolean out = true;
		//=================================
		// Copied from XJ's code
//		InputStream is = FileReader.class.getResourceAsStream(filename);
//		BufferedReader br = new BufferedReader(new InputStreamReader(is));
//		ArrayList<String> input = new ArrayList<>();
//		String s;
//		while((s = br.readLine())!= null) 
//			input.add(s);
//		br.close();
		//==================================
		
		boolean booltemp = false;
		int[] rowList = openList(QP);
		for(int i = 0; i < rowList.length; i++){
			if(rowList[i]==1){
				BigInteger I = singleRowHash(QP,i);
				String checkString = new String(I.toByteArray());
				booltemp = commit.contains(checkString);
				out = out && booltemp;
			}
		}
		return out;
	}
	//=============================================================================	
	public static void main(String[] args){
		int[][] A = new int[4][4];
		for(int i = 0; i < A[0].length;i++){
			A[0][i]=1;
			A[2][i]=1;
		}
		System.out.println("A= ");
		for(int i = 0; i < A[0].length;i++){
		System.out.println(Arrays.toString(A[i]));
			}
		String out = graphCommit(A);
		System.out.println(out);
		
		
	
		boolean b = checkCommit(out,A);
		System.out.print(b);
		
	}
}
