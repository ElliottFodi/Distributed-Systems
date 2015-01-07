

import java.util.Scanner;



// compile and run instructions
// javac -cp C:\Users\Spufflez\Libraries\JSON\json.jar *.java
// java -cp C:\Users\Spufflez\Libraries\JSON\json.jar;. main_thread

public class main_thread {

	public static void main(String[] args) {
		
		// block on console input 
		Scanner scanner = new Scanner(System.in);		
		String console_input = "";
		
		//loop until the start command is given
		while(!console_input.equals("start")){
			System.out.println("Use Start command to start system:");
			console_input = scanner.nextLine();
		}
		
		
		//initialize lists and queue
		repository_of_lists lists = new repository_of_lists();
		active_queue eventQueue = new active_queue(lists);
		
		//port the server will listen on
		int port = 1989;
		
		// spawn the listener thread to listen for incoming client connections
		listenForClientsThread listenerThread = new listenForClientsThread(port, lists, eventQueue);
		listenerThread.start();
		
		// spawn the process events thread to process the events in the queue
		processEventsThread processThread = new processEventsThread(lists, eventQueue);
		processThread.start();
		
		// spawn thread to listen for admin client
		listenForAdmin adminThread = new listenForAdmin(lists, eventQueue);
		adminThread.start();
		
		// initialize admin commands
		localAdminCommands admin = new localAdminCommands(lists, scanner, eventQueue);
		
		//boolean to stop loop
		boolean stopSystem = false;
		
		//loop over commands 
		while(stopSystem == false){
			
			System.out.println("Enter a command");
			console_input = scanner.nextLine();
			switch(console_input){
				case "addUser":
					admin.adminAddUser();
					break;
					
				case "addStock":
					admin.adminAddStock();
					break;
				
				case "removeUser":
					admin.adminRemoveUser();
					break;
				
				case "removeStock":
					admin.adminRemoveStock();
					break;
					
				case "listUsers":
					admin.adminListUsers();
					break;
					
				case "listStocks":
					admin.adminListStocks();
					break;
					
				case "notify":
					System.out.println(console_input);
					break;
					
				case "sell":
					admin.adminSell();
					break;
					
				case "buy":
					admin.adminBuy();
					break;
					
				case "listMyStocks":
					admin.adminListMyStocks();
					break;
					
				case "subscribe":
					admin.adminSubscribe();
					break;
					
				case "unsubscribe":
					admin.adminUnsubscribeToStock();
					break;
					
				case "unsubscribeAll":
					admin.adminUnsubscribeToAllStocks();
					break;
				
				case "myMoney":
					admin.adminGetMoney();
					break;
					
				case "addMoney":
					admin.adminSetMoney();
					break;
					
				case "addShares":
					admin.addShares();
					break;
					
				case "removeShares":
					admin.removeShares();
					break;
					
				case "myShares":
					admin.myShares();
					break;
					
				case "start":
					System.out.println("the server is already running");
					break;
					
				case "stop system":
					System.out.println("Stopping Server");
					stopSystem = true;
					break;
					
				default:
					System.out.println("Invalid command please enter a valid command " + console_input);
					
			} //switch end
			
		} //while loop

		//kill the program
		System.exit(1);
	} //main

} //class
