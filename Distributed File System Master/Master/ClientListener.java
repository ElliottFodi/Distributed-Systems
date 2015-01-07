import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class ClientListener extends Thread{

	private int port;
	private static int port_Client;
	private repository repo;
	
	public ClientListener(int p, repository passed_repo)
	{
		this.port = p;
		port_Client = p+1;
		repo = passed_repo;
	}
	
	public void run()
	{
		//create socket and open port
		ServerSocket server_Client = null;
		try {
			server_Client = new ServerSocket(this.port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//loop and accept client connections
		while(true)
		{
		  try{
			  
		       Socket socket = server_Client.accept();
		       
		       //debug print out
		       System.out.println("CLIENT: connection accept sending port ");
		       invoke_Client(socket);
		  }catch(IOException e){
			  e.printStackTrace();
		  }
		}
	}
	
	public synchronized void invoke_Client(Socket socket)
	{
		PrintWriter os = null;
		try {
			if(port_Client==port+2000)
				port_Client = port+1;
			os = new PrintWriter(socket.getOutputStream());
			
			//send new port t client
			os.println(port_Client);
			
			//create client handler thread
			HandleClientThread thread = new HandleClientThread(port_Client, repo);
			thread.start();
			port_Client+=1;
			os.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				os.close();
			}catch(Exception e){}
			try{
				socket.close();
			}catch(Exception e){}
		}
	}
}
