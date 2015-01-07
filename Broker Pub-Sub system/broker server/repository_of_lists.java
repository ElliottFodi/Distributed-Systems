import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/*
 * This class interacts with the different classes and maintains the lists for each class
 * this si the brain
 */


public class repository_of_lists {


	// list of all topics
	// Hash map of topics, with the key == the topic title and the value == the topic object
	HashMap<String, stock> stockHM = new HashMap<String, stock>();
	
	
	// list of all users
	// Hash map of users, with the key == the user id and the value == the user object 
	HashMap<String, user> userHM = new HashMap<String, user>();
	
	
	// list of all events
	// Hash map of events, with the key == the event id and the value == the event object 
	HashMap<Long, event> eventHM = new HashMap<Long, event>();

	
	public repository_of_lists(){
		
	}
	
	
	/*
	 * 
	 * User methods
	 * 
	 */
	
	public void addUser(String uID, long money){
		
		// add user to user hash map
		synchronized (userHM) {
			userHM.put(uID, new user(uID, money));
		}
		
	}
	
	public void removeUser(String uID){
		
		// get list of stocks user is sub'd to and remove user from each stock
		String[] uStocks = userHM.get(uID).getUserSubscriptionsToStocks();

		for(int i = 0; i < uStocks.length; i++){
			removeUserFromStockAdvertList(uID, uStocks[i]);
		}
		
		// remove user from user hash map
		synchronized (userHM) {
			userHM.remove(uID);
		}
	}
	
	public void addStockToUserProfile(String passed_uID, String passed_stock_id){
		
		// add stock to the list of stocks the user is subscribed to
		synchronized (userHM.get(passed_uID)) {
			userHM.get(passed_uID).addStock(passed_stock_id);
		}
		
	}
	
	public void removeStockFromUserProfile(String passed_uID, String passed_stock_id){
		
		// remove stock from the list of stocks the user is subscribed to
		synchronized (userHM.get(passed_uID)) {
			userHM.get(passed_uID).removeStock(passed_stock_id);
		}
	}
	
	
	public String[] listAllUserStocks(String uID){
		
		// lists all of the stocks the user is subscribed to
		String[] uStocks = userHM.get(uID).getUserSubscriptionsToStocks();
		return uStocks;
	}
	
	public void updateUserIP(String uID, String IP){
		
		// updates the users IP address
		synchronized (userHM.get(uID)) {
			userHM.get(uID).setIP(IP);
		}
	}
	
	public String getUserIP(String uID){
		
		// returns the users IP address
		String ip = "";
		synchronized (userHM.get(uID)) {
			ip = userHM.get(uID).getIP();
		}
		return ip;
	}
	
	public boolean verifyUser(String uID){
		
		// checks if the user is in the user Hash Map
		if(userHM.get(uID) == null){
			// user is not in the Hash map
			//System.out.println("verify == false");
			return false;
		}else{
			// user is in the hash map
			//System.out.println("verify == true");
			return true;
		}
	}
	
	public String[] listUsers(){
		
		// lists all of the users in the system
		Set<String> Users = userHM.keySet();
		String[] users = (String[]) Users.toArray( new String[Users.size()]);
		return users;
	}
	
	public ArrayList<Long> getUserMissedEvents(String uID){
		
		// list any events the user missed
		ArrayList<Long> missedEvents;
		synchronized (userHM.get(uID)) {
			missedEvents = userHM.get(uID).getUserMissedEvents();
		}
		
		return missedEvents;
	}
	
	public void clearUserMissedEvents(String uID){
		
		// delete any missed events the user has logged
		synchronized (userHM.get(uID)) {
			userHM.get(uID).clearUserMissedEvents();
		}
		
	}
	
	public void userAddMissedEvent(String uID, long eID){
		
		// add a missed event to the users profile
		synchronized (userHM.get(uID)) {
			userHM.get(uID).addMissedEvent(eID);
		}
		
	}
	
	public boolean doesUserListContainStock(String uid, String passedStock){
		
		// is the user subscribed to a specific stock
		if(userHM.get(uid).isStockInList(passedStock) == true){
			// the user has the stock
			return true;
		}else{
			//the user does not have the stock
			return false;
		}
		
	}
	
	public boolean userBuyShare(String passed_buyer_id, long passed_event_id ){
		
		// transfers money and shares from the buyer and the seller 
		
		String sellerID ="";
		String stock = "";
		long sellPrice = -1;
		
		//add a check here to see if the event exists
		
		if (eventHM.get(passed_event_id) == null){
			// the share was bought
			return false;
		}
		
		// get information from the event
		synchronized (eventHM.get(passed_event_id)) {
			sellerID = eventHM.get(passed_event_id).getSellerID();
			stock = eventHM.get(passed_event_id).getStockID();
			sellPrice = eventHM.get(passed_event_id).getSellPrice();
		}
		
		// remove the event so no one else can buy it
		synchronized (eventHM) {
			eventHM.remove(passed_event_id);
		}

		synchronized (userHM.get(passed_buyer_id)) {
			
			//remove money from buyer
			userHM.get(passed_buyer_id).removeUserMoney(sellPrice);
			
			//add share to buyer
			userHM.get(passed_buyer_id).addShare(stock, 1);
		}
		
		synchronized (userHM.get(sellerID)) {
			
			//add money to seller
			userHM.get(sellerID).addUserMoney(sellPrice);
			
			//remove share from seller
			userHM.get(sellerID).removeShare(stock, 1);
		}
		
		// we got the share! we got the share! naa naa, na, naa naa 
		return true;

	}
	
