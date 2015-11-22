package HelperClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Communication {

	/**
	 * Receive a message
	 * @param Socket
	 * @throws IOException
	 * @return String of the data received from prover 
	 */
	public static String receiveBuffer(Socket socket){
		try{
			InputStream is = socket.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String buffer = br.readLine();
	        System.out.println("Message received from prover is : "+ buffer);
	        return buffer;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Send a message 
	 * 
	 * @param socket
	 * @param msg, the message which needs to be sent
	 */
	
	public static void sendBuffer(Socket socket, String msg){
		try{
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(msg +"\n");
	        System.out.println("Message sent to prover is : "+ msg);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
