package HelperClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import HelperClass.MatrixOps;
public class FileReader {
	/*
	 * Read graph from the given file path. filePath is relative path. 
	 * Assume all testing files go in the same directory with FileReader
	 * 
	 * Return 2D adjacency matrix 
	 */
	public static int[][] readGraph(String filePath) throws IOException{
		InputStream is = FileReader.class.getResourceAsStream(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		ArrayList<String> input = new ArrayList<>();
		String s;
		while((s = br.readLine())!= null) 
			input.add(s);
		br.close();
		int graphSize = input.size();
		System.out.println("Graphsize: "+ graphSize);
		int[][] adjMatrix = new int[graphSize][graphSize];
		for(int i = 0; i < graphSize; i++){
			String temp = input.get(i);
			int k = 0;
			for(int j = 0; j < temp.length(); j++){
				if(temp.charAt(j)!= ' ') {
					adjMatrix[i][k++] = Character.getNumericValue(temp.charAt(j));
				}
			}
		}
		//MatrixOps.matrix_print(adjMatrix);
		return adjMatrix;
	}

}

