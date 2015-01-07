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
 *establishes a connection with the server
 *called every time the user wants to issue a command 
 */

public class establishConnection {

	String IP = "";
	int port = 1990;
	String uID = "";
	Socket clientSocket = null;
	BufferedReader dataFromServer;
	DataOutputStream dataToServer;
	
	public establishConnection(String ip, String uid){
		IP = ip;
		uID = uid;
	}
	
	//set up connection with server
	public void connectToServer(){
		
		
		try {
			//connect to server to get a communication port
			clientSocket = new Socket(IP, port);
			dataFromServer = new BufferedReader(new  InputStreamReader(clientSocket.getInputStream()));
			dataToServer = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("connected to server");

		} catch (IOException e) {
			System.out.println("connection closed: invalid user");
			//e.printStackTrace();
		}
	}

	//send command to server and receive JSONObject back
	//used for commands that do not return a list
	public JSONObject sendCommand(JSONObject obj){

		JSONObject replyObj = new JSONObject();
		String request = obj.toJSONString();		
		//System.out.println("request JSON: " + request);
		
		//send command to server and receive reply
		try {
			dataToServer.writeBytes(request + "\n");
			//System.out.println("sent command");
			JSONParser replyParser = new JSONParser();
			replyObj = (JSONObject) replyParser.parse(dataFromServer.readLine());
			//System.out.println("command reply recieved");
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return replyObj;
	}
	
	//send command to server and receive JSONArray back
	//used for lists
	public JSONArray sendListCommand(JSONObject obj){
		String request = obj.toJSONString();		
		JSONArray replyList = new JSONArray();
		
		//send command to server and receive reply
		try {
			//System.out.println("writing request to server");
			dataToServer.writeBytes(request + "\n");
			JSONParser replyParser = new JSONParser();
			replyList = (JSONArray) replyParser.parse(dataFromServer.readLine());
			clientSocket.close();
			//System.out.println("reply recieved");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return replyList;
	}
}
