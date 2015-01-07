import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class HandleCSThread extends Thread{
	//private int port;
	Socket socket;
    private repository repo;

	public HandleCSThread(Socket passed_socket, repository passed_repo)
	{
		//this.port= p;
		socket = passed_socket;
		repo = passed_repo;
	}
	
	public void run()
	{
		//ServerSocket server = null;
		//Socket subSocket = null;
		BufferedReader is = null;
		PrintWriter os = null;
		try{
			//opening port
			//server = new ServerSocket(port);
			
			//accepting connection
			//subSocket = server.accept();
			
			//debug print out
			//System.out.println("Xfer connection establised: " + socket.getInetAddress().toString());
			
			//create reader and writer
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = new PrintWriter(socket.getOutputStream());
			
			//create JSON parser and object
			JSONParser parser = new JSONParser();
			JSONObject parserObj = null;
			
			try{
				//get the send JSONObject from the cs
				//System.out.println("parsing msg");
				parserObj = (JSONObject)parser.parse(is.readLine());
				//System.out.println("parsed msg");
			}catch(ParseException e){
				e.printStackTrace();
			}
				//get the method invoked 
                String method = (String)parserObj.get("method");
                
                //debug print
                //System.out.println("Method recieved: " + method);
                switch(method)
                {
                case "fileAdded": 
                	fileAdded(parserObj,os);
                	break;
                case "failedToCopy":
                	failedToCopy(parserObj,os);
                	break;
                case "table":
                	tableUpdate(parserObj,os);
                	break;
                case "fileHidden":
                	fileHidden(parserObj,os);
                	break;
                case "heartBeat":
                	heartBeat(parserObj,os);
                	break;
                case "fileDeleted":
                	fileDeleted(parserObj,os);
                	break;
                case "newCS":
                	newCS(parserObj,os);
                	break;
                case "shutDownCS":
                	shutDownCS(parserObj,os);
                	break;
                default:
                	//invalid input
                	System.out.println("default method hit!");
			System.out.println("method recieved: " + method);
                	break;
                }
				
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				socket.close();
			}catch(Exception e){}
			try{
				//server.close();
			}catch(Exception e){}
			try{
				is.close();
			}catch(Exception e){}
			try{
				os.close();
			}catch(Exception e){}
		}
	}
	
	public void fileAdded(JSONObject jobj,PrintWriter outPut)
	{
		
		//parse JSON
		String fileName = (String) jobj.get("fileName").toString();
		int CSID = (int)Integer.parseInt(jobj.get("CSID").toString());

		//debug print out
		System.out.println("file added: " + CSID + " Name: " + fileName);
		
		//add the file
		repo.addFile(CSID, fileName);
		setContactTime(CSID);
		
		//create response
		JSONObject res = new JSONObject();
		res.put("result", true);
		String ACK = res.toString();
		outPut.println(ACK);
		outPut.flush();
	}
	
	public void failedToCopy(JSONObject jobj,PrintWriter outPut)
	{
		//parse JSON
		int CSID = (int)Integer.parseInt(jobj.get("CSID").toString());
		int failedCS = (int)Integer.parseInt(jobj.get("failedCS").toString());
		JSONArray array = (JSONArray)jobj.get("listOfCS");
		int[] listOfCS = new int[array.size()];
		String fileName = (String) jobj.get("fileName");
		
		//debug print out
		System.out.println("Failed CS: " + failedCS);
		for(int i = 0; i < array.size(); i++){
			System.out.println("file list: " + array.get(i));
		}
		
		
		//place contents of JSONArray into int[]
		for(int i=0;i<array.size();i++)
		{
			int temp = (int)Integer.parseInt(array.get(i).toString());
			listOfCS[i]=temp;
		}
		
		String[] newInfo = repo.getNewIPForFailedCS( failedCS, listOfCS);
		setContactTime(CSID);

		System.out.println("sending new ip");		

		String newCS = newInfo[0];
		String newIP = newInfo[1];
		JSONObject res = new JSONObject();
		res.put("newCSID", newCS);
		res.put("newIP", newIP);
		res.put("fileName", fileName);
		String ACK = res.toString();
		outPut.println(ACK);
		outPut.flush();
		
	}
	public void tableUpdate(JSONObject jobj,PrintWriter outPut)
	{
		//parse JOSON
		int CSID = (int)Integer.parseInt(jobj.get("CSID").toString());
		
		//debug print out
		System.out.println("Table Update recieved for CS: " + CSID);
		JSONArray array = (JSONArray)jobj.get("fileList");
		LinkedList<String> fileList = new LinkedList<String>();
		
		//place files from JSONArray into String[]
		for(int i=0;i<array.size();i++)
		{
			String temp = array.get(i).toString();
			System.out.println("file recived: " + temp);
			fileList.add(temp);
		}
		
		repo.compareTables(CSID, fileList);
		setContactTime(CSID);

		
		JSONObject res = new JSONObject();
		res.put("result", true);
		String ACK = res.toString();
		outPut.println(ACK);
		outPut.flush();
	}
	
	public void fileHidden(JSONObject jobj,PrintWriter outPut)
	{
		System.out.println("fileHidden method called");
		//parse JSON
		int CSID = (int)Integer.parseInt(jobj.get("CSID").toString());
		String oldFileName = jobj.get("oldFileName").toString();
		String newFileName = jobj.get("newFileName").toString();
		
		//debug print out
		System.out.println("old file name: " + oldFileName);
		System.out.println("new file name: " + newFileName);
		
		repo.renameFileForACS(CSID, oldFileName, newFileName);
		setContactTime(CSID);
		
		JSONObject res = new JSONObject();
		res.put("result", true);
		String ACK = res.toString();
		outPut.println(ACK);
		outPut.flush();
	}
	
	public void heartBeat(JSONObject jobj,PrintWriter outPut)
	{
		int CSID = (int)Integer.parseInt(jobj.get("CSID").toString());
		
		String[] delete = repo.heartBeatGetDeleteFiles(CSID);
		String[] move = repo.heartBeatGetMoveFiles(CSID);
		setContactTime(CSID);
		
		JSONArray del = new JSONArray();
		JSONArray mov = new JSONArray();
		
		if(delete.length != 0){
			for(int i = 0; i < delete.length; i++){
				del.add(delete[i]);
				System.out.println("delete file name to be sent: " + delete[i]);
			}
		}
		
		//TODO get files and IP address from hash map
		if(move.length != 0){
			for(int i = 0; i < move.length; i++){
				System.out.println("chunk server: " + CSID + "move file");
				mov.add(move[i]);
				System.out.println("move file name to be sent: " + move[i]);
			}
		}
		
		
		//function
		JSONObject res = new JSONObject();
		res.put("result", true);
		res.put("filesToBeDeleted", del);
		res.put("filesToBeMoved", mov);
		String ACK = res.toString();
		outPut.println(ACK);
		outPut.flush();
	}
	
	public void fileDeleted(JSONObject jobj,PrintWriter outPut)
	{
		//parse json
		int CSID = (int)Integer.parseInt(jobj.get("CSID").toString());
		String deleteFileName = jobj.get("fileName").toString();
		
		//debug print out
		System.out.println("delete file name: " + deleteFileName);
		
		repo.delteAFileFromACS(CSID, deleteFileName);
		setContactTime(CSID);
		
		JSONObject res = new JSONObject();
		res.put("result", true);
		String ACK = res.toString();
		outPut.println(ACK);
		outPut.flush();
	}
	
	public void newCS(JSONObject jobj, PrintWriter outPut)
	{
		//debug print out
		System.out.println("newCS method called");
		
		String IP = jobj.get("IP").toString();
		
		//debug print out
		System.out.println("passed IP: " + IP);
		
		int port = 8888;
		JSONArray array = (JSONArray)jobj.get("fileList");
		LinkedList<String> fList = new LinkedList<String>();
		for(int i=0;i<array.size();i++)
		{
			String temp = array.get(i).toString();
			fList.add(temp);
		}
		
		int assignedID = repo.addChunkServer(IP, fList, port);
		setContactTime(assignedID);
		
		JSONObject res = new JSONObject();
		res.put("CSID", assignedID);
		String ACK = res.toString();
		System.out.println("result: " + ACK);
		outPut.println(ACK);
		outPut.flush();
	}
	
	public void shutDownCS(JSONObject jobj, PrintWriter outPut)
	{
		int CSID = (int)Integer.parseInt(jobj.get("CSID").toString());
		
		repo.removeChunkServer(CSID);
		
		JSONObject res = new JSONObject();
		res.put("result", true);
		String ACK = res.toString();
		outPut.println(ACK);
		outPut.flush();
	}
	
	public void setContactTime(int passed_CSID){
		long contactTime = System.currentTimeMillis();
		repo.setLastContactListForACS(passed_CSID, contactTime);
	}

}
