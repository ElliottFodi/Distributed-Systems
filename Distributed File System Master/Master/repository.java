import java.util.LinkedList;

//the repository is a general list of commands that can be used through out the system
//these methods usually combine several calls to the master table 
public class repository {

	mastertable mt;
	public repository(mastertable passed_mt) {
		mt = passed_mt;
	}
	
	//add a file to the cs and to the files list if it is not there
	public void addFile(int passed_cs_id, String passed_fileName){
		mt.addFile(passed_fileName, passed_cs_id);
	}
	
	//delete a file from the entire system
	public void deleteFile(int passed_cs_id, String passed_fileName){
		mt.deleteFile(passed_fileName);
	}
	
	//delete a file from a chunk server's list of files it has a copy of 
	public void delteAFileFromACS(int passed_cs_id, String passed_fileName){
		mt.deleteFileFromCS(passed_cs_id, passed_fileName);
	}
	
	//rename a file from the entire system
	//if the file doesn't exist in the filesHM ... just change the name in the cs files list 
	public void renameFile(int passed_cs_id, String passed_oldFileName, String passed_newFileName){
		mt.reNameFile(passed_oldFileName, passed_newFileName);
	}
	
	//rename a file in the chunk server's list of files it has a copy of 
	public void renameFileForACS(int passed_cs_id, String passed_oldFileName, String passed_newFileName){
		mt.reNameFileForACS(passed_cs_id, passed_oldFileName, passed_newFileName);
	}
	
	//add a chunk server
	public int addChunkServer(String passed_IP, LinkedList<String> passed_files, int passed_port){
		int id = mt.id_generateid();
		
		//add the chunk server to the HM of chunk servers
		mt.csHM_addCSToCSHM(id, passed_IP, passed_port);
		
		//add any files the chunk server may have
		for(int i = 0; i < passed_files.size(); i++){
			mt.addFile(passed_files.get(i), id);
		}
		
		return id;
	}
	
	//remove a chunk server
	public void removeChunkServer(int passed_cs_id){
		mt.csHM_removeCSFromCSHM(passed_cs_id);
	}
	
	//compare the current files table for a cs with the sent one
	public void compareTables(int passed_cs_id, LinkedList<String> passed_files){
		mt.tableAnalyze(passed_files, passed_cs_id);
	}
	
	//get a new cs IP for a cs to send a file 
	public String[] getNewIPForFailedCS(int passed_cs_id_that_failed, int[] passed_originalCSList){
		//0 == cs id 1 == ip
		String ip = "";
		if(mt.csHM_doesCSExist(passed_cs_id_that_failed) == true){
			
			ip = mt.csHM_getIPForACS(passed_cs_id_that_failed);
		}
		String[] new_IP = mt.reAssign(ip, passed_originalCSList);
		return new_IP;
	}
	
	//get the list of files the cs needs to delete 
	public String[] getDeleteFilesListForACS(int passed_cs_id){
		String[] filesToDelete = mt.csHM_getListOfDeleteFilesForACS(passed_cs_id);
		return filesToDelete;
	}
	
	//get the list of ip's and files the cs needs to send files to 
	public String[] getMoveFilesListForACS(int passed_cs_id){
		String[] filesToMove = mt.csHM_getListOfMoveFilesForACS(passed_cs_id);
		return filesToMove;
	}
	
	//get when a chunk server last contacted the master
	public long getLastContactListForACS(int passed_cs_id){
		long lastContact = mt.csHM_getLastContactForACS(passed_cs_id);
		return lastContact;
	}
	
	//update when a chunk server last contacted the master
	public void setLastContactListForACS(int passed_cs_id, long passed_lastContact){
		mt.csHM_setLastContactForACS(passed_cs_id, passed_lastContact);
	}
	
	//get chunk server IP's for the client to send the file to
	public String[] client_AddFile(){
		//0 == cs id 1 == ip
		String[] listOf_csID_and_ip = mt.getThreeCS();
		return listOf_csID_and_ip;
	}
	
	//returns the IP of the server that has the file
	public String[] client_getFile(String passed_fileName){
		LinkedList<Integer> csContainingFile= mt.filesHM_getListOfCSContainingFile(passed_fileName);
		int cs = csContainingFile.get(0);
		String ip = mt.csHM_getIPForACS(cs);
		String[] csInfo = {Integer.toString(cs), ip};
		return csInfo;
	}
	
	//returns a list of file names of files to be deleted
	public String[] heartBeatGetDeleteFiles(int passed_cs_id){
		String[] delete = mt.csHM_getListOfDeleteFilesForACS(passed_cs_id);
		return delete; 

	}
	
	//returns a list of file names and corresponding IP address for files that need to be moved
	public String[] heartBeatGetMoveFiles(int passed_cs_id){
		String[] move = mt.csHM_getListOfMoveFilesForACS(passed_cs_id);
		return move;
	}
	
	//NOTE: clients do not call this
	//this checks for any hidden files in the files Hash Map
	//since the system is set to delete hidden files every 10 min 
	//this will add files that need to be deleted to the corresponding chunk servers
	//list of files to be deleted
	public void client_checkForHiddenFiles(){
			//get list of all files
			//pull the ones with a . in front
		String[] files = mt.filesHM_listOfFilesInFilesHM();
		LinkedList<String> hiddenFiles = new LinkedList<String>();
		for(int i = 0; i < files.length; i++){
			String firstLetter = files[i].substring(0, 1);
			if(firstLetter.equals(".")){
				hiddenFiles.add(files[i]);
			}
		}
		
		for(int i = 0; i < hiddenFiles.size(); i++){
			//get list of CS that have the file
			LinkedList<Integer> csContainFile = mt.filesHM_getListOfCSContainingFile(hiddenFiles.get(i));
			//add file to a CS delete files list
			for(int j = 0; j < csContainFile.size(); j++){
				//add the file to the CS's delete files list 
				//need to make add file to delete file list 
				mt.csHM_addFileToCSDeleteFileList(hiddenFiles.get(i), csContainFile.get(j));
			}
			
		}	
		
	}
	
	//this is called when the client wants to delete a file
	//returns a list of all chunk servers containing the file that the client wants to delete
	public String[] client_deleteFile(String passed_fileName){
		LinkedList<Integer> cs = mt.filesHM_getListOfCSContainingFile(passed_fileName);
		String[] cs_And_IP = new String[2* cs.size()]; 
		int index = 0;
		for(int i = 0; i < cs.size(); i++){
			cs_And_IP[index] = cs.get(i).toString();
			cs_And_IP[index + 1] = mt.csHM_getIPForACS(cs.get(i));
			index += 2;
		}
		
		return cs_And_IP;
	}
	
	//get a list of all chunk servers in the chunk server Hash Map
	public int[] getListOfCS(){
		return mt.csHM_getListOfAllCS();
	}
	
	//get the master table
	public mastertable getMasterTable(){
		return mt;
	}
}
