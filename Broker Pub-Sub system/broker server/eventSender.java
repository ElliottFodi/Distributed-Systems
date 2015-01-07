//import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.InputStreamReader;

import java.net.Socket;
import java.net.UnknownHostException;


import org.json.simple.JSONObject;


public class eventSender extends Thread {

	repository_of_lists lists;
	event sendEvent;
	String stockID = "";
	
	public eventSender(repository_of_lists list, event e){
		lists = list;
		sendEvent = e;
		stockID = e.getStockID();
	}
	

	@SuppressWarnings("unchecked")
	public void run(){
		
		// get a list of users subscribed to the stock so the event can be advertised
		String[] subs = lists.getUsersAdvertisedToStock(stockID);

		for (int i = 0; i < subs.length; i++){
			
			// get the users IP address and attempt to send the event
			String uID = subs[i];
			String subscriberIp = lists.getUserIP(uID);
			//System.out.println("eventSender: uid: "+ uID + " :ip: " + subscriberIp);
			Socket clientSocket;
			
			try {
				
				// if the server can not connect to the client ... log the event
				
				clientSocket = new Socket(subscriberIp, 1999);
				DataOutputStream dataToServer = new DataOutputStream(clientSocket.getOutputStream());
				
				// send the event via json
				JSONObject obj = new JSONObject();
				obj.put("command", "event");
				obj.put("eventID", sendEvent.getEventID());
				obj.put("sellPrice", sendEvent.getSellPrice());
				obj.put("sellerID", sendEvent.getSellerID());
				obj.put("stockID", sendEvent.getStockID());
				
				String sendString = obj.toJSONString();

				//System.out.println("Event sender sending event");
				dataToServer.writeBytes(sendString + " \n");

				clientSocket.close();

			} catch (UnknownHostException e1) {
				
				e1.printStackTrace();
				
			} catch (IOException e1) {
				
				// log event here
				System.out.println("client not online ... logging event");
				lists.userAddMissedEvent(uID, sendEvent.getEventID());
			}
		

		
		}
		

		
		
	}
	
}
