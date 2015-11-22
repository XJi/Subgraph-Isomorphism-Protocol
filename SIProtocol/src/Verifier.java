import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import HelperClass.MatrixOps;

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
	
	/**
	 * Receive commitment(Q) from the prover
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
	        System.out.println("Message received from prover is "+ buffer);
	        return buffer;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVERPORT);
        try {
            while (true) {
            	socket = serverSocket.accept();
            	System.out.println("Accept.\n"
            			+ "Request the commitment of Q");
            	/* Receive commitment(Q) */
                String s = receiveBuffer(socket);
                // Get commitment Q from the prover
                int[][] commitmentQ = MatrixOps.convertToMatrix(s);
                int bit = (int)(Math.random()+0.5);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(bit+"\n");
                String verify = receiveBuffer(socket);
                System.out.println("Need to verify; "+verify);
                
                
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