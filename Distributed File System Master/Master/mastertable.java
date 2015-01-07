import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

//this is the master table that maps files to chunk servers
public class mastertable {

	//Hash Map mapping chunk servers to a chunk server object 
	HashMap<Integer, csDetails> chunkServers = new HashMap<Integer, csDetails>();
	
	//Hash Map that maps file names to a file object
	HashMap<String, fileDetails> Files = new HashMap<String, fileDetails>();
	
	//how many times a file should be replicated 
	int replication_factor = 0;
	
	//chunk server id counter for assigning new ID's
	Integer cs_id = 0;
	
	//constructor
	public mastertable(int passed_replication_factor){
		replication_factor = passed_replication_factor;
	}
	
	
	//add files to Files HM
	public void filesHM_addFileToFilesHM(String passed_fileName, int passed_primaryCS, LinkedList<Integer> passed_cs){
		synchronized (Files) {
			Files.put(passed_fileName, new fileDetails(passed_primaryCS, passed_cs));
		}
	}
	
	//remove files from Files HM
	public void filesHM_removeFileFromFilesHM(String passed_fileName){
		synchronized (Files) {
			Files.remove(passed_fileName);
		}
	}
	
	//get list of chunk servers that have a copy of the file
	public LinkedList<Integer> filesHM_getListOfCSContainingFile(String passed_fileName){
		LinkedList<Integer> list = new LinkedList<Integer>();
		if(filesHM_doesAFileExist(passed_fileName) == true){
			synchronized (Files.get(passed_fileName)) {
				
				list = Files.get(passed_fileName).getListOfChunkServers(); 
			}
		}
		return list;
	}
	
	//get how many replicas of a file there are 
	public int filesHM_getNumberOfCopiesForAFile(String passed_fileName){
		int cp = 0;
		synchronized (Files.get(passed_fileName)) {
			cp = Files.get(passed_fileName).getCopies(); 
		}
		return cp;
	}
	
	//add a chunk server to the list of chunk servers that contain a file  
	public void filesHM_addACSToFileDetails(String passed_fileName, int passed_cs){
		synchronized (Files.get(passed_fileName)) {
			
			Files.get(passed_fileName).addChunkServerToList(passed_cs);
		}
	}
	
	//remove a chunk server from the list of chunk servers that contain a file
	public void filesHM_removeACSFromFileDetials(String passed_fileName, int passed_cs){
		
		if( filesHM_doesAFileExist(passed_fileName) == true){
			synchronized (Files.get(passed_fileName)) {
				Files.get(passed_fileName).removeChunkServerFromList(passed_cs);
			}
			
		}
	}
	
	//does list of chunk servers contain the passed chunk server
	public boolean filesHM_isACSInTheListOfCS(String passed_fileName, int passed_cs){
		boolean exists = true;
		synchronized (Files.get(passed_fileName)) {
			exists = Files.get(passed_fileName).isChunkServerInList(passed_cs);
		}
		return exists;
	}
	
	//does files exist in the files Hash Map
	public boolean filesHM_doesAFileExist(String passed_fileName){
		
		boolean exists = true;
		synchronized (Files) {
			exists = Files.containsKey(passed_fileName);
			
		}
		
		if(exists == true){
			return true;
		}else{
			return false;
		}
	}
	
//	//get list of files to chunk servers for logging
//	public void filesHM_getLog(){
//		
//	}
	
	//get list of all files in the files Hash Map
	public String[] filesHM_listOfFilesInFilesHM(){
		Set<String> listOfFiles;
		synchronized (Files) {
			listOfFiles = Files.keySet();
			
		}
		String[] filesList = new String[listOfFiles.size()];
		listOfFiles.toArray(filesList);
		
		return filesList;
	}
	
	
	/*
	 * chunk server methods 
	 */
	
	
	//add new chunk server to chunkServers HM
	public void csHM_addCSToCSHM(int passed_cs_id, String passed_ip, int passed_port ){
		//chunkServers.put(passed_cs_id, new HashMap<String, Character>());
		
		synchronized (chunkServers) {
			
			chunkServers.put(passed_cs_id, new csDetails(passed_ip, passed_port));
		}
	}
	
