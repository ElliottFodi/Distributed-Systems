import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;








import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * listen for an admin connection to the server
 */

class listenForAdmin extends Thread{
	int port = 1990;
	repository_of_lists lists;
	active_queue eventQueue;
	
	public listenForAdmin(repository_of_lists list, active_queue queue){
		lists = list;
		eventQueue = queue;
	}
	
	@SuppressWarnings("unchecked")
	public void run(){
		try {
			
			// accepting only 1 administrator connection
			//System.out.println("admin thread: accepting connection");
			@SuppressWarnings("resource")
			ServerSocket adminSocket = new ServerSocket(port);
			
			//loop and accept the admin client when it connects to issue a command
			while(true){
				Socket admin_client_Socket = adminSocket.accept();
				BufferedReader xferFromClient = new BufferedReader(new InputStreamReader(admin_client_Socket.getInputStream()));
				DataOutputStream xferToClient = new DataOutputStream(admin_client_Socket.getOutputStream());
	
				
				JSONParser jsonParser = new JSONParser();
				String command = "";				
	
				//admin commands
					try{
						JSONObject obj = (JSONObject) jsonParser.parse(xferFromClient.readLine());
						command = (String) obj.get("command");
						System.out.println("command: " + command);
						String sendResponse = "";
						JSONObject response = new JSONObject();
						JSONArray jArray =  new JSONArray();
						boolean success = true;
	
						switch(command){
						
							case "addUser":
								addUser(obj);
								response.put("command", "addUser_success");
								sendResponse = response.toJSONString();
								xferToClient.writeBytes(sendResponse +"\n");
								xferToClient.close();
								break;
								
							case "addStock":
								addStock(obj);
								response.put("command", "addStock_success");
								sendResponse = response.toJSONString();
								xferToClient.writeBytes(sendResponse +"\n");
								xferToClient.close();
								break;
								
							case "removeUser":
								removeUser(obj);
								response.put("command", "removeUser_success");
								sendResponse = response.toJSONString();
								xferToClient.writeBytes(sendResponse +"\n");
								xferToClient.close();
								break;
								
							case "removeStock":
								removeStock(obj);
								response.put("command", "removeStock_success");
								sendResponse = response.toJSONString();
								xferToClient.writeBytes(sendResponse +"\n");
								xferToClient.close();
								break;
								
							case "listUsers":
								jArray = listUsers();
								xferToClient.writeBytes(jArray.toJSONString() +"\n");
								break;
						
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
								if ( buy == true){
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
								jArray = listStocks();
								xferToClient.writeBytes(jArray.toJSONString() +"\n");
								break;
								
							case "listMyStocks":
								jArray = listMyStocks(obj);
								xferToClient.writeBytes(jArray.toJSONString() +"\n");
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
									
							case "addShares":
								addShares(obj);
								response.put("command", "add_shares_success");
								sendResponse = response.toJSONString();
								xferToClient.writeBytes(sendResponse +"\n");
								break;
								
							case "removeShares":
								removeShares(obj);
								response.put("command", "remove_shares_success");
								sendResponse = response.toJSONString();
								xferToClient.writeBytes(sendResponse +"\n");
								break;
								
							case "myShares":
								jArray = getShares(obj);
								xferToClient.writeBytes(jArray.toJSONString() +"\n");
								break;
									
							case "close":
								// send message closing connection
								admin_client_Socket.close();
								System.out.println("closing connection");
								break;
								
							default:
								xferToClient.writeBytes(command + ": is an invalid command, enter a valid command\n");
								
						} //switch end
						
					}catch(ParseException e){
						e.printStackTrace();
					}
			}// end while
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("catch error: " + e);
		}
		
	} //end run
	
	//add user to the system
	public void addUser(JSONObject passedObj){
		JSONObject obj = passedObj;
		
		String uID = (String)obj.get("user");
		long money = (long)obj.get("money");
		lists.addUser(uID, money);
		System.out.println("admin added user");
		
	}
	
	//add stock for users to subscribe to 
	public void addStock(JSONObject passedObj){
		JSONObject obj = passedObj;
		String stockID = (String)obj.get("stockID");
		lists.addStock(stockID);
		System.out.println("admin added stock");
	}
	
	//remove user fromt he system
	public void removeUser(JSONObject passedObj){
		JSONObject obj = passedObj;
		String uID = (String)obj.get("user");
		lists.removeUser(uID);
		System.out.println("admin removed user");
	}
	
	//remove stock from the entire systema nd all users
	public void removeStock(JSONObject passedObj){
		JSONObject obj = passedObj;
		String stockID = (String)obj.get("stockID");
		lists.removeStock(stockID);
		System.out.println("admin removed stock");
	}
	
	//list all users in the system
	@SuppressWarnings("unchecked")
	public JSONArray listUsers(){
		
		System.out.println("in list users");
		String[] users = lists.listUsers();
		
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < users.length; i++){
			jsonArray.add(users[i]);
		}
		System.out.println("admin list users");
		return jsonArray;
		
	}
	
