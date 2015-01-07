//import java.util.ArrayList;

import java.util.Scanner;


// compile and run instructions
// javac -cp C:\Users\Spufflez\Libraries\JSON\json.jar *.java
// java -cp C:\Users\Spufflez\Libraries\JSON\json.jar;. main_thread 1989

public class main_thread {

	public static void main(String[] args) {
		
		// block on console input 
		Scanner scanner = new Scanner(System.in);		
		String console_input = "";
		
		
		
		while(!console_input.equals("start")){
			System.out.println("Use the Start command to start system:");
			console_input = scanner.nextLine();
		}
		
		
		//initialize lists and queue
		repository_of_lists lists = new repository_of_lists();
		active_queue eventQueue = new active_queue(lists);
		
		
		int port = 1989;
		
		// spawn the listener thread to listen for incoming client connections
		listenForClientsThread listenerThread = new listenForClientsThread(port, lists, eventQueue);
		listenerThread.start();
		
		// spawn the process events thread to process the events in the queue
		processEventsThread processThread = new processEventsThread(lists, eventQueue);
		processThread.start();
		
		
		// initialize admin commands
		localAdminCommands admin = new localAdminCommands(lists, scanner, eventQueue);
		
		boolean stopSystem = false;
		
		//commands a user on the server can utilize
		while(stopSystem == false){
			
			System.out.println("Enter a command");
			console_input = scanner.nextLine();
			switch(console_input){
				case "addUser":
					admin.adminAddUser();
					break;
					
				case "addTopic":
					admin.adminAddTopic();
					break;
				
				case "removeUser":
					admin.adminRemoveUser();
					break;
				
				case "removeTopic":
					admin.adminRemoveTopic();
					break;
					
				case "listUsers":
					admin.adminListUsers();
					break;
					
				case "listTopics":
					admin.adminListTopics();
					break;
					
				case "notify":
					System.out.println(console_input);
					break;
					
				case "publish":
					admin.adminPublish();
					break;
					
				case "advertise":
					admin.adminPublish();
					break;
					
				case "listMyTopics":
					admin.adminListMyTopics();
					break;
					
				case "subscribe":
					admin.adminSubscribe();
					break;
					
				case "unsubscribe":
					admin.adminUnsubscribeToTopic();
					break;
					
				case "unsubscribeAll":
					admin.adminUnsubscribeToAllTopics();
					break;
					
				case "start":
					//spawn threads here
					//if running don't spawn more threads
					break;
					
				case "stop system":
					// send message closing connection
					System.out.println("Stopping Server");
					stopSystem = true;
					break;
					
				default:
					System.out.println("Invalid command please enter a valid command");
					
			} //switch end
			
		} //while loop

		System.exit(1);
	} //main

} //class