	//remove chunk server from chunkServers HM
	//this will delete the chunk server from each file's list of chunk server that 
	//have a copy of the file and make sure the replication factor is met by calling balance  
	public void csHM_removeCSFromCSHM(int passed_cs_id){
		// here to remember to finish 
		
		
		//get files for the passed chunk server
		String[] files = csHM_getListOfFilesForACS(passed_cs_id);
		
		//check the downed cs's move files list and get any files from there and add them to the files list 
		
		//remove chunk server id from each FilesHM file's details 
		for(int i = 0; i < files.length; i++){
			filesHM_removeACSFromFileDetials(files[i], passed_cs_id);
		}
		
		
		//remove the chunk server
		chunkServers.remove(passed_cs_id);
		
		//balance the cs since one went down
		balance(files);
		
		
		//if no chunk servers have the file ... remove the file from the files Hash Map

		for(int i = 0; i < files.length; i++){
			LinkedList<Integer> listOFCS = filesHM_getListOfCSContainingFile(files[i]);
			if(listOFCS.size() == 0){
				filesHM_removeFileFromFilesHM(files[i]);			
			}
		}
	}
	
	//balance used if a cs goes down ...it will make sure the replication factor stays correct
	//this is only used in csHM_removeCS 
	public void balance(String[] passedFileList){
		
		//get the id for every cs
		int[] cs_ids = csHM_getListOfAllCS();
		int[] cs_amountOfFiles = new int[cs_ids.length];
		
		//get the amount of files for each cs
		for( int i = 0; i < cs_ids.length; i++){
			
			cs_amountOfFiles[i] = csHM_getCSNumberOfFiles(cs_ids[i]);	
		}
		
		//combine id and amounts into a 2d array for processing
		int[][] idsAndAmounts = new int[cs_ids.length][2];
		for(int i = 0; i < cs_ids.length; i++){
			idsAndAmounts[i][0] = cs_ids[i];
			idsAndAmounts[i][1] = cs_amountOfFiles[i];
		}
		
		//sort the ids based on the amount of files they have 
		//in the 2d array the second array is of size 2 
		//0 index is the id, 1 index is the file amount
		int[][] lowestAmountOfFiles = twoarrayBubbleSort(idsAndAmounts);
		
		// for each file in the passed list
		for(String file : passedFileList){
			
			//get a list of cs that have the file
			LinkedList<Integer> CS_thatHaveTheFile = filesHM_getListOfCSContainingFile(file);
			
			//get the first letter of the file name
			String firstLetter = file.substring(0, 1);
			
			//if the first letter is not a "." then it is not a hidden file
			//if it is a hidden file... do nothing to it, don't replicate hidden files
			if(firstLetter.equals(".") == false){
				
				//append a "." to the file name to check if a hidden version of the file exists
				String hiddenVersion = "." + file;
				
				//if there is not a hidden version of the file replicate it 
				//... don't replicate a file if a hidden version exists
				//if a hidden version exists, this cs just didn't receive the command to hide the file yet
				if(filesHM_doesAFileExist(hiddenVersion) == false){
					
					//if the list is == to or larger then the replication factor ... do nothing
					if(filesHM_getNumberOfCopiesForAFile(file) < replication_factor){
						
						//find the cs with the least files that isn't a cs with the file already
						for(int i = 0; i < lowestAmountOfFiles.length; i++){
							
							//if the list of cs does not contain the id of the cs we are currently looking at then proceed other wise skip it 
							if((CS_thatHaveTheFile.contains(lowestAmountOfFiles[i][0]) == false) && (CS_thatHaveTheFile.size() != 0)){
								
								//get the ip of the cs we want to send the file to 
								String ip = chunkServers.get(lowestAmountOfFiles[i][0]).ip_getIP();
								
								//it doesnt have the file so we can copy the file to this cs
								chunkServers.get(CS_thatHaveTheFile.getFirst()).mfHM_addFile(file, ip);
								
								//we are adding a file to this server so ... incrament our local count
								lowestAmountOfFiles[i][1]++;
								
								//break cause we found a cs to move the file to 
								break;
								
							}else{
								//do nothing
							}
						}
						
						//resort since we incramented that value
						lowestAmountOfFiles = twoarrayBubbleSort(lowestAmountOfFiles);
					}//end third if 
				}//end second if
			}//end first if
			

			
		}
		
	}
	
	
	//does a chunk server exist in the chunk server Hash Map
	public boolean csHM_doesCSExist(int passed_cs_id){
		
		boolean exists = true;
		synchronized (chunkServers) {
			exists = chunkServers.containsKey(passed_cs_id);
		}
		
		if( exists == true){
			return true;
		}else{
			return false;
		}
	}
	
