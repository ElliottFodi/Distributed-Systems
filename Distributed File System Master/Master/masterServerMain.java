import java.io.IOException;

public class masterServerMain {
	
	

	
	public void Service() throws IOException
	{
		System.out.println("Service starts...");
			
		int replicationFactor = 2;
		
		mastertable mt = new mastertable(replicationFactor);
		
		//pass repository
		repository repo = new repository(mt);
		
		//pass cs listener port and repository
		CSListener thread_CS = new CSListener(8888,repo);
		thread_CS.start();
		
		//pass client listener port and repository
		ClientListener thread_Client = new ClientListener(6666,repo);
		thread_Client.start();
		
		//pass repository
		CSLastContactChecker timeCheck = new CSLastContactChecker(repo);
		timeCheck.start();
		
		//pass repository
		LocalSystemInput sysIn = new LocalSystemInput(repo);
		sysIn.start();
		
		//send tables to ghost server
		BackUpThread backUpThread = new BackUpThread(repo);
		backUpThread.start();
		
		
	}

	public static void main(String[] args) throws IOException {
		new masterServerMain().Service();
	}

}
