import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.io.Serializable;


public class csDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	HashMap<String, Character> filesStored = new HashMap<String, Character>();
	String IP = "";
	Integer port = 0;
	
	Long lastContact = 0L;
	
	LinkedList<String> deleteFiles = new LinkedList<String>();
	HashMap<String, String> moveFiles = new HashMap<String, String>();
	
	
	/*
	 *constructor 
	 */
		
	public csDetails(String passed_ip, int passed_port) {
		IP = passed_ip;
		port = passed_port;
	}
	
	
	/*
	 * files stored Hash Map methods
	 */
	
	//fsHM == files Stored Hash Map
	//add file to the files stored HM
	public void fsHM_addFile(String passed_fileName, char passed_status){
		synchronized (filesStored) {
			
			filesStored.put(passed_fileName, passed_status);
		}
	}
	
	//change file status in files Stored HM
	public void fsHM_changeFileStatus(String passed_fileName, char passed_status){
		synchronized (filesStored) {
			
			filesStored.put(passed_fileName, passed_status);
		}
	}
	
	//remove file from files stored HM
	public void fsHM_removeFile(String passed_fileName){
		synchronized (filesStored) {
			
			filesStored.remove(passed_fileName);
		}
	}
	
	//get file status from files Stored HM
	public char fsHM_getFileStatus(String passed_fileName){
		synchronized (filesStored) {
			
			return filesStored.get(passed_fileName);
		}
	}
	
	//does a file exist in the files stored Hash Map
	public boolean fsHM_doesFileExist(String passed_fileName){
		boolean exists = true;
		synchronized (filesStored) {
			
			exists = filesStored.containsKey(passed_fileName);
		}
		if(exists == true){
			return true;
		}else{
			return false;
		}
	}
	
	//get list of files from files Stored HM
	//files are not listed in any order 
	public String[] fsHM_listFiles(){
		Set<String> listFiles;
		synchronized (filesStored) {
			
			listFiles = filesStored.keySet();
		}
		String[] listfiles = new String[listFiles.size()];
		listFiles.toArray(listfiles);
		return listfiles;
	}
	
	//get list of files and status from files Stored HM
	public String[] fsHM_listFilesAndStatus(){
		Set<String> listFiles; 
		synchronized (filesStored) {
			
		listFiles = filesStored.keySet();
		}
		
		String[] listfiles = new String[listFiles.size()];
		listFiles.toArray(listfiles);
		synchronized (filesStored) {
			for(int i = 0; i < listfiles.length; i++){
				listfiles[i] = listfiles[i] + ": " + filesStored.get(listfiles[i]);
			}
		}
		return listfiles;
	}
	
	//get the amount of files stored in files Stored HM
	public int fsHM_amountOfFilesStored(){
		int size = 0; 
		synchronized (filesStored) {
			size = filesStored.size();
		}
		return size;
	}
	
	
	/*
	 * IP methods
	 */
	
	//ip
	//set IP 
	public void ip_setIP(String passed_IP){
		synchronized (IP) {
			
			IP = passed_IP;
		}
	}
	
	//get IP
	public String ip_getIP(){
		String ip = "";
		synchronized (IP) {
			ip = IP;
		}
		return ip;
	}
	
	
	/*
	 * port methods
	 */
	
	//port
	//set port
	public void port_SetPort(int passed_port){
		synchronized (port) {
			
			port = passed_port;
		}
	}
	
	//get port
	public Integer port_getPort(){
		Integer pt = 0;
		synchronized (port) {
			pt = port;
		}
		
		return pt;
	}
	
	
	
	/*
	 * last contact methods
	 */
	
	//lc
	//set last contact 
	public void lc_setLastContact(long passed_lastContact){
		synchronized (lastContact) {
			
			lastContact = passed_lastContact;
		}
	}
	
	//get last contact
	public long lc_getLastContact(){
		long lc = 0;
		synchronized (lastContact) {
			lc = lastContact;
		}
		return lc;
	}
	
	
	/*
	 * delete file list methods
	 */
	
	//dfL
	//add file to deleteFiles list
	public void dfL_addFile(String passed_fileName){
		synchronized (deleteFiles) {
			if( deleteFiles.contains(passed_fileName) == false){
				
				deleteFiles.add(passed_fileName);
			}
		}
	}
	
	//remove file from delete files list
	public void dfL_removeFile(String passed_fileName){
		synchronized (deleteFiles) {
			
			deleteFiles.remove(passed_fileName);
		}
	}
	
	//clear delete files list
	public void dfL_clearList(){
		synchronized (deleteFiles) {
			
			deleteFiles.clear();
		}
	}
	
	//get list of files to delete
	public String[] dfl_getFiles(){
		String[] files;
		synchronized (deleteFiles) {
			files = new String[deleteFiles.size()];
			deleteFiles.toArray(files);
		}
		
		return files;
	}
	
	/*
	 * move file hash map methods
	 */
	
	//mfHM == move file Hash Map
	//add file to move files HM
	public void mfHM_addFile(String passed_fileName, String passed_ip){
		synchronized (moveFiles) {
			
			moveFiles.put(passed_fileName, passed_ip);
		}
	}
	
	//remove file from move files HM
	public void mfHM_removeFile(String passed_fileName){
		synchronized (moveFiles) {
			
			moveFiles.remove(passed_fileName);
		}
	}
	
	//change IP in move files HM
	public void mfHM_changeIP(String passed_fileName, String passed_ip){
		synchronized (moveFiles) {
			
			moveFiles.put(passed_fileName, passed_ip);
		}
	}
	
	//get list of files from move files HM
	public String[] mfHM_listFiles(){
		Set<String> listFiles;
		synchronized (moveFiles) {
			
		listFiles = moveFiles.keySet();
		}
		String[] listfiles = new String[listFiles.size()];
		listFiles.toArray(listfiles);
		return listfiles;
	}
	
	//get list of files and ip's from move files HM
	public String[] mfHM_listFilesAndIp(){
		
		Set<String> listFiles; 
		synchronized (moveFiles) {
		listFiles = moveFiles.keySet();
			
		}
		String[] listfiles = new String[listFiles.size()];
		listFiles.toArray(listfiles);
		
		synchronized (moveFiles) {
			for(int i = 0; i < listfiles.length; i++){
				listfiles[i] = listfiles[i] + ": " + moveFiles.get(listfiles[i]);
			}
			
		}
		return listfiles;
	}
	
	//get the amount of files to be moved from move files HM
	public int mfHM_amountOfFilesToBeMoved(){
		int size = 0;
		synchronized (moveFiles) {
			size = moveFiles.size();
		}
		return size;
	}
	
	//get IP that corresponds to a file from move files HM
	public String mfHM_getIp(String passed_fileName){
		String ip = "";
		synchronized (moveFiles) {
			ip = moveFiles.get(passed_fileName);
		}
		return ip;
	}
	
	//clear the move file Hash map
	public void mfHM_clearHM(){
		moveFiles.clear();
	}
	
	
}