	//get a list of files the chunk server has
	public String[] csHM_getListOfFilesForACS(int passed_cs_id){
		return chunkServers.get(passed_cs_id).fsHM_listFiles();
	}
	
	//get a list of files and file status's for each file
	public String[] csHM_getListOfFilesandStatusForACS(int passed_cs_id){
		return chunkServers.get(passed_cs_id).fsHM_listFilesAndStatus();
	}
	
	//get a list of all chunk servers in the chunk server Hash Map
	public int[] csHM_getListOfAllCS(){
		Set<Integer> listOFCS;
		synchronized (chunkServers) {
			
			listOFCS = chunkServers.keySet();
		}
		Integer[] cs = new Integer[listOFCS.size()];
		listOFCS.toArray(cs);
		
		int[] listOfChunkServers = new int[cs.length];
		for(int i = 0; i <cs.length; i++){
			listOfChunkServers[i] = cs[i];
		}
		return listOfChunkServers;
	}
	
	//get the amount of files the chunk server is storing 
	public int csHM_getCSNumberOfFiles(int passed_cs_id){
		return chunkServers.get(passed_cs_id).fsHM_amountOfFilesStored();
	}
	
	//get a list of chunk servers and how many files they are storing
	public void csHM_getCSNumberOfFilesAndStatus(){
		int[] chunkServers = csHM_getListOfAllCS();
		String[] csAndFiles = new String[chunkServers.length];
		for(int i = 0; i < chunkServers.length; i++){
			csAndFiles[i] = chunkServers[i] + ": " + csHM_getCSNumberOfFiles(chunkServers[i]); 
		}
	}
	
	//add file to a chunk server's HM of files it contains 
	public void csHM_addFileToCSHM(int passed_cs_id, String passed_fileName, char passed_status){
		chunkServers.get(passed_cs_id).fsHM_addFile(passed_fileName, passed_status);
	}
	
	//remove a file from a chunk server's HM of files it contains
	public void csHM_removeFileFromCSHM(int passed_cs_id, String passed_fileName){
		chunkServers.get(passed_cs_id).fsHM_removeFile(passed_fileName);
	}
	
	//does a file exists in a chunk sever's file HM of files it contains
	public boolean csHM_doesCSContainFile(int passed_cs_id, String passed_fileName){
		return chunkServers.get(passed_cs_id).fsHM_doesFileExist(passed_fileName);

	}
	
	//get a file from the chunk server's file HM
	//public void csHM_getFile(int passed_cs_id, String passed_fileName){
	//	chunkServers.get(passed_cs_id).get(passed_fileName);
	//}
	
	//set a chunk server's file status to "t" "s" "p" "n"
	public void csHM_setCSFileStatus(int passed_cs_id, String passed_fileName, char passed_status){
		chunkServers.get(passed_cs_id).fsHM_addFile(passed_fileName, passed_status);
	}
	
	//get a chunk server's file status
	public char csHM_getCSFileStatus(int passed_cs_id, String passed_fileName){
		return chunkServers.get(passed_cs_id).fsHM_getFileStatus(passed_fileName);
	}
	
	//get a list of files that are in a chunk server delete file List
	public String[] csHM_getListOfDeleteFilesForACS(int passed_cs_id){
		String[] deleteFiles;
		synchronized (chunkServers.get(passed_cs_id)) {
			deleteFiles = chunkServers.get(passed_cs_id).dfl_getFiles();
			chunkServers.get(passed_cs_id).dfL_clearList();
		}
		return deleteFiles;
	}
	
	//get a list of files and ip address from a chunk server's Hash Map of files to be moved
	//[0] == file name [1] == IP address
	public String[] csHM_getListOfMoveFilesForACS(int passed_cs_id){
		String[] moveFiles;
		synchronized (chunkServers.get(passed_cs_id)) {
			moveFiles = chunkServers.get(passed_cs_id).mfHM_listFiles();
			
		}
		String[] files_And_ip = new String[moveFiles.length * 2];
		int index = 0;
		for(int i = 1; i < files_And_ip.length; i+=2){
			files_And_ip[i - 1] =  moveFiles[index]; 
			files_And_ip[i] = chunkServers.get(passed_cs_id).mfHM_getIp(moveFiles[index]);
			index++; 
		}
		
		synchronized (chunkServers.get(passed_cs_id)) {
			chunkServers.get(passed_cs_id).mfHM_clearHM();
		}
		return files_And_ip;
	}
	
