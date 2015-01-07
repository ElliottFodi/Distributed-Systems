
import java.util.Scanner;


// takes the IP address of the server as an argument
public class main_thread {

	public static void main(String[] args) {
		
		String IPaddress = args[0];
		
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter User Name to Log in");
		String uID = scanner.nextLine();
		
		//initialize commands and connection classes
		establishConnection connection = new establishConnection(IPaddress, uID);
		userCommands commands = new userCommands(scanner, connection, uID);
		
		boolean exit = false;
		
		//commands for the client admin to utilize
		while(exit != true){
		System.out.println("Enter a command");
		String input = scanner.nextLine();
			switch(input){
			
				case "addUser":
					commands.addUser();
					break;
					
				case "addStock":
					commands.addStock();
					break;
					
				case "removeUser":
					commands.removeUser();
					break;
					
				case "removeStock":
					commands.removeStock();
					break;
					
				case "listUsers":
					commands.listUsers();
					break;
					
				case "sell":
					commands.sell();
					break;
					
				case "buy":
					commands.buy();
					break;
					
				case "subscribe":
					commands.subscribe();
					break;
					
				case "unsubscribe":
					commands.unsubscribe();
					break;
					
				case "unsubscribeAll":
					commands.unscubscribeAll();
					break;
					
				case "listStocks":
					commands.listStocks();
					break;
					
				case "listMyStocks":
					commands.listMyStocks();
					break;
					
				case "myMoney":
					commands.getMoney();
					break;
					
				case "addMoney":
					commands.addMoney();
					break;
					
				case "newUserID":
					commands.retypeUserID();
					break;
					
				case "addShares":
					commands.addShares();
					break;
					
				case "removeShares":
					commands.removeShares();
					break;
					
				case "myShares":
					commands.myShares();
					break;
					
				case "close":
					exit = true;
					break;
					
				default:
					System.out.println("Invalid command, please enter a valid command");
			}
		}
		
		System.out.println("System terminating");
		System.exit(1);
	}

}
