import java.util.Scanner;


/*
 * This class is for local commands to the server, as these commands need to display 
 * data to the servers console and prompt the user on the server. 
 */

public class localAdminCommands {
	
	repository_of_lists lists;
	Scanner scanner;
	active_queue eventQueue;
	
	public localAdminCommands(repository_of_lists list, Scanner scan_in, active_queue queue){
		lists = list;
		scanner = scan_in;
		eventQueue = queue;
	}

	//add a stock for users to subscribe to
	public void adminAddStock(){
		System.out.println("Enter stock title: ");
		String stockID = scanner.nextLine(); 
		
		lists.addStock(stockID);
		System.out.println("Topic: " + stockID + " added");
	}
	
	//add a new user to the system
	public void adminAddUser(){
		System.out.println("Enter user name: ");
		String userName = scanner.nextLine(); 

		System.out.println("Enter money: ");
		//long money = scanner.nextLong(); 
		long money = Long.parseLong(scanner.nextLine());
		
		lists.addUser(userName, money);
		System.out.println("User: " + userName + " added");
	}
	
	
	//remove a user from the system
	public void adminRemoveUser(){
		System.out.println("Enter user name: ");
		String uID = scanner.nextLine(); 

		lists.removeUser(uID);
		System.out.println("User: " + uID + " removed");
	}
	
	//remove a stock from the entire system
	public void adminRemoveStock(){
		System.out.println("Enter the stock title");
		String stockID = scanner.nextLine();
		
		lists.removeStock(stockID);
		System.out.println("Stock: " + stockID + " removed");
	}
	
	//place holder for future command
	public void adminNotify(){
		// Possibly add publish commands here
	}
	
	//list all the users in the system
	public void adminListUsers(){
		String[] users = lists.listUsers();
		
		for (int i = 0; i < users.length; i++){
			System.out.println("User: " + users[i]);
		}
	}
	
	//list all the stocks available to all users
	public void adminListStocks(){
		
		String[] stocks = lists.listAllStocks();
		
		for (int i = 0; i < stocks.length; i++){
			System.out.println("Stock: " + stocks[i]);
		}
	}
	
	//sell a stock for a user
	public void adminSell(){
		
		long eventID = -1;
		System.out.println("Enter stock name");
		String adminStockID = scanner.nextLine();
		
		System.out.println("Enter price");
		long adminSellPrice = Long.parseLong(scanner.nextLine());
		System.out.println("Enter seller ID");
		String adminSeller = scanner.nextLine();
		event adminEvent = new event(eventID, adminSellPrice, adminStockID, adminSeller);
		eventQueue.addEventToQueue(adminEvent);
	}
	
	//buy a stock for a user
	public void adminBuy(){
		
		System.out.println("Enter buyer name");
		String uID = scanner.nextLine();
		System.out.println("Enter shareID");
		long eventID = Long.parseLong(scanner.nextLine());
		
		boolean success = lists.userBuyShare(uID, eventID);
		
		if(success == true){
			System.out.println("Bought the share");
		}else{
			System.out.println("you were to slow");
		}
		
	}
	
	//subscribe to a stock for a user
	public void adminSubscribe(){
		System.out.println("Enter User ID");
		String uID = scanner.nextLine();
		System.out.println("Enter stock");
		String stock = scanner.nextLine();
		
		lists.addUserToStockAdvertList(uID, stock);
		lists.addStockToUserProfile(uID, stock);
	}
	
	//unsubscribe to a stock for a user
	public void adminUnsubscribeToStock(){
		System.out.println("Enter User ID");
		String uID = scanner.nextLine();
		System.out.println("Enter stock");
		String stock = scanner.nextLine();
		
		lists.removeUserFromStockAdvertList(uID, stock);
	}
	
	//unsubscribe to all stocks for a user
	public void adminUnsubscribeToAllStocks(){
		System.out.println("Enter User ID");
		String uID = scanner.nextLine();
		
		lists.removeUserFromAllStockAdvertLists(uID);
	}
	
	//list all stocks for a user
	public void adminListMyStocks(){
		System.out.println("Enter User ID");
		String uID = scanner.nextLine();
		String[] userStocks = lists.listAllUserStocks(uID);
		
		for(int i = 0; i < userStocks.length; i++){
			System.out.println("My Stock: " + userStocks[i]);
		}
	}
	
	//list the money a user has
	public void adminGetMoney(){
		System.out.println("Enter user name");
		String uID = scanner.nextLine();
		System.out.println("User's money: " + lists.userGetMoney(uID));
	}
	
	//set the monsy for a user
	public void adminSetMoney(){
		System.out.println("Enter user name");
		String uID = scanner.nextLine();
		System.out.println("Enter amount to add to account");
		long amount = Long.parseLong(scanner.nextLine());
		
		lists.userAddMoney(uID, amount);
	}
	
	//list the shares for a user
	public void myShares(){
		System.out.println("Enter user name");
		String uID = scanner.nextLine();
		String[] shares = lists.userGetShares(uID);
		
		for(int i = 0; i < shares.length; i++){
			System.out.println("Share: " + shares[i]);
		}
	}
	
	//add shares to a user's account
	public void addShares(){
		System.out.println("Enter user name");
		String uID = scanner.nextLine();
		System.out.println("Enter stock name");
		String stock = scanner.nextLine();
		System.out.println("Enter shares to be added");
		int shares = Integer.parseInt(scanner.nextLine());
		
		lists.userAddShares(uID, stock, shares);
	}
	
	//remove shares from a users account
	public void removeShares(){
		System.out.println("Enter user name");
		String uID = scanner.nextLine();
		System.out.println("Enter stock name");
		String stock = scanner.nextLine();
		System.out.println("Enter shares to be removed");
		int shares = Integer.parseInt(scanner.nextLine());
		
		lists.userRemoveShares(uID, stock, shares);
	}
}
