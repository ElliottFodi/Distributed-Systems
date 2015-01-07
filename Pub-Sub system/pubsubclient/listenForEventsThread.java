import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * listens for incoming events
 */

public class listenForEventsThread extends Thread {

	public listenForEventsThread(){
		
	}
	
	public void run(){
		
		try {
			
			// socket to listen for incoming events while the client is online
			ServerSocket clientEventListenerSocket = new ServerSocket(1999);
			
			while(true){
				Socket server_socket = clientEventListenerSocket.accept();
				BufferedReader eventFromServer = new BufferedReader(new InputStreamReader(server_socket.getInputStream()));
				
				
				//display events 
				JSONParser jsonParser = new JSONParser();
				JSONObject reply = (JSONObject) jsonParser.parse(eventFromServer.readLine());
				
				String command = (String) reply.get("command");
				
				if(command.equals("event")){
					System.out.println("Event Title: " + reply.get("title"));
					System.out.println("Event Topic: " + reply.get("topic"));
					System.out.println("Event Content: " + reply.get("content"));
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
