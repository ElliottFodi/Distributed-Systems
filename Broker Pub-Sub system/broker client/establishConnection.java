import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * establishes a connection with the server and sets up a data communication 
 * port to transfer command data on
 */

public class establishConnection {

	String IP = "";
	int port = 1989;
	String uID = "";
	Socket xferClientSocket = null;
	BufferedReader xferFromServer;
	DataOutputStream xferToServer;
	
	public establishConnection(String ip, String uid){
		IP = ip;
		uID = uid;
	}
	
	//set up connection with server
	public void connectToServer(){
		
		
		try {
			//connect to server to get a communication port
			Socket clientSocket = new Socket(IP, port);
			BufferedReader dataFromServer = new BufferedReader(new  InputStreamReader(clientSocket.getInputStream()));
			int xferPort = Integer.parseInt( dataFromServer.readLine());
			dataFromServer.close();
			clientSocket.close();
			
			//connect to server via port received
			xferClientSocket = new Socket(IP, xferPort);
			xferFromServer = new BufferedReader(new InputStreamReader(xferClientSocket.getInputStream()));
			xferToServer = new DataOutputStream(xferClientSocket.getOutputStream());
			
			//verify the user
			String data = "";
			xferToServer.writeBytes(uID + "\n");
			data = xferFromServer.readLine();
			if (!data.equals("valid")){
				System.out.println("invalid password");
				return;
			}
			//System.out.println("client verified");
			
			String command = "";
			JSONParser jsonParser = new JSONParser();
			
			// display any missed events
			while(!command.equals("none")){
				JSONObject reply = (JSONObject) jsonParser.parse(xferFromServer.readLine());
				
				command = (String) reply.get("command");
				System.out.println("Command String: " + command);
				
				if(command.equals("event")){
					System.out.println("Event shareID: " + reply.get("eventID"));
					System.out.println("Event price: " + reply.get("sellPrice"));
					System.out.println("Event seller: " + reply.get("sellerID"));
					System.out.println("Event stock name: " + reply.get("stockID"));
				}


			}
			System.out.println("finished accepting missed events");

		} catch (IOException e) {
			System.out.println("connection closed: invalid user");
			//e.printStackTrace();
		} catch (ParseException e) {
			//e.printStackTrace();
		}
	}

	//send command to server and receive JSONObject back
	//used for commands that do not return a list
	public JSONObject sendCommand(JSONObject obj){

		JSONObject replyObj = new JSONObject();
		String request = obj.toJSONString();		
		
		//send command to server and receive reply
		try {
			xferToServer.writeBytes(request + "\n");
			//System.out.println("sent command");
			JSONParser replyParser = new JSONParser();
			replyObj = (JSONObject) replyParser.parse(xferFromServer.readLine());
			//System.out.println("command reply recieved");
			xferClientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return replyObj;
	}
	
	//send command to server and receive JSONArray back
	//used for lists
	public JSONArray sendListCommand(JSONObject obj){
		
		String request = obj.toJSONString();		
		JSONArray listOfTopics = new JSONArray();
		
		//send command to server and receive reply
		try {
			xferToServer.writeBytes(request + "\n");
			JSONParser replyParser = new JSONParser();
			listOfTopics = (JSONArray) replyParser.parse(xferFromServer.readLine());
			xferClientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return listOfTopics;
	}
}
