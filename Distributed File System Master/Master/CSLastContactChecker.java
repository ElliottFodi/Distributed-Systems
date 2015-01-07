import java.util.LinkedList;


public class CSLastContactChecker extends Thread{
	private repository repo;
	
	public CSLastContactChecker(repository passed_repo)
	{
		repo = passed_repo;
	}
	
	//false == not failed time is ok
	//true == failed contact time is to long
	//takes the last contacted time stamp as an argument
	public boolean isFailed(long time)
	{
		return System.currentTimeMillis()-time>120000;
		// if failed remove chunk server(cs_id)
	}
	
	public void run()
	{
		//LinkedList timeStamp = master.timeStamp;
		while(true)
		{
			int[] CSID = repo.getListOfCS();
			for(int i = 0; i < CSID.length; i++ )
		    {
				long lastContact = repo.getLastContactListForACS(CSID[i]);
				if(isFailed(lastContact) == true){
					repo.removeChunkServer(CSID[i]);
				}

		    }
			
			repo.client_checkForHiddenFiles();
			
	        try {
	        	//sleep for 1 min
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
