import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;


public class user {
	
	// users ID number
	String id = "";
	
	// users IP address, port is set to a default value for all clients
	String ip = "";

	// list of topics by title that the user is subscribed to  
	LinkedList<String> subscriptionsToTopics = new LinkedList<String>();
	
	// list of missed event ID's 
	ArrayList<Long> missedEvents = new ArrayList<Long>();
	
	public user(String user_id){
		id = user_id;
	}
	
	public void addTopic(String topic){
		subscriptionsToTopics.add(topic);
	}
	
	public void removeTopic(String topic){
		subscriptionsToTopics.remove(topic);
	}
	
	public void clearAllTopics(){
		subscriptionsToTopics.clear();
	}
	
	public void addMissedEvent(long missedEventID){
		missedEvents.add(missedEventID);
	}
	
	public void removeMissedEvent(long sentEventID){
		missedEvents.remove(sentEventID);
	}
	
	public void setIP(String passedIP){
		ip = passedIP;
	}
	
	public String getIP(){
		return ip;
	}
	
	public String getUserID(){
		return id;
	}
	
	public LinkedList<String> getSubscriptionsToTopics(){
		return subscriptionsToTopics;
	}
	
	public ArrayList<Long> getMissedEvents(){
		return missedEvents;
	}
	
	public void clearMissedEvents(){
		missedEvents.clear();
	}
	
	public boolean isTopicInList(String passedTopic){

		if(subscriptionsToTopics.contains(passedTopic) == true){
			return true;
		}else{
			return false;
		}
	}
}
