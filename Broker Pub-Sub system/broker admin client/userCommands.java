import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*
 * this class contains all the commands the user can enter
 * this is where the user is prompted for information and any replies 
 * from the server are displayed here the method name corresponds to what it does 
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
	public void addUser(){
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "addUser");
		System.out.println("Enter user name to be added: ");
		String uID = scanner.nextLine(); 
		requestObj.put("user", uID);

		System.out.println("Enter money: ");
		long money = Long.parseLong(scanner.nextLine()); 
		requestObj.put("money", money);
		
		serverConnection.connectToServer();
		serverConnection.sendCommand(requestObj);
	}
	
	@SuppressWarnings("unchecked")
	public void removeUser(){
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "removeUser");
		System.out.println("Enter user name to be removed: ");
		String uID = scanner.nextLine(); 
		requestObj.put("user", uID);
		
		serverConnection.connectToServer();
		serverConnection.sendCommand(requestObj);
	}
	
	@SuppressWarnings("unchecked")
	public void addStock(){
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "addStock");
		System.out.println("Enter stock name to be added: ");
		String stockID = scanner.nextLine(); 
		requestObj.put("stockID", stockID);
		
		serverConnection.connectToServer();
		serverConnection.sendCommand(requestObj);
	}
	
	@SuppressWarnings("unchecked")
	public void removeStock(){
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "removeStock");
		System.out.println("Enter stock name to be added: ");
		String stockID = scanner.nextLine(); 
		requestObj.put("stockID", stockID);
		
		serverConnection.connectToServer();
		serverConnection.sendCommand(requestObj);
	}
	
	@SuppressWarnings("unchecked")
	public void listUsers(){
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "listUsers");
		
		serverConnection.connectToServer();
		JSONArray users = serverConnection.sendListCommand(requestObj);
				
		System.out.println("listing users");
		for(int i = 0; i < users.size(); i++){
			System.out.println(users.get(i));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void sell(){

		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "sell");
		
		System.out.println("Enter user name:");
		requestObj.put("user", scanner.nextLine());
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
		System.out.println("enter user name");
		requestObj.put("user", scanner.nextLine());
		System.out.println("Enter shareID ");
		String eventID = scanner .nextLine();
		requestObj.put("eventID", eventID);
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		System.out.println("buy " + replyObj.get("command"));
		
	}
	
	@SuppressWarnings("unchecked")
	public void subscribe(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "subscribe");
		System.out.println("Enter user name");
		requestObj.put("user", scanner.nextLine());
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
		System.out.println("Enter user name:");
		requestObj.put("user", scanner.nextLine());
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
		System.out.println("Enter user name");
		requestObj.put("user", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("unsubscribeAll " + replyObj.get("command"));
	}
	
	@SuppressWarnings("unchecked")
	public void listStocks(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "listStocks");
		System.out.println("Enter user name");
		requestObj.put("user", scanner.nextLine());
		
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
		System.out.println("Enter user name");
		requestObj.put("user", scanner.nextLine());
		
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
		System.out.println("Enter user name");
		requestObj.put("user", scanner.nextLine());
		System.out.println("Enter amount to add: ");
		requestObj.put("money", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("addMoney " + replyObj.get("command"));
	}
	
	@SuppressWarnings("unchecked")
	public void getMoney(){
		
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "myMoney");
		System.out.println("Enter user name");
		requestObj.put("user", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("subscribe " + replyObj.get("command"));
		System.out.println("Money: " + replyObj.get("money"));
	}
	
	@SuppressWarnings("unchecked")
	public void addShares(){
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "addShares");
		System.out.println("Enter user name");
		requestObj.put("user", scanner.nextLine());
		System.out.println("Enter stock to add shares to");
		requestObj.put("stockID", scanner.nextLine());
		System.out.println("Enter amount of shares to add: ");
		requestObj.put("shares", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("addMoney " + replyObj.get("command"));
	}
	
	@SuppressWarnings("unchecked")
	public void removeShares(){
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "removeShares");
		System.out.println("Enter user name");
		requestObj.put("user", scanner.nextLine());
		System.out.println("Enter stock to add shares to");
		requestObj.put("stockID", scanner.nextLine());
		System.out.println("Enter amount of shares to add: ");
		requestObj.put("shares", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONObject replyObj = serverConnection.sendCommand(requestObj);
		
		System.out.println("addMoney " + replyObj.get("command"));
	}
	
	@SuppressWarnings("unchecked")
	public void myShares(){
		JSONObject requestObj = new JSONObject();
		requestObj.put("command", "myShares");
		System.out.println("Enter user name");
		requestObj.put("user", scanner.nextLine());
		
		serverConnection.connectToServer();
		JSONArray myShares = serverConnection.sendListCommand(requestObj);
		
		for(int i = 0; i < myShares.size(); i++){
			System.out.println(myShares.get(i));
		}
	}
}
