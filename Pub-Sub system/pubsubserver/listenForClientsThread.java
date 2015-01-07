import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class listenForClientsThread extends Thread{
	
	int port = 0;
	repository_of_lists lists;
	active_queue eventQueue;
	int generatedPort = 10000;
	
	public listenForClientsThread(int portNumber, repository_of_lists list, active_queue queue){
		port = portNumber;
		lists = list;
		eventQueue = queue;
	}
	
	
	//set up connection for accepting incoming connections, then generate port to 
	//communicate on and spawn thread to listen on that port
	public void run(){
		
		
		try {
			
			//create socket
			ServerSocket serverSocket = new ServerSocket(port);
			
			//loop and accept connections
			while(true){
				Socket clients_Socket = serverSocket.accept();
				//System.out.println("accepted new connection");
				DataOutputStream dataToClient = new DataOutputStream(clients_Socket.getOutputStream());

				//generate port to communicate on 
				if(generatedPort == 65530){
					generatedPort = 10000;
				}
				
				//spawn thread and send port to client
				handleClientThread thread1 = new handleClientThread(generatedPort, lists, eventQueue);
				thread1.start();
				String sendPort = Integer.toString(generatedPort);
				dataToClient.writeBytes(sendPort + "\n");
				
				generatedPort++;
				
				dataToClient.close();
				clients_Socket.close();
				//System.out.println("listener thread: waiting for new connections");

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	

}


/*
 * handle Client class takes care of all the client commands 
 * NOTE this should be in its own java file (its too big to be in here)
 */

class handleClientThread extends Thread{
	int port = 0;
	repository_of_lists lists;
	active_queue eventQueue;
	
	handleClientThread(int newPort, repository_of_lists list, active_queue queue){
		port = newPort;
		lists = list;
		eventQueue = queue;
	}
	
	//handle client commands
	public void run(){
		try {
			
			//set up socket to accept a client connection
			//System.out.println("handler thread: accepting connection");
			ServerSocket xferSocket = new ServerSocket(port);
			Socket clients_xfer_Socket = xferSocket.accept();
			BufferedReader xferFromClient = new BufferedReader(new InputStreamReader(clients_xfer_Socket.getInputStream()));
			DataOutputStream xferToClient = new DataOutputStream(clients_xfer_Socket.getOutputStream());
			//String xferText = "";
			
			// verify user ID
			String userID = "";
			userID = xferFromClient.readLine();
			//System.out.println("user id : " + userID);
			boolean verify = lists.verifyUser(userID);
			if(verify == false){
				xferToClient.writeBytes("Invalid User Name: closing connection\n");
				xferSocket.close();
				return;
			}else{
				xferToClient.writeBytes("valid\n");
			}

			System.out.println("client verified");
			
			//update user Ip address
			InetAddress ipAddress = clients_xfer_Socket.getInetAddress();
			String userIpAddress = ipAddress.toString();
			userIpAddress = userIpAddress.substring(1, userIpAddress.length());
			lists.updateUserIP(userID, userIpAddress);
			
			//System.out.println("client ip saved");
			
			//send out any missed events
			ArrayList missedEvents = lists.getUserMissedEvents(userID);
			//System.out.println("Users missed events: " + missedEvents.size());
			JSONObject missedEvent = new JSONObject();
			if(missedEvents.size() != 0){
				//send missed event
				for(int i = 0; i < missedEvents.size(); i++){
					//System.out.println("missed event id number" + missedEvents.indexOf(i) +": i: " + i);
					event missed = lists.getEvent((long)missedEvents.get(i));
					missedEvent.put("command", "event");
					//System.out.println("missed title: " + missed.getTitle());
					//System.out.println("missed content: " + missed.getContent());
					//System.out.println("missed topic: " + missed.getTopic());
					missedEvent.put("title", missed.getTitle());
					missedEvent.put("content", missed.getContent());
					missedEvent.put("topic", missed.getTopic());
					
					String sendEvent = missedEvent.toJSONString();
					xferToClient.writeBytes(sendEvent + "\n");
				}
				
			}
				
			missedEvent.put("command", "none");
			xferToClient.writeBytes(missedEvent.toJSONString() + "\n");
			//System.out.println("sent missed event msg");
			
			JSONParser jsonParser = new JSONParser();
			String command = "";
				
			//commands the client can use
			//if no data is sent to the client a success message is sent
				try{
					JSONObject obj = (JSONObject) jsonParser.parse(xferFromClient.readLine());
					command = (String) obj.get("command");
					System.out.println("command: " + command);
					String sendResponse = "";
					JSONObject response = new JSONObject();
					boolean success = true;

					switch(command){
						case "publish":
							success = publish(obj);
							if(success == true){
								response.put("command", "publish_success");
							}else{
								response.put("command", "DNE");
							}
							sendResponse = response.toJSONString();
							xferToClient.writeBytes(sendResponse +"\n");
							xferToClient.close();
							break;
							
						case "subscribe":
							subscribe(obj);
							response.put("command", "subscribe_success");
							sendResponse = response.toJSONString();
							xferToClient.writeBytes(sendResponse +"\n");
							break;
							
						case "unsubscribe":
							unsubscribe(obj);
							response.put("command", "unsibscribe_success");
							sendResponse = response.toJSONString();
							xferToClient.writeBytes(sendResponse +"\n");
							break;
							
						case "unsubscribeAll":
							unsubscribeAll(obj);
							response.put("command", "unsubscribe_to_all_success");
							sendResponse = response.toJSONString();
							xferToClient.writeBytes(sendResponse +"\n");
							break;
						
						case "listTopics":
							JSONArray jsonARRAY = listTopics();
							xferToClient.writeBytes(jsonARRAY.toJSONString() +"\n");
							break;
							
						case "listMyTopics":
							JSONArray jarray = listMyTopics(obj);
							xferToClient.writeBytes(jarray.toJSONString() +"\n");
							break;
							
						case "advertise":
							success = advertise(obj);
							
							if(success == true){
								response.put("command", "publish_success");
							}else{
								response.put("command", "DNE");
							}
							response.put("command", "advertise_success");
							sendResponse = response.toJSONString();
							xferToClient.writeBytes(sendResponse +"\n");
							break;
							
						case "close":
							// send message closing connection
							xferSocket.close();
							System.out.println("closing connection");
							break;
							
						default:
							xferToClient.writeBytes(command + ": is an invalid command, enter a valid command\n");
							
					} //switch end
					
				}catch(ParseException e){
					e.printStackTrace();
				}
				
			//} //while end
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("catch error: " + e);
		}
		
	} //end run
	
	//publish an event to the event queue
	public boolean publish(JSONObject passedObj){
		JSONObject obj = passedObj;
		
		int eventID = -1;
		String eventTitle = (String) obj.get("title");
		String eventContent = (String) obj.get("content");
		String eventTopicTitle = (String) obj.get("topic");
		//check if the topic exists
		
		if(lists.doesTopicExists(eventTopicTitle) == true){
			//topic exists
			eventQueue.addEventToQueue(new event(eventID, eventTitle, eventContent, eventTopicTitle));
			return true;
		}else{
			//topic does not exists
			return false;
		}
	}
	
	//place an advertise event in the queue
	public boolean advertise(JSONObject passedObj){
		
		JSONObject obj = passedObj;
		
		int eventID = -1;
		String eventTitle = (String) obj.get("title");
		String eventContent = (String) obj.get("content");
		String eventTopicTitle = (String) obj.get("topic");
		
		if(lists.doesTopicExists(eventTopicTitle) == true){
			//topic exists
			eventQueue.addEventToQueue(new event(eventID, eventTitle, eventContent, eventTopicTitle));
			return true;
		}else{
			//topic does not exists
			// send msg topic DNE
			return false;
		}
	}
	
	//subscribe to an event
	public void subscribe(JSONObject passedObj){

		//add topic to the users list of sub'd topics
		//add user to the list of sub'd users under the topic

		JSONObject obj = passedObj;
		
		String subscriberID = (String) obj.get("user");
		String subscribeTopic = (String) obj.get("topic");
		//System.out.println("subscribe user: " + subscriberID);
		//System.out.println("subscribe topic: " + subscribeTopic);
		
		if(lists.doesTopicExists(subscribeTopic) == true){
			//sub to the topic 
			lists.addUserToTopic(subscriberID, subscribeTopic);
			lists.addTopicToUserProfile(subscriberID, subscribeTopic);
		}else{
			//do nothing
		}

		
	}
	
	//unsubscribe to a topic
	public void unsubscribe(JSONObject passedObj){
		//remove topic from the users list of sub'd topics
		//remove user from the list of sub'd users under the topic
		
		JSONObject obj = passedObj;
		
		String subscriberID = (String) obj.get("user");
		String subscribeTopic = (String) obj.get("topic");
		
		if(lists.doesUserListContainTopic(subscriberID, subscribeTopic) == true){
			//if the user is sub'd to the topic 
			//unsub the user 
			lists.removeUserFromTopic(subscriberID, subscribeTopic);
			lists.removeTopicFromUserProfile(subscriberID, subscribeTopic);
		}else{
			//do nothing
			//System.out.println("doing nothing");
		}
		

	}
	
	//unsubscribe to all topics 
	public void unsubscribeAll(JSONObject passedObj){
		//remove all topics from the users list of sub'd topics
		//remove user from all topic lists 
		
		JSONObject obj = passedObj;
		
		String subscriberID = (String) obj.get("user");
		
		lists.removeAllTopicsFromUserProfile(subscriberID);
	}
	
	//list all topics 
	public JSONArray listTopics(){
		//send list of all topics to user
		Set setOfTopics = lists.listAllTopics();
		
		JSONArray jsonArray = new JSONArray();
		String[] array = (String[]) setOfTopics.toArray( new String[setOfTopics.size()]);
		for(int i = 0; i < setOfTopics.size(); i++){
			jsonArray.add(array[i]);
		}
		return jsonArray;
	}
	
	//list all topics the user is subscribed to 
	public JSONArray listMyTopics(JSONObject passedObj){
		//send list of the users topics to user
		JSONObject obj = passedObj;
		
		String subscriberID = (String) obj.get("user");
		LinkedList userTopics = lists.listAllUserTopics(subscriberID);
		
		JSONArray jsonArray = new JSONArray();
		int sizeOfList = userTopics.size();
		for(int i = 0; i < sizeOfList; i++){
			jsonArray.add(userTopics.get(i));
		}
		
		return jsonArray;
	}
	
}