	//sell a stock for a user
	public boolean sell(JSONObject passedObj){
		JSONObject obj = passedObj;
		
		int eventID = -1;
		String stockID = (String) obj.get("stockID");
		long sellPrice = Long.parseLong( (String) obj.get("sellPrice"));
		String sellerID = (String) obj.get("user");
		
		//check if the stock exists
		System.out.println("admin sell");
		if(lists.doesStockExists(stockID) == true){
			//stock exists
			eventQueue.addEventToQueue(new event(eventID, sellPrice, stockID, sellerID));
			return true;
		}else{
			//topic does not exists
			return false;
		}
	}
	
	//subscribe to a stock for a user
	public void subscribe(JSONObject passedObj){

		//add stocks to the users list of sub'd stocks
		//add user to the list of sub'd users under the stock

		JSONObject obj = passedObj;
		
		String uID = (String) obj.get("user");
		String stockID = (String) obj.get("stockID");
		//System.out.println("subscribe user: " + uID);
		//System.out.println("subscribe topic: " + stockID);
		
		if(lists.doesStockExists(stockID) == true){
			lists.addUserToStockAdvertList(uID, stockID);
			lists.addStockToUserProfile(uID, stockID);
		}else{
			//do nothing
		}

		System.out.println("admin subscribe");
		
	}
	
	//buy a stock for a user
	public boolean buy(JSONObject passed_obj){
		JSONObject obj = passed_obj;
		
		String uID = (String)obj.get("user");
		long eventID = Long.parseLong( (String) obj.get("eventID"));
		
		boolean success = lists.userBuyShare(uID, eventID);
		System.out.println("admin buy");

		return success;
	}
	
	//unsubscribe to a stock for a user
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
		
		System.out.println("admin unsubscribe");
		

	}
	
	//unsubscribe to all stocks for a user
	public void unsubscribeAll(JSONObject passedObj){
		//remove user from all stock lists 
		
		JSONObject obj = passedObj;
		
		String uID = (String) obj.get("user");
		
		lists.removeUserFromAllStockAdvertLists(uID);
		System.out.println("admin unsubscribeAll");
	}
	
	//list all available stocks 
	@SuppressWarnings("unchecked")
	public JSONArray listStocks(){
		//send list of all stocks to user
		String[] stocksArray = lists.listAllStocks();
		
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < stocksArray.length; i++){
			jsonArray.add(stocksArray[i]);
		}
		System.out.println("admin listStocks");
		return jsonArray;
	}
	
	//list all stocks a user is subscribed to 
	@SuppressWarnings("unchecked")
	public JSONArray listMyStocks(JSONObject passedObj){
		//send list of the user's stocks to user
		JSONObject obj = passedObj;
		
		String uID = (String) obj.get("user");
		String[] userStocks = lists.listAllUserStocks(uID);
		
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < userStocks.length; i++){
			jsonArray.add(userStocks[i]);
		}
		System.out.println("admin listMyStocks");
		return jsonArray;
	}
	
	//add money to a users account
	public void addMoney(JSONObject passed_obj){
		
		// add money to the users account
		JSONObject obj = passed_obj;
		
		String uID = (String)obj.get("user");
		long money = Long.parseLong((String) obj.get("money"));
		lists.userAddMoney(uID, money);
		System.out.println("admin added money");
	}
	
	//list the money the user currently has
	public long getMoney(JSONObject passed_obj){
		
		// return the amount of money the user has
		JSONObject obj = passed_obj;
		String uID = (String)obj.get("user");
		System.out.println("admin get money");
		return lists.userGetMoney(uID);
		
			
	}
	
	//list the shares the user currently has
	@SuppressWarnings("unchecked")
	public JSONArray getShares(JSONObject passed_obj){
		JSONObject obj = passed_obj;
		String uID = (String)obj.get("user");
		String[] shares = lists.userGetShares(uID);
		
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < shares.length; i++){
			jsonArray.add(shares[i]);
		}
		System.out.println("admin list shares");
		
		return jsonArray;
	}
	
	//add a share to a users account
	public void addShares(JSONObject passed_obj){
		
		JSONObject obj = passed_obj;
		String uID = (String)obj.get("user");
		String stock = (String)obj.get("stockID");
		int shares = Integer.parseInt((String)obj.get("shares"));
		
		lists.userAddShares(uID, stock, shares);
		
	}
	
	//remove a share form a users account
	public void removeShares(JSONObject passed_obj){
		JSONObject obj = passed_obj;
		String uID = (String)obj.get("user");
		String stock = (String)obj.get("stockID");
		int shares = Integer.parseInt( (String)obj.get("shares"));
		
		lists.userRemoveShares(uID, stock, shares);
	}
}