	public long userGetMoney(String passed_uID){
		
		// return the money the user has on his account
		return userHM.get(passed_uID).getMoney();
	}
	
	public void userAddMoney(String passed_uID, long passed_money){
		
		// add money to the users account
		userHM.get(passed_uID).addUserMoney(passed_money);
	}
	
	public String[] userGetShares(String passed_uID){
		
		return userHM.get(passed_uID).getShares();
		
	}
	
	public void userAddShares(String passed_uID, String passed_stock, int passed_shares){
		
		synchronized (userHM.get(passed_uID)) {
			userHM.get(passed_uID).addShare(passed_stock, passed_shares);
		}
	}
	
	public void userRemoveShares(String passed_uID, String passed_stock, int passed_shares){
		synchronized (userHM.get(passed_uID)) {
			userHM.get(passed_uID).removeShare(passed_stock, passed_shares);
		}
	}
	
	
	
	
	
	/*
	 * 
	 * Stock methods
	 * 
	 */
	
	public void addStock(String passed_stockID){
		
		// add a new stock to the hash map of available stocks
		synchronized (stockHM) {
			stockHM.put(passed_stockID, new stock(passed_stockID));
		}
		
	}
	
	public void removeStock(String passed_stock){

		// get all users subscribed to the stock, and remove the stock from there profile
		String[] usersInAdvertList = stockHM.get(passed_stock).getUsersInAdvertList();
		
		for(int i = 0; i < usersInAdvertList.length; i++){
			removeStockFromUserProfile(usersInAdvertList[i], passed_stock);
		}
		
		//remove the stock from the hash map of available stocks
		synchronized (stockHM) {
			stockHM.remove(passed_stock);
		}
		
	}
	
	public void addUserToStockAdvertList(String passed_uID, String passed_stock_id){
		
		// add user to the stocks advertisement list
		synchronized (stockHM.get(passed_stock_id)) {
			stockHM.get(passed_stock_id).addUserToAdvertList(passed_uID);
		}
	}
	
	public void removeUserFromStockAdvertList(String uID, String passed_stockID){
		
		// remove the user from the stocks advertisement list

		// does the stock exist
		if(stockHM.get(passed_stockID) != null){
			//does the user exist in the advert list
			if(stockHM.get(passed_stockID).isUserInAdvertList(uID) == true){
				synchronized (stockHM.get(passed_stockID)) {
					stockHM.get(passed_stockID).removeUserFromAdvertList(uID);
				}
			}
		}
		

		
	}
	
	public void removeUserFromAllStockAdvertLists(String passed_uID){
		
		
		// remove the users from all stock advertisement lists, that the user is subscribed to 
		String[] userStockList = userHM.get(passed_uID).getUserSubscriptionsToStocks();
		
		for(int i = 0; i < userStockList.length; i++){
			synchronized (stockHM.get(userStockList[i])) {
				stockHM.get(userStockList[i]).removeUserFromAdvertList(passed_uID);
			}
		}
		
	}
	
	public String[] listAllStocks(){
		
		// lists all available stocks
		Set<String> set = stockHM.keySet();
		String[] stocks = (String[]) set.toArray( new String[set.size()]);
		return stocks;
		
	}
	
	public String[] getUsersAdvertisedToStock(String passed_stock){
		
		// list the users subscribed to a stock
		String[] advertUsers = stockHM.get(passed_stock).getUsersInAdvertList();
		return advertUsers;
		
	}
	
	public boolean doesStockExists(String passed_stock){
		
		// check if the stock is in the hash map
		if(stockHM.get(passed_stock) == null){
			// the stock is not in the list
			return false;
		}else{
			//the stock is in the list
			return true;
		}
	}
		
	
	
	
	
	/*
	 * 
	 * Event methods
	 * 
	 */
	
	public void addEvent(event e){
		// add an event tot he hash map
		synchronized (eventHM) {
			eventHM.put(e.getEventID(), e);
		}
	}
	
	public void removeEvent(event e){
		
		//remove an event from the hash map
		synchronized (eventHM) {
			eventHM.remove(e.getEventID());
		}
	}
	
	public event getEvent(long eventID){
		
		// get an event from the hash map 
		return eventHM.get(eventID);
		
	}


}
