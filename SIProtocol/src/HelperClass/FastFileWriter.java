package HelperClass;

import java.io.*;

public class FastFileWriter{

   public static void WriteToANewFile(String relativePath, String content) throws IOException{
	   // Extract the relative path from the input string
	   if(relativePath.contains("/")){
		   String invstr = new StringBuilder(relativePath).reverse().toString();
		   relativePath = relativePath.substring(relativePath.length()-invstr.indexOf('/'), relativePath.length()-1);
		   
	   }
       File file = new File(relativePath);
       // creates the file
       file.createNewFile();
       // creates a FileWriter Object
       FileWriter writer = new FileWriter(file); 
       // Writes the content to the file
       writer.write(content); 
       writer.flush();
       writer.close();
   }
   
   public static void WriteToFormattedFile(String relativePath, String content) throws IOException{
	   // Extract the relative path from the input string
	   if(relativePath.contains("/")){
		   String invstr = new StringBuilder(relativePath).reverse().toString();
		   relativePath = relativePath.substring(relativePath.length()-invstr.indexOf('/'), relativePath.length()-1);
		   
	   }
       File file = new File(relativePath);
       // creates the file
       file.createNewFile();
       // creates a FileWriter Object
       FileWriter writer = new FileWriter(file); 
       int row = (int)Math.sqrt(content.length());
       for(int i = 0; i < row; i++){
    	   writer.write(content.substring(i*row, (i+1)*row)+"\n");
       }
       // Writes the content to the file
       writer.write(content); 
       writer.flush();
       writer.close();
   }
}