	//get the last contact time for a chunk server
	public long csHM_getLastContactForACS(int passed_cs_id){
		long lc;
		synchronized (chunkServers.get(passed_cs_id)) {
			lc = chunkServers.get(passed_cs_id).lc_getLastContact();
		}
		
		return lc;
	}
	
	//set the last contact time for a chunk server
	public void csHM_setLastContactForACS(int passed_cs_id, long passed_lastContact){
		synchronized (chunkServers.get(passed_cs_id)) {
			chunkServers.get(passed_cs_id).lc_setLastContact(passed_lastContact);
		}
	}
	
	//get the IP address for a chunk server
	public String csHM_getIPForACS(int passed_cs_id){
		String ip = "";
		synchronized (chunkServers.get(passed_cs_id)) {
			ip = chunkServers.get(passed_cs_id).ip_getIP();
			
		}
		return ip;
	}
	
	//add a file to a chunk servers list of files to be deleted
	public void csHM_addFileToCSDeleteFileList(String passed_fileName, int passed_cs_id){
		chunkServers.get(passed_cs_id).dfL_addFile(passed_fileName);
		System.out.println("file added to delete file list: " + passed_fileName);
	}
	
//	//export a log file
//	public void csHM_getLog(){
//		
//	}
	
	/*
	 * methods that utilize the chunk server and file methods 
	 */
	
	//generate an id when a new CS connects to the master
	public int id_generateid(){
		int id = 0;
		synchronized (cs_id) {
			id = cs_id;
			cs_id++;
		}
		return id;
	}
	
	//used in the Balance method only 
	//check all chunk servers and issue a file to the chunk server with the least files on it 
	public int[][] twoarrayBubbleSort(int[][] passed_idsAndValues){

		int[][] idsAndValues = passed_idsAndValues;
		boolean swapOccured = true;
		while(swapOccured){
			swapOccured = false;
			for(int i = 0; i < idsAndValues.length - 1; i++){
				if(idsAndValues[i][1] > idsAndValues[i+1][1]){
					int temp = idsAndValues[i][1];
					int id_temp = idsAndValues[i][0];
					idsAndValues[i][1] = idsAndValues[i+1][1];
					idsAndValues[i][0] = idsAndValues[i+1][0];
					idsAndValues[i+1][1] = temp;
					idsAndValues[i+1][0] = id_temp;
					swapOccured=true;
				}
			}
		}
		
		return idsAndValues;
	}
	
	//if a chunk server reports it can not send a file to an address
	//this will get a new address to send the file to provided there is an available chunk server
	//[0] == CSID [1] == IP address
	public String[] reAssign(String passed_IP_that_failed, int[] passed_originalCSList){
		
		String[] idAndIP = new String[2];
		
		//get cs and there file amounts
		
		//get the id for every cs
		int[] cs_ids = csHM_getListOfAllCS();
		int[] cs_amountOfFiles = new int[cs_ids.length];
		
		//get the amount of files for each cs
		for( int i = 0; i < cs_ids.length; i++){
			
			cs_amountOfFiles[i] = csHM_getCSNumberOfFiles(cs_ids[i]);	
		}
		
		//combine id and amounts into a 2d array for processing
		int[][] idsAndAmounts = new int[cs_ids.length][2];
		for(int i = 0; i < cs_ids.length; i++){
			idsAndAmounts[i][0] = cs_ids[i];
			idsAndAmounts[i][1] = cs_amountOfFiles[i];
		}
		
		//sort the ids based on the amount of files they have 
		//in the 2d array the second array is of size 2 
		//0 index is the id, 1 index is the file amount
		int[][] lowestAmountOfFiles = twoarrayBubbleSort(idsAndAmounts);
		
		LinkedList<Integer> originalCS = new LinkedList<Integer>();
		for(int i = 0; i< passed_originalCSList.length; i++){
			originalCS.add(passed_originalCSList[i]);
		}

		//select a new cs with a low file amount that was not given in the original list 

		//find the cs with the least files that isn't a cs with the file already
		for(int i = 0; i < lowestAmountOfFiles.length; i++){
			
			//if the list of cs does not contain the id of the cs we are currently looking at then proceed other wise skip it 
			if(originalCS.contains(lowestAmountOfFiles[i][0]) == false){
				
				//get the ip of the cs we want to send the file to
				//0 == cs id 1 == ip
				idAndIP[0] = lowestAmountOfFiles[i][0] + "";
				idAndIP[1] = chunkServers.get(lowestAmountOfFiles[i][0]).ip_getIP();
				
				//break cause we found a cs to move the file to 
				break;
			}
		}
		return idAndIP; 
	}
	
