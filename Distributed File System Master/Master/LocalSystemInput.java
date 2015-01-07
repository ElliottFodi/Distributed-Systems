import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;


public class LocalSystemInput extends Thread{
	private mastertable mt;
	private repository repo;
	
	public LocalSystemInput(repository passed_repo)
	{
		this.repo = passed_repo;
		this.mt = repo.getMasterTable();
	}
	
	public void run()
	{
		BufferedReader is = null;
		String str;
		try{
			while(true)
			{
				System.out.println("press 1 to see commands...");
			   is = new BufferedReader(new InputStreamReader(System.in));
			   str = is.readLine();
			   switch(str)
			   {
			   case "1":
				   localCommandsList();
			   case "2":
				   listAllFiles();
				   break;
			   case "3":
				   listAllCS();
				   break;
			   case "4":
				   int CSID1 = Integer.parseInt(is.readLine());
				   CSListFiles(CSID1);
				   break;
			   case "5":
				   int CSID2 = Integer.parseInt(is.readLine());
				   CSGetIP(CSID2);
				   break;
			   case "6":
				   int CSID3 = Integer.parseInt(is.readLine());
				   CSGetLastContact(CSID3);
				   break;
			   case "7":
				   int CSID4 = Integer.parseInt(is.readLine());
				   CSGetDeleteFiles(CSID4);
				   break;
			   case "8":
				   int CSID5 = Integer.parseInt(is.readLine());
				   CSGetMoveFile(CSID5);
				   break;
			   case "9":
				   String fileName = is.readLine();
				   FilesGetCS(fileName);
				   break;
			   default:
				   break;
}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				is.close();
			}catch(Exception e){}
		}
	}
	//get the local commands lost
	public void localCommandsList()
	{
		System.out.println("2: get list of all files \n");
		System.out.println("3: get list of all chunk server \n");
		System.out.println("4: get list of All Files in certain chunk server \n");
		System.out.println("5: get IP address for a certain chunk server \n");
		System.out.println("6: get the time when chunk server last contacted master server \n");
		System.out.println("7: get delete files for a certain chunk server \n");
		System.out.println("8: get move files for a certain chunk server \n");
		System.out.println("9: get chunk servers which have a certain file \n");
	}
	
	//get the list of all files
	public void listAllFiles()
	{
		String[] listOfAllFile = mt.filesHM_listOfFilesInFilesHM();
		for(int i=0;i<listOfAllFile.length;i++)
		{
			System.out.println("file: "+listOfAllFile[i]);
		}
	}
	
	//get the list of all chunk servers
	public void listAllCS()
	{
		int[] listOfAllCS = mt.csHM_getListOfAllCS();
		for(int i=0;i<listOfAllCS.length;i++)
		{
		  System.out.println("Chunk server: "+listOfAllCS[i]);
		}
	}
	
	//get the list of files in a certain chunk server
	public void CSListFiles(int CSID)
	{
		String[] fileListOfACS = mt.csHM_getListOfFilesForACS(CSID);
		for(int i=0;i<fileListOfACS.length;i++)
		{
			System.out.println("file: "+fileListOfACS[i]);
		}
	}
	
	//get chunk server ID
	public void CSGetIP(int CSID)
	{
		String IP = mt.csHM_getIPForACS(CSID);
		System.out.println("chunk server "+CSID+": "+IP);
	}
	
	//get chunk server last contact time
	public void CSGetLastContact(int CSID)
	{
		long lastContact = mt.csHM_getLastContactForACS(CSID);
		System.out.println("last contact for chunk server "+CSID+": "+lastContact);
	}
	
	//get delete file from a certain chunk server
	public void CSGetDeleteFiles(int CSID)
	{
		String[] deleteFiles = mt.csHM_getListOfDeleteFilesForACS(CSID);
		for(int i=0;i<deleteFiles.length;i++)
		{
			System.out.println("delete file: "+deleteFiles[i]);
		}
	}
	
	//get move file from a a certain chunk server
	public void CSGetMoveFile(int CSID)
	{
		String[] moveFiles = mt.csHM_getListOfMoveFilesForACS(CSID);
		for(int i=0;i<moveFiles.length;i++)
		{
			System.out.println("move file: "+moveFiles[i]);
		}
	}
	
	//get chunk servers that have a certain file
	public void FilesGetCS(String fileName)
	{
		LinkedList<Integer> CSID = mt.filesHM_getListOfCSContainingFile(fileName);
		System.out.println("chunk servers which contain file "+fileName+": \n");
		for(int i=0;i<CSID.size();i++)
		{
			System.out.println("chunk server "+CSID+"\n");
		}
	}

}
