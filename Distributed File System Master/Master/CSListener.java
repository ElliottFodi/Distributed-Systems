import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class CSListener extends Thread{
	private int port;
	private repository repo;
	private static int port_CS;
	
	public CSListener(int p,repository passed_repo)
	{
		this.port = p;
		repo = passed_repo;
		port_CS = p+1;
	}
	
	public void run()
	{
		//create socket 
		ServerSocket server_ChunkServer = null;
		try {
			server_ChunkServer = new ServerSocket(this.port);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//loop and accept connections
		while(true)
		{
		  try{
			  
		       Socket socket = server_ChunkServer.accept();
		       
		       //debug print out
		       //System.out.println("Connection accepted: " + socket.getInetAddress().toString());
		       //pass in the socket to the handler
		       HandleCSThread thread = new HandleCSThread(socket, repo);
		       thread.start();
		       //invoke_ChunkServer(socket);
		  }catch(IOException e){
			  e.printStackTrace();
		  }
		}
	}
	
	
//	public synchronized void invoke_ChunkServer(Socket socket)
//	{
//		PrintWriter os = null;
//		try {
//			if(port_CS==port+2000)
//				port_CS = port+1;
//			os = new PrintWriter(socket.getOutputStream());
//			os.println(port_CS);
//			HandleCSThread thread = new HandleCSThread(port_CS,repo);
//			thread.start();
//			
//			//debug print out
//			System.out.println("Sending port: " + port_CS);
//			port_CS+=1;
//			os.flush();
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			try{
//				os.close();
//			}catch(Exception e){}
//			try{
//				socket.close();
//			}catch(Exception e){}
//		}
//	}

}
