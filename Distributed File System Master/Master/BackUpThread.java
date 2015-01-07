import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;


public class BackUpThread extends Thread{
	private repository repo;
	
	public BackUpThread(repository passed_in_repo)
	{
		this.repo = passed_in_repo;
	}
	
	public void run()
	{
		Socket socket = null;
		ObjectOutputStream os = null;

		try{
			while(true)
			{
				socket = new Socket("129.21.159.19",6000);
				os = new ObjectOutputStream(socket.getOutputStream());
				os.writeObject(repo.getMasterTable().ghost_getHashMaps());
				Thread.sleep(60000);
			}
		}catch(IOException e){
			e.printStackTrace();;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			try{
				socket.close();
			}catch(Exception e){}
			try{
				os.close();
			}catch(Exception e){}
		}
	}


}
