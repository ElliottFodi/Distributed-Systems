import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/*
 * This class interacts with all the lists and other classes,
 * it acts like the brain
 */

public class repository_of_lists {


	// list of all topics
	// Hash map of topics, with the key == the topic title and the value == the topic object
	HashMap<String, topic> topicHM = new HashMap<String, topic>();
	
	
	// list of all users
	// Hash map of users, with the key == the user id and the value == the user object 
	HashMap<String, user> userHM = new HashMap<String, user>();
	
	
	// list of all events
	// Hash map of events, with the key == the event id and the value == the event object 
	HashMap<Long, event> eventHM = new HashMap<Long, event>();
	
	
	//long userID = 0;
	
	public repository_of_lists(){
		
	}
	
	
	/*
	 * 
	 * User methods
	 * 
	 */
	
	//add user to hash map
	public void addUser(String uID){
		
		synchronized (userHM) {
			userHM.put(uID, new user(uID));
		}
		
	}
	
	//remove user from hash map and 
	//remove user list of users subscribed to a topic (this is in the topic)
	public void removeUser(String uID){
		LinkedList usersSubscribtions = userHM.get(uID).getSubscriptionsToTopics();
		
		for(int i = 0; i < usersSubscribtions.size(); i++){
			String topic = usersSubscribtions.get(i).toString();
			removeUserFromTopic(uID, topic);
		}
		
		synchronized (userHM) {
			userHM.remove(uID);
		}
	}
	
	//add a topics to the users profile
	public void addTopicToUserProfile(String uID, String topicTitle){
		
		synchronized (userHM.get(uID)) {
			userHM.get(uID).addTopic(topicTitle);
		}
		
	}
	
	//remove a topic fromt he users profile
	public void removeTopicFromUserProfile(String uID, String topicTitle){
		
		synchronized (userHM.get(uID)) {
			userHM.get(uID).removeTopic(topicTitle);
		}
	}
	
	//remove all topics from the users profile
	public void removeAllTopicsFromUserProfile(String uID){
		//get list of topics the user is subscribed to 
		LinkedList<String> userSubscriptions = new LinkedList<String>();
		int listSize = 0;
		synchronized (userHM.get(uID)) {
			userSubscriptions = userHM.get(uID).getSubscriptionsToTopics();
		}
		
		//remove the user
		listSize = userSubscriptions.size();
		String topic = "";
		for (int i = 0; i < listSize; i++){
			topic = userSubscriptions.removeFirst();
			removeUserFromTopic(uID, topic);
		}
		
		synchronized (userHM.get(uID)) {
			userHM.get(uID).clearAllTopics();
		}

	}
	
	//list all the topics a user is subscribe to 
	public LinkedList<String> listAllUserTopics(String uID){
		LinkedList<String> currentlySubscribedTopics = new LinkedList<String>();
		currentlySubscribedTopics = userHM.get(uID).getSubscriptionsToTopics();
		return currentlySubscribedTopics;
	}
	
	// set the users ip to a new ip address
	public void updateUserIP(String uID, String IP){
		synchronized (userHM.get(uID)) {
			userHM.get(uID).setIP(IP);
		}
	}
	
	//obtain the users IP address
	public String getUserIP(String uID){
		String ip = "";
		synchronized (userHM.get(uID)) {
			ip = userHM.get(uID).getIP();
		}
		return ip;
	}
	
	//check to see if the user is in the system
	public boolean verifyUser(String uID){

		
		if(userHM.get(uID) == null){
			return false;
		}else{
			return true;
		}
	}
	
	//list all users in the system
	public Set<String> listUsers(){
		return userHM.keySet();
	}
	
	//get a list of events the user missed
	public ArrayList getUserMissedEvents(String uID){
		ArrayList missedEvents;
		synchronized (userHM.get(uID)) {
			missedEvents = userHM.get(uID).getMissedEvents();
		}
		
		return missedEvents;
	}
	
	//clear the list of events the user missed
	public void clearUserMissedEvents(String uID){
		
		synchronized (userHM.get(uID)) {
			userHM.get(uID).clearMissedEvents();
		}
		
	}
	
	//add a missed event to the usres list of missed events
	public void userAddMissedEvent(String uID, long eID){
		
		synchronized (userHM.get(uID)) {
			userHM.get(uID).addMissedEvent(eID);
		}
		
	}
	
	//is the user subscribed to a topic
	public boolean doesUserListContainTopic(String uid, String passedTopic){
		
		if(userHM.get(uid).isTopicInList(passedTopic) == true){
			// the user has the topic
			return true;
		}else{
			//the user does not have the topic
			return false;
		}
		
	}
	
	/*
	 * 
	 * Topic methods
	 * 
	 */
	
	//add a topic to the system for all users to subscribe to
	public void addTopic(String topicTitle, LinkedList<String> keyWords){
		
		synchronized (topicHM) {
			topicHM.put(topicTitle, new topic(topicTitle, keyWords));
		}
		
	}
	
	//remove a topic from the entire system and all users
	public void removeTopic(String topicTitle){
		
		HashMap subscribers = topicHM.get(topicTitle).getSubscribers();
		Set subs = subscribers.keySet();
		String[] sub = (String[]) subs.toArray( new String[subs.size()]);
		
		for(int i = 0; i < sub.length; i++){
			removeTopicFromUserProfile(sub[i], topicTitle);
		}
		
		synchronized (topicHM) {
			topicHM.remove(topicTitle);
		}
		
	}
	
	//add a user to the topic's list of user that are subscribed to it
	public void addUserToTopic(String uID, String topicTitle){
		
		synchronized (topicHM.get(topicTitle)) {
			topicHM.get(topicTitle).addUser(uID);
		}
	}
	
	//remove a user from the topic's list of user that are subscribed to it
	public void removeUserFromTopic(String uID, String topicTitle){
		
		synchronized (topicHM.get(topicTitle)) {
			topicHM.get(topicTitle).removeUser(uID);
		}
		
	}
	
	//list all topics users can subscribe to 
	public Set<String> listAllTopics(){
		
		Set<String> set = topicHM.keySet();
		return set;
		
	}
	
	//get a list of subscribers to a topic
	public Set getSubscribers(String topic){
		
		HashMap hm = topicHM.get(topic).getSubscribers();
		Set<String> set = hm.keySet();
		return set;
		
	}
	
	//does a specific topic exist
	public boolean doesTopicExists(String passedTopic){
		if(topicHM.get(passedTopic) == null){
			return false;
		}else{
			return true;
		}
	}
		
	
	
	/*
	 * 
	 * Event methods
	 * 
	 */
	
	//add an event to the event hash map 
	public void addEvent(event e){
		synchronized (eventHM) {
			eventHM.put(e.getID(), e);
		}
	}
	
	//remove an event from the hash map
	public void removeEvent(event e){
		
		synchronized (eventHM) {
			eventHM.remove(e.getID());
		}
	}
	
	//get an event from the hash map
	public event getEvent(long eventID){
		return eventHM.get(eventID);
		
	}


}
