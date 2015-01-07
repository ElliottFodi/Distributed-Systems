import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
/*
 * local commands that are issued on the server
 * the method names correspond to what they do 
 */

public class localAdminCommands {
	
	repository_of_lists lists;
	Scanner scanner;
	active_queue eventQueue;
	
	public localAdminCommands(repository_of_lists list, Scanner scan_in, active_queue queue){
		lists = list;
		scanner = scan_in;
		eventQueue = queue;
	}

	public void adminAddTopic(){
		LinkedList<String> keyWords = new LinkedList<String>();
		System.out.println("Enter topic title: ");
		String topicTitle = scanner.nextLine(); 
		String keyword = "";		 
		while(!keyword.equals("finished")){
			System.out.println("Enter keywords: Type finished when you are done entering key words");
			keyword = scanner.nextLine();
			keyWords.add(keyword);
			
		}
		
		lists.addTopic(topicTitle, keyWords);
		System.out.println("Topic: " + topicTitle + " added");
		
	}
	
	public void adminAddUser(){
		System.out.println("Enter user name: ");
		String userName = scanner.nextLine(); 

		lists.addUser(userName);
		System.out.println("User: " + userName + " added");
	}
	
	public void adminRemoveUser(){
		System.out.println("Enter user name: ");
		String userName = scanner.nextLine(); 

		lists.removeUser(userName);
		System.out.println("User: " + userName + " removed");
	}
	
	public void adminRemoveTopic(){
		System.out.println("Enter the topic title");
		String topicTitle = scanner.nextLine();
		
		lists.removeTopic(topicTitle);
		System.out.println("Topic: " + topicTitle + " removed");
	}
	
	public void adminNotify(){
		// Possibly add publish commands here
	}
	
	public void adminListUsers(){
		Set setOfUsers = lists.listUsers();
		String[] array = (String[]) setOfUsers.toArray( new String[setOfUsers.size()]);
		
		for (int i = 0; i < array.length; i++){
			System.out.println("User: " + array[i]);
		}
	}
	
	public void adminListTopics(){
		
		Set setOfTopics = lists.listAllTopics();
		
		String[] array = (String[]) setOfTopics.toArray( new String[setOfTopics.size()]);

		for (int i = 0; i < array.length; i++){
			System.out.println("Topic: " + array[i]);
		}
	}
	
	
	public void adminPublish(){
		
		long eventID = -1;
		System.out.println("Enter title");
		String adminTitle = scanner.nextLine();
		System.out.println("Enter topic");
		String adminTopic = scanner.nextLine();
		System.out.println("Enter content");
		String adminContent = scanner.nextLine();
		event adminEvent = new event(eventID, adminTitle, adminContent, adminTopic);
		eventQueue.addEventToQueue(adminEvent);
	}
	
	public void adminSubscribe(){
		System.out.println("Enter User ID");
		String userID = scanner.nextLine();
		System.out.println("Enter topic");
		String topic = scanner.nextLine();
		
		lists.addUserToTopic(userID, topic);
		lists.addTopicToUserProfile(userID, topic);
	}
	
	public void adminUnsubscribeToTopic(){
		System.out.println("Enter User ID");
		String userID = scanner.nextLine();
		System.out.println("Enter topic");
		String topic = scanner.nextLine();
		
		lists.removeUserFromTopic(userID, topic);
		lists.removeTopicFromUserProfile(userID, topic);
	}
	
	public void adminUnsubscribeToAllTopics(){
		System.out.println("Enter User ID");
		String userID = scanner.nextLine();
		
		lists.removeAllTopicsFromUserProfile(userID);
	}
	
	public void adminListMyTopics(){
		System.out.println("Enter User ID");
		String userID = scanner.nextLine();
		LinkedList userTopics = lists.listAllUserTopics(userID);
		
		for(int i = 0; i < userTopics.size(); i++){
			System.out.println("My topic: " + userTopics.get(i));
		}
	}
}
