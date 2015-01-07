import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/*
 * this class contains all the commands the user can enter
 * this is where the user is prompted for information and any replies from the server are displayed here 
 * the method names correspond to there purpose 
 */

public class userCommands {

	Scanner scanner;
	String IP = "";
	String uID = "";
	establishConnection serverConnection;
	public userCommands(Scanner scan, establishConnection conect, String uid){
		scanner = scan;
		uID = uid;
		// problem here
		serverConnection = conect;
		
	}
	
	public void publish(){

		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "publish");
		System.out.println("Enter title: ");
		requestObj.put("title", scanner.nextLine());
		System.out.println("Enter topic: ");
		requestObj.put("topic", scanner.nextLine());
		System.out.println("Enter content: ");
		requestObj.put("content", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		if(replyObj.get("command").equals("DNE")){
			System.out.println("Topics does not exist");
		}else{
			System.out.println("publish " + replyObj.get("command"));			
		}
		
	}
	
	public void subscribe(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "subscribe");
		requestObj.put("user", uID);
		System.out.println("Enter topic: ");
		requestObj.put("topic", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("publish " + replyObj.get("command"));
		
	}
	
	public void unsubscribe(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "unsubscribe");
		requestObj.put("user", uID);
		System.out.println("Enter topic: ");
		requestObj.put("topic", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("publish " + replyObj.get("command"));
	}
	
	public void unscubscribeAll(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "unsubscribeAll");
		requestObj.put("user", uID);
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("publish " + replyObj.get("command"));
	}
	
	public void listTopics(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "listTopics");
		requestObj.put("user", uID);
		
		serverConnection.connectToServer();
		JSONArray myTopics = serverConnection.sendListCommand(requestObj);
		
		for(int i = 0; i < myTopics.size(); i++){
			System.out.println(myTopics.get(i));
		}
	}
	
	public void listMyTopics(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "listMyTopics");
		requestObj.put("user", uID);
		
		serverConnection.connectToServer();
		JSONArray myTopics = serverConnection.sendListCommand(requestObj);
		
		for(int i = 0; i < myTopics.size(); i++){
			System.out.println(myTopics.get(i));
		}
	}
	
	public void advertise(){

		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "publish");
		System.out.println("Enter title: ");
		requestObj.put("title", scanner.nextLine());
		System.out.println("Enter topic: ");
		requestObj.put("topic", scanner.nextLine());
		System.out.println("Enter content: ");
		requestObj.put("content", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		if(replyObj.get("command").equals("DNE")){
			System.out.println("Topics does not exist");
		}else{
			System.out.println("publish " + replyObj.get("command"));			
		}
	}
	
	//incase the user logged in the the wrong ID
	public void retypeUserID(){
		System.out.println("Enter new user ID");
		
		uID = scanner.nextLine();
		
	}
}
