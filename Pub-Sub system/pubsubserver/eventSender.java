import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;

import org.json.simple.JSONObject;
/*
 * sends events to clients that are online and logs events for clients that are off line
 */

public class eventSender extends Thread {

	repository_of_lists lists;
	event sendEvent;
	String eventTopic = "";
	int portNumber = 1999;
	public eventSender(repository_of_lists list, event e){
		lists = list;
		sendEvent = e;
		eventTopic = e.getTopic();
	}
	
		// get the list of users from the topic class
		// get the users IP address 
		// try to send the event 
	public void run(){
		
		Set subs = lists.getSubscribers(eventTopic);
		int setSize = subs.size();
		String[] array = (String[]) subs.toArray( new String[subs.size()]);
		for (int i = 0; i < setSize; i++){
			String uID = array[i];
			
			String subscriberIp = lists.getUserIP(uID);
			//System.out.println("eventSender: uid: "+ uID + " :ip: " + subscriberIp);
			
			Socket clientSocket;
			try {
				
				// if the server can not connect to the client ... log the event
				
				//try to establish connection to client
				clientSocket = new Socket(subscriberIp, portNumber);
				DataOutputStream dataToServer = new DataOutputStream(clientSocket.getOutputStream());
				BufferedReader dataFromServer = new BufferedReader(new  InputStreamReader(clientSocket.getInputStream()));
				
				
				JSONObject obj = new JSONObject();
				obj.put("command", "event");
				obj.put("title", sendEvent.getTitle());
				obj.put("content", sendEvent.getContent());
				obj.put("topic", sendEvent.getTopic());
				
				String sendString = obj.toJSONString();

				//System.out.println("Event sender sending event");
				dataToServer.writeBytes(sendString + " \n");

				clientSocket.close();

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				
				// log event here
				System.out.println("client nopt online ... logging event");
				lists.userAddMissedEvent(uID, sendEvent.getID());
			}
		

		
		}
		

		
		
	}
	
}
