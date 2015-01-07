import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.Set;







import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * This class listens for incoming client connections, accepts and passes
 * the connection to a thread to deal with client commands. 
 */
public class listenForClientsThread extends Thread{
	
	int port = 0;
	repository_of_lists lists;
	active_queue eventQueue;
	int generatedPort = 10000;
	
	//pass in port number, repository and queue
	public listenForClientsThread(int portNumber, repository_of_lists list, active_queue queue){
		port = portNumber;
		lists = list;
		eventQueue = queue;
	}
	
	//thread run, this code is run when the thread is started
	public void run(){
		
		
		try {
			//create socket to listen for connections
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(port);
			
			//loop and accept connections
			while(true){
				Socket clients_Socket = serverSocket.accept();
				//System.out.println("accepted new connection");
				DataOutputStream dataToClient = new DataOutputStream(clients_Socket.getOutputStream());

				//generate port to be given to the client and used to listen on
				if(generatedPort == 65530){
					generatedPort = 10000;
				}
				
				//spawn thread to handle client commands
				handleClientThread thread1 = new handleClientThread(generatedPort, lists, eventQueue);
				thread1.start();
				
				//send port to the client
				String sendPort = Integer.toString(generatedPort);
				dataToClient.writeBytes(sendPort + "\n");
				
				generatedPort++;
				
				//close the client connection
				dataToClient.close();
				clients_Socket.close();
				//System.out.println("listener thread: accepting new connection");

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	

}

/*
 * Handler thread, this class deal with all client commands and calls the proper methods on the server
 * NOTE this should have its own java file
 * Some print statements have been left commented out, they are used for debugging 
 */
class handleClientThread extends Thread{
	int port = 0;
	repository_of_lists lists;
	active_queue eventQueue;
	
	//Receive port to communicate on, repository and queue
	handleClientThread(int newPort, repository_of_lists list, active_queue queue){
		port = newPort;
		lists = list;
		eventQueue = queue;
	}
	
	@SuppressWarnings("unchecked")
	public void run(){
		try {
			
			//create socket and accept client connection
			//System.out.println("handler thread: accepting connection");
			ServerSocket xferSocket = new ServerSocket(port);
			Socket clients_xfer_Socket = xferSocket.accept();
			BufferedReader xferFromClient = new BufferedReader(new InputStreamReader(clients_xfer_Socket.getInputStream()));
			DataOutputStream xferToClient = new DataOutputStream(clients_xfer_Socket.getOutputStream());
			
			// verify user ID
			String userID = "";
			userID = xferFromClient.readLine();
			//System.out.println("Handler Thread: user id : " + userID);
			boolean verify = lists.verifyUser(userID);
			if(verify == false){
				xferToClient.writeBytes("Invalid User Name: closing connection\n");
				xferSocket.close();
				return;
			}else{
				xferToClient.writeBytes("valid\n");
			}

			System.out.println("Handler Thread: client verified");
			
			//update user IP address
			InetAddress ipAddress = clients_xfer_Socket.getInetAddress();
			String userIpAddress = ipAddress.toString();
			userIpAddress = userIpAddress.substring(1, userIpAddress.length());
			lists.updateUserIP(userID, userIpAddress);
			
			//System.out.println("Handler Thread: client ip saved");
			
			//send out any missed events
			ArrayList<Long> missedEvents = lists.getUserMissedEvents(userID);
			//System.out.println("Handler Thread: User's missed events: " + missedEvents.size());
			JSONObject missedEvent = new JSONObject();
			if(missedEvents.size() != 0){
				//send missed event
				for(int i = 0; i < missedEvents.size(); i++){
					System.out.println("missed event id number" + missedEvents.indexOf(i) +": i: " + i);
					event missed = lists.getEvent((long)missedEvents.get(i));
					
					//System.out.println("missed eventID: " + missed.getEventID());
					//System.out.println("missed sellPrice: " + missed.getSellPrice());
					//System.out.println("missed sellerID: " + missed.getStockID());
					//System.out.println("missed stockID: " + missed.getStockID());
					
					missedEvent.put("command", "event");
					missedEvent.put("eventID", missed.getEventID());
					missedEvent.put("sellPrice", missed.getSellPrice());
					missedEvent.put("sellerID", missed.getSellerID());
					missedEvent.put("stockID", missed.getStockID());
					
					String sendEvent = missedEvent.toJSONString();
					xferToClient.writeBytes(sendEvent + "\n");
				}
				
			}
				
			//send command telling the client there are no more missed events
			missedEvent.put("command", "none");
			xferToClient.writeBytes(missedEvent.toJSONString() + "\n");
			//System.out.println("sent missed event msg");
			
			JSONParser jsonParser = new JSONParser();
			String command = "";
				
			//list of available commands
				try{
					JSONObject obj = (JSONObject) jsonParser.parse(xferFromClient.readLine());
					command = (String) obj.get("command");
					System.out.println("command: " + command);
					String sendResponse = "";
					JSONObject response = new JSONObject();
					JSONArray jsonArray = new JSONArray();
					boolean success = true;

					switch(command){
						case "sell":
							success = sell(obj);
							if(success == true){
								response.put("command", "publish_success");
							}else{
								response.put("command", "DNE");
							}
							sendResponse = response.toJSONString();
							xferToClient.writeBytes(sendResponse +"\n");
							xferToClient.close();
							break;
							
						case "buy":
							boolean buy = buy(obj);
							
							if(buy == true){
								response.put("command", "buy_success");
							}else{
								response.put("command", "buy_failed_to_slow");
							}
							sendResponse = response.toJSONString();
							xferToClient.writeBytes(sendResponse +"\n");
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
						
						case "listStocks":
							jsonArray = listStocks();
							xferToClient.writeBytes(jsonArray.toJSONString() +"\n");
							break;
							
						case "listMyStocks":
							jsonArray = listMyStocks(obj);
							xferToClient.writeBytes(jsonArray.toJSONString() +"\n");
							break;
							
						case "myMoney":
							response.put("command", "my_money_success");
							response.put("money", getMoney(obj));
							sendResponse = response.toJSONString();
							xferToClient.writeBytes(sendResponse +"\n");
							break;
							
						case "addMoney":
								addMoney(obj);
								response.put("command", "add_money_success");
								sendResponse = response.toJSONString();
								xferToClient.writeBytes(sendResponse +"\n");
								break;
							
						case "myShares":
							jsonArray = getShares(obj);
							xferToClient.writeBytes(jsonArray.toJSONString() +"\n");
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
			//System.out.println("catch error: " + e);
		}
		
	} //end run
	
	
	/*
	 * Implementation of the above commands 
	 */
	
	public boolean sell(JSONObject passedObj){
		
		//add event to the event queue
		
		JSONObject obj = passedObj;
		long eventID = -1;
		String stockID = (String) obj.get("stockID");
		long sellPrice = Long.parseLong( (String) obj.get("sellPrice"));
		String sellerID = (String) obj.get("user");
		
		//check if the stock exists
		if(lists.doesStockExists(stockID) == true){
			//stock exists
			eventQueue.addEventToQueue(new event(eventID, sellPrice, stockID, sellerID));
			return true;
		}else{
			//stock does not exists
			return false;
		}
	}
	
	
	public void subscribe(JSONObject passedObj){

		//add stock to the users list of sub'd stock
		//add user to the list of sub'd users under the stock

		JSONObject obj = passedObj;
		
		String uID = (String) obj.get("user");
		String stockID = (String) obj.get("stockID");
		//System.out.println("subscribe user: " + uID);
		//System.out.println("subscribe stock: " + stockID);
		
		if(lists.doesStockExists(stockID) == true){
			//sub to the topic 
			lists.addUserToStockAdvertList(uID, stockID);
			lists.addStockToUserProfile(uID, stockID);
		}else{
			//do nothing
		}

		
	}
	
	public boolean buy(JSONObject passed_obj){
		
		//transfer shares and money when a share is bought
		//also delete the share posting
		
		JSONObject obj = passed_obj;
		
		String uID = (String)obj.get("user");
		long eventID = (long)obj.get("eventID");
		
		boolean success = lists.userBuyShare(uID, eventID);
		return success;
	}
	
	public void unsubscribe(JSONObject passedObj){
		
		//remove user from the list of sub'd users under the stock
		
		JSONObject obj = passedObj;
		
		String uID = (String) obj.get("user");
		String stockID = (String) obj.get("stockID");
		
		if(lists.doesUserListContainStock(uID, stockID) == true){
			//if the user is sub'd to the stock, unsub the user 
			lists.removeUserFromStockAdvertList(uID, stockID);
		}else{
			//do nothing
		}
		

	}
	
	public void unsubscribeAll(JSONObject passedObj){
		
		//remove user from all stocks lists 
		
		JSONObject obj = passedObj;
		
		String uID = (String) obj.get("user");
		
		lists.removeUserFromAllStockAdvertLists(uID);
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray listStocks(){
		//send list of all stocks to user
		String[] stocksArray = lists.listAllStocks();
		
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < stocksArray.length; i++){
			jsonArray.add(stocksArray[i]);
		}
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray listMyStocks(JSONObject passedObj){
		
		//send list of the users stocks to user
		
		JSONObject obj = passedObj;
		
		String uID = (String) obj.get("user");
		String[] userStocks = lists.listAllUserStocks(uID);
		
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < userStocks.length; i++){
			jsonArray.add(userStocks[i]);
		}
		
		return jsonArray;
	}
	
	public void addMoney(JSONObject passed_obj){
		
		//add money to the users account
		
		JSONObject obj = passed_obj;
		
		String uID = (String)obj.get("user");
		long money = Long.parseLong((String) obj.get("money"));
		lists.userAddMoney(uID, money);
		
	}
	
	public long getMoney(JSONObject passed_obj){
		
		//get the money the user has 
		
		JSONObject obj = passed_obj;
		String uID = (String)obj.get("user");
		return lists.userGetMoney(uID);
			
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getShares(JSONObject passed_obj){
		
		//return a JSON array of shares the user has
		
		JSONObject obj = passed_obj;
		String uID = (String)obj.get("user");
		String[] shares = lists.userGetShares(uID);
		
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < shares.length; i++){
			jsonArray.add(shares[i]);
		}
		//System.out.println("admin list shares");
		
		return jsonArray;
	}
}
