
import java.util.HashMap;
import java.util.LinkedList;


public class topic {
	
	// title of the topic, also ID's the topic
	String title = "";
	
	// keywords associated with the topic
	LinkedList<String> keywords;
	
	// hash map instead of array list to deal with removing subscribers
	//key == userID and value is just a boolean
	HashMap<String,Boolean> usersSubscribedToTopic = new HashMap<String,Boolean>();
	
	public topic(String topic_title, LinkedList<String> topic_keywords){
		title = topic_title;
		keywords = topic_keywords;
	}
	
	public void addUser(String subscriberID){

		usersSubscribedToTopic.put(subscriberID, true);
	}
	
	public void removeUser(String subscriberID){

		usersSubscribedToTopic.remove(subscriberID);
	}
	
	public HashMap getSubscribers(){
		return usersSubscribedToTopic;
	}
}