	//when a client asks to add a new file to the system, this will attempt to return 3
	//chunk server to send the file to. IF there are less then 3 chunk servers it will return 
	// as many as possible.
	//[0] == CSID [1] == IP address
	public String[] getThreeCS(){		
		String[] idAndIP; 
				
		//get the id for every cs
		int[] cs_ids = csHM_getListOfAllCS();
		int[] cs_amountOfFiles = new int[cs_ids.length];
		
		 
		if(cs_ids.length >= replication_factor){
			idAndIP = new String[replication_factor * 2];
		}else{
			idAndIP = new String[cs_ids.length * 2];
		}
		
		//get the amount of files for each cs
		for( int i = 0; i < cs_ids.length; i++){
			
			cs_amountOfFiles[i] = csHM_getCSNumberOfFiles(cs_ids[i]);	
		}
		
		//combine id and amounts into a 2d array for processing
		int[][] idsAndAmounts = new int[cs_ids.length][2];
		for(int i = 0; i < cs_ids.length; i++){
			idsAndAmounts[i][0] = cs_ids[i];
			idsAndAmounts[i][1] = cs_amountOfFiles[i];
		}
		
		//sort the ids based on the amount of files they have 
		//in the 2d array the second array is of size 2 
		//0 index is the id, 1 index is the file amount
		int[][] lowestAmountOfFiles = twoarrayBubbleSort(idsAndAmounts);
		
		int idandip_index = 0;

		//find the cs with the least files that isn't a cs with the file already
		for(int i = 0; i < lowestAmountOfFiles.length; i++){
				
			if(i == idAndIP.length/2){
				break;
			}
			//if the list of cs does not contain the id of the cs we are currently looking at then proceed other wise skip it 
				
				//get the ip of the cs we want to send the file to 
				idAndIP[idandip_index] = lowestAmountOfFiles[i][0] + "";
				idAndIP[idandip_index+1] = chunkServers.get(lowestAmountOfFiles[i][0]).ip_getIP();
				System.out.println("IP of the cs in :" + chunkServers.get(lowestAmountOfFiles[i][0]).ip_getIP());
				
				idandip_index++;
				idandip_index++;

				//break cause we found a cs to move the file to 

			
		}
		return idAndIP; 
	}
	
	//add a file to the system
	//this will check if the file exists, if not it will add the file to the files Hash Map
	//if the file exists it will only add the chunk server to the files's List of CS that have a 
	//copy of the file
	public void addFile(String passed_fileName, int passed_cs_id){
		//if the file exists...just add the file to the cs list 
		if(filesHM_doesAFileExist(passed_fileName) == false){
			csHM_addFileToCSHM(passed_cs_id, passed_fileName, 'C');
			LinkedList<Integer> cs = new LinkedList<Integer>();
			cs.add(passed_cs_id);
			filesHM_addFileToFilesHM(passed_fileName, passed_cs_id, cs);
		}else{
			csHM_addFileToCSHM(passed_cs_id, passed_fileName, 'C');
			filesHM_addACSToFileDetails(passed_fileName, passed_cs_id);
		}

	}
	
