import java.util.LinkedList;
import java.io.Serializable;

//this class contains details pertaining to a file
public class fileDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	//list of all chunk servers the file is located on 
	LinkedList<Integer> chunkServers = new LinkedList<Integer>();
	
	//chunk server to be initially contacted
	int primaryChunkServer = 0;
	
	//the number of chunk servers the file is located on (this would be how many copies of the file exist)
	int copies = 0;
	
	//constructor with only CSID passed in
	public fileDetails(int passed_chunkServer_id){
		chunkServers.add(passed_chunkServer_id);
		primaryChunkServer = passed_chunkServer_id;
		copies = 1;
	}
	
	//constructor with arguments passed in 
	public fileDetails(int passed_chunkServer_id, LinkedList<Integer> passed_chunkServers){
		chunkServers = passed_chunkServers;
		primaryChunkServer = passed_chunkServer_id;
		copies = passed_chunkServers.size();
	}
	
	//get a list of all the chunk servers holding a copy of the file
	public LinkedList<Integer> getListOfChunkServers(){
		return chunkServers;
	}
	
	//get the primary chunk server for a file
	public int getPrimaryChunkServer(){
		return primaryChunkServer;
	}
	
	//get how many copies of a file exist
	public int getCopies() {
		return copies;
	}
	
	
	//add a chunk server is in the list of chunk servers that the have a copy of the file
	public void addChunkServerToList(int passed_chunkServer) {
		chunkServers.add(passed_chunkServer);
		copies = chunkServers.size();
	}
	
	//remove a chunk server is in the list of chunk servers that the have a copy of the file
	public void removeChunkServerFromList(int passed_chunkServer) {
		chunkServers.remove((Integer)passed_chunkServer);
		if(passed_chunkServer == primaryChunkServer){
			if(chunkServers.size() != 0){				
				primaryChunkServer = chunkServers.getFirst();
			}
		}
		copies = chunkServers.size();
	}
	
	//check if the chunk server is in the list of chunk servers that the have a copy of the file
	public boolean isChunkServerInList(int passed_chunkServer){
		if(chunkServers.contains(passed_chunkServer) == true){
			return true;
		}else{
			return false;
		}
	}
	
	//set the primary chunk server
	public void setPrimaryChunkServer(int passed_chunkServer){
		primaryChunkServer = passed_chunkServer;
	}
	
}

