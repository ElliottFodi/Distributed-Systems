import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*
 * this class contains all the commands the user can enter
 * this is where the user is prompted for information and any replies from the server are displayed here 
 * the method names correspond to the command they control
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
	
	@SuppressWarnings("unchecked")
	public void sell(){

		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "sell");
		requestObj.put("user", uID);
		
		System.out.println("Enter stock name: ");
		requestObj.put("stockID", scanner.nextLine());
		System.out.println("Enter selling price: ");
		requestObj.put("sellPrice", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		if(replyObj.get("command").equals("DNE")){
			System.out.println("Stock does not exist");
		}else{
			System.out.println("sell " + replyObj.get("command"));			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void buy(){
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "buy");
		requestObj.put("user", uID);
		System.out.println("Enter shareID ");
		long eventID = Long.parseLong(scanner .nextLine());
		requestObj.put("eventID", eventID);
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		System.out.println("buy " + replyObj.get("command"));
		
	}
	
	@SuppressWarnings("unchecked")
	public void subscribe(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "subscribe");
		requestObj.put("user", uID);
		System.out.println("Enter stock name: ");
		requestObj.put("stockID", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("subscribe " + replyObj.get("command"));
		
	}
	
	@SuppressWarnings("unchecked")
	public void unsubscribe(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "unsubscribe");
		requestObj.put("user", uID);
		System.out.println("Enter stock name: ");
		requestObj.put("stockID", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("unsubscribe " + replyObj.get("command"));
	}
	
	@SuppressWarnings("unchecked")
	public void unscubscribeAll(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "unsubscribeAll");
		requestObj.put("user", uID);
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("unsubscribeAll " + replyObj.get("command"));
	}
	
	@SuppressWarnings("unchecked")
	public void listStocks(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "listStocks");
		requestObj.put("user", uID);
		
		serverConnection.connectToServer();
		JSONArray myTopics = serverConnection.sendListCommand(requestObj);
		
		for(int i = 0; i < myTopics.size(); i++){
			System.out.println(myTopics.get(i));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void listMyStocks(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "listMyStocks");
		requestObj.put("user", uID);
		
		serverConnection.connectToServer();
		JSONArray myTopics = serverConnection.sendListCommand(requestObj);
		
		for(int i = 0; i < myTopics.size(); i++){
			System.out.println(myTopics.get(i));
		}
	}
	
	//incase the user logged in the the wrong ID
	public void retypeUserID(){
		System.out.println("Enter new user ID");
		
		uID = scanner.nextLine();
		
	}
	
	@SuppressWarnings("unchecked")
	public void addMoney(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "addMoney");
		requestObj.put("user", uID);
		System.out.println("Enter money to add: ");
		requestObj.put("money", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("addMoney " + replyObj.get("command"));
	}
	
	@SuppressWarnings("unchecked")
	public void getMoney(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "myMoney");
		requestObj.put("user", uID);
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("subscribe " + replyObj.get("command"));
		System.out.println("Money: " + replyObj.get("money"));
	}
	
	@SuppressWarnings("unchecked")
	public void myShares(){
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "myShares");
		requestObj.put("user", uID);
		
		serverConnection.connectToServer();
		JSONArray myTopics = serverConnection.sendListCommand(requestObj);
		
		for(int i = 0; i < myTopics.size(); i++){
			System.out.println(myTopics.get(i));
		}
	}
}
