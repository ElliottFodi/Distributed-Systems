import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class user {
	
	// users ID number
	String id = "";
	
	// users IP address, port is set to a default value for all clients
	String ip = "";

	// the amount of money the user has
	long money = 0;
	
	// list of missed event ID's 
	ArrayList<Long> missedEvents = new ArrayList<Long>();
	
	// HashMap used to keep track of how many shares of a stock a user has 
	HashMap<String, Integer> sharesOfAStockHM = new HashMap<String, Integer>();
	
	public user(String user_id, long passed_money){
		id = user_id;
		money = passed_money;
	}
	
	public void addStock(String passed_stock_id){
		sharesOfAStockHM.put(passed_stock_id, 0);
	}
	
	public void removeStock(String passed_stock){
		sharesOfAStockHM.remove(passed_stock);
	}
	
	public void clearAllStocks(){
		sharesOfAStockHM.clear();
	}
	
	public void addMissedEvent(long missedEventID){
		missedEvents.add(missedEventID);
	}
	
	public void removeMissedEvent(long sentEventID){
		missedEvents.remove(sentEventID);
	}
	
	public void setIP(String passedIP){
		ip = passedIP;
	}
	
	public String getIP(){
		return ip;
	}
	
	public String getUserID(){
		return id;
	}
	
	public String[] getUserSubscriptionsToStocks(){
		Set<String> stocksSet = sharesOfAStockHM.keySet();
		String[] stocks = (String[]) stocksSet.toArray( new String[stocksSet.size()]);
		return stocks;
	}
	
	public ArrayList<Long> getUserMissedEvents(){
		return missedEvents;
	}
	
	public void clearUserMissedEvents(){
		missedEvents.clear();
	}
	
	public boolean isStockInList(String passed_stock){
		
		if(sharesOfAStockHM.get(passed_stock) == null){
			//not in list
			return false;
		}else{
			//in list
			return true;
		}
	}
	
	public void addUserMoney(long passed_money){
		money = money + passed_money;
	}
	
	public void removeUserMoney(Long passed_money){
		money = money - passed_money;
	}
	
	public void addShare(String passed_stock, int passed_shares){
		int currentShares = sharesOfAStockHM.get(passed_stock);
		currentShares = currentShares + passed_shares;
		sharesOfAStockHM.put(passed_stock, currentShares);		
	}
	
	public void removeShare(String passed_stock, int passed_shares){
		int currentShares = sharesOfAStockHM.get(passed_stock);
		currentShares = currentShares - passed_shares;
		sharesOfAStockHM.put(passed_stock, currentShares);
	}
	
	public long getMoney(){
		return money;
	}
	
	public String[] getShares(){
		Set<String> stocksSet = sharesOfAStockHM.keySet();
		String[] stocks = (String[]) stocksSet.toArray( new String[stocksSet.size()]);
		String[] shares = new String[stocks.length];
		for(int i = 0; i < stocks.length; i++){
			shares[i] = stocks[i] + ": " + sharesOfAStockHM.get(stocks[i]);
		}
		return shares;
	}
	
}
