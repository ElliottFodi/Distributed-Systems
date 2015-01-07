import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class HandleClientThread extends Thread{
	private int port;
	private repository repo;
	
	public HandleClientThread(int p, repository passed_repo)
	{
		this.port = p;
		repo = passed_repo;
	}
	
	public void run()
	{
		ServerSocket server = null;
		Socket subSocket = null;
		BufferedReader is = null;
		PrintWriter os = null;
		
		try{
			//accept the client connection
			server = new ServerSocket(port);
			subSocket = server.accept();
			
			//debug print out
			System.out.println("CLIENT: xfer connection accepted on port: " + port);
			
			is = new BufferedReader(new InputStreamReader(subSocket.getInputStream()));
			os = new PrintWriter(subSocket.getOutputStream());
			JSONParser parser = new JSONParser();
			JSONObject parserObj = new JSONObject();
			
			//parse into json object
			try{
				parserObj = (JSONObject)parser.parse(is.readLine());
			}catch(ParseException e){
				e.printStackTrace();
			}
                String method = (String)parserObj.get("method");
                
                //debug print out
                System.out.println("CLIENT: method recived: " + method);
                switch(method)
                {
                case "readFile": 
                	readFile(parserObj, os);
                	break;
                case "addFile":
                	addFile(os);
                	break;
                case "deleteFile":
                	deleteFile(parserObj, os);
                	break;
                default:
                	break;
                }
				
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				subSocket.close();
			}catch(Exception e){}
			try{
				server.close();
			}catch(Exception e){}
		}
	}
	public void readFile(JSONObject objects, PrintWriter outPut)
	{
		String fileName = (String)objects.get("fileName");
		
		//debug print out
		System.out.println("CLIENT fileName passed: " + fileName);
		
		String[] csInfo = repo.client_getFile(fileName);
		
		JSONObject res = new JSONObject();
		
		//debug print out
		System.out.println("CLIENT METHOD: readFIle");
		System.out.println("CLIENT CSID: " + csInfo[0]);
		System.out.println("CLIENT METHOD: " + csInfo[1]);
		res.put("method", "readFile");
		res.put("CSID", csInfo[0]);
		res.put("IP", csInfo[1]);
		String ACK = res.toString();
		outPut.println(ACK);
		outPut.flush();
	}
	
	public void addFile(PrintWriter outPut)
	{
		String[] csInfo = repo.client_AddFile();
		
		JSONObject res = new JSONObject();
		JSONArray csList = new JSONArray();
		for(int i = 0; i < csInfo.length; i++){			
			csList.add(csInfo[i]);
			System.out.println("CLIENT addFILE: " + csInfo[i]);
		}
		
		res.put("method", "addFile");
		res.put("csList", csList);
		String ACK = res.toString();
		outPut.println(ACK);
		outPut.flush();
	}
	
	public void deleteFile(JSONObject objects, PrintWriter outPut)
	{
		String deleteFileName = (String)objects.get("deleteFileName");
		System.out.println("CLIENT delete file: " + deleteFileName);
		
		String[] csList = repo.client_deleteFile(deleteFileName);
		JSONArray csDeleteList = new JSONArray();
		for(int i = 0; i < csList.length; i++){			
			csDeleteList.add(csList[i]);
			System.out.println("CLIENT addFILE: " + csList[i]);
		}
		
		JSONObject res = new JSONObject();
		res.put("method", "deleteFile");
		res.put("csList", csDeleteList);
		String ACK = res.toString();
		outPut.println(ACK);
		outPut.flush();
		
	}
}