	//This will compare 2 lists of files, when a chunk server sends over its list of files it 
	//contains this will compare it to the current list of files that the master has listed for 
	//a chunk server. If the master is missing a file, it is added to the master
	//If a master has a file that the passed list does not, then the master will delete the file.
	public void tableAnalyze(LinkedList<String> passed_csFileList, int passed_cs_id){
		//compare the lists
		//get list of files for the cs id passed
		String[] csFiles = csHM_getListOfFilesForACS(passed_cs_id);
		
		// OLD if the passed cs's list has a file the master does not have, add it 

		//the cs has a file that the master does not ... delete it on the cs to maintain a constant state
		for(int i = 0; i < passed_csFileList.size(); i++){
			if(csHM_doesCSContainFile(passed_cs_id, passed_csFileList.get(i)) == false){
				// DELETE: issue the rename command so the file will be added hidden
				// DELETE: this way the delete command will be issued to that cs to delete that file 
				// DELETE: reNameFileForACS(passed_cs_id, csFiles[i], "." + csFiles[i]);
				// DELETE: chunkServers.get(passed_cs_id).fsHM_addFile(csFiles[i], 'C');

				csHM_addFileToCSDeleteFileList(csFiles[i], passed_cs_id);
			}
		}
		
		//if the master has a file that the cs does not have remove it 
		//identify what files the master has that the cs does not 
		LinkedList<String> csFilesList = new LinkedList<String>();
		//for every file in a list of files the master has 
		for(int i = 0; i < csFiles.length; i++){
			//compare to the passed list ... if the passed list does not contain the file 
			//remove the file from the master
			if(passed_csFileList.contains(csFiles[i]) == false){
				//csFilesList.remove(i);
				//delete the file
 
				//chunkServers.get(passed_cs_id).fsHM_removeFile(csFilesList.get(i));
				
			}
		}
	}
	
	//this deletes the file from the entire system all at once
	public LinkedList<Integer> deleteFile(String passed_fileName){
		//get the cs that have the file
		
		LinkedList<Integer> csContainingFile = filesHM_getListOfCSContainingFile(passed_fileName);
		
		
		if(filesHM_doesAFileExist(passed_fileName) == true){
			//remove the file from the files hm 
			filesHM_removeFileFromFilesHM(passed_fileName);			
		}
		
		// remove the file from each cs list
		for(int i = 0; i < csContainingFile.size(); i++){
			csHM_removeFileFromCSHM(csContainingFile.get(i), passed_fileName);
		}
		return csContainingFile;
	}
	
	//this deletes a file from the file list if it is there and from a single cs 
	public void deleteFileFromCS(int passed_cs_id, String passed_fileName){
		if(filesHM_doesAFileExist(passed_fileName) == true){
			
			  //if the file has a list of (cs containg the file) of length 1
			  //... this is the last cs containing the file ... remove the file from filesHash Map 
			  //other wise if there are more then one cs, delete the cs from the files list of cs containg the file 
			 
			LinkedList<Integer> csContainingFile = filesHM_getListOfCSContainingFile(passed_fileName);
			if(csContainingFile.size() == 1){
				//remove the file from the files HM
				//this deletes everything associated with that file
				filesHM_removeFileFromFilesHM(passed_fileName);
				
//				if(filesHM_isACSInTheListOfCS(passed_fileName, passed_cs_id) == true){
//					//remove the file from the CS list of files it contains
//					filesHM_removeACSFromFileDetials(passed_fileName, passed_cs_id);
//				}
			}else{
				//this is NOT the last chunk server containing the file, so just remove the file
				//from the CS list of files it contains, first check to see if the passed cs is in the list of 
				//cs it contains ... to avoid null pointer exceptions
				if(filesHM_isACSInTheListOfCS(passed_fileName, passed_cs_id) == true){					
					filesHM_removeACSFromFileDetials(passed_fileName, passed_cs_id);
				}
			}
		}
		//check if the file exists then remove it  
		if(csHM_doesCSContainFile(passed_cs_id, passed_fileName) == true){			
			csHM_removeFileFromCSHM(passed_cs_id, passed_fileName);
		}
	}
	
	//this will rename a file by deleting it and then adding it as a new file 
	public void reNameFile(String passed_fileName, String passed_newName){
		//delete the old file
		 LinkedList<Integer> passed_csConatiningFile = deleteFile(passed_fileName);
		
		//add the new file to the files HM and to each cs that had the file originally
		for(int i = 0; i < passed_csConatiningFile.size(); i++){
			
			addFile(passed_newName, passed_csConatiningFile.get(i));
		}
		
	}
	
	//this will rename a file for a single chunk server 
	public void reNameFileForACS(int passed_cs_id, String passed_oldFileName, String passed_newFileName){
		deleteFileFromCS(passed_cs_id, passed_oldFileName);
		addFile(passed_newFileName, passed_cs_id);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector<HashMap> ghost_getHashMaps()
	{
		Vector hashMaps = new Vector();
		hashMaps.add(this.chunkServers);
		hashMaps.add(this.Files);
		return hashMaps;
	}
	
}
