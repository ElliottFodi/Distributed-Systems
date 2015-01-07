import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class listenForEventsThread extends Thread {
/*
 * opens a socket to listen for events from the server
 */
	public listenForEventsThread(){
		
	}
	
	public void run(){
		
		try {
			
			// socket to listen for incoming events while the client is online
			@SuppressWarnings("resource")
			ServerSocket clientEventListenerSocket = new ServerSocket(1999);
			
			while(true){
				Socket server_socket = clientEventListenerSocket.accept();
				BufferedReader eventFromServer = new BufferedReader(new InputStreamReader(server_socket.getInputStream()));
				
				
				//display events 
				JSONParser jsonParser = new JSONParser();
				JSONObject reply = (JSONObject) jsonParser.parse(eventFromServer.readLine());
				
				String command = (String) reply.get("command");
				
				if(command.equals("event")){
					System.out.println("Event shareID: " + reply.get("eventID"));
					System.out.println("Event price: " + reply.get("sellPrice"));
					System.out.println("Event seller: " + reply.get("sellerID"));
					System.out.println("Event stock name: " + reply.get("stockID"));
				}
				
				server_socket.close();
			}
			

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
