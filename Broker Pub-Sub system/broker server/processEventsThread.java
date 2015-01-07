//import java.util.ArrayList;



public class processEventsThread extends Thread {

	repository_of_lists lists;
	active_queue eQueue;
	int port = 2000;
	
	public processEventsThread(repository_of_lists list, active_queue queue){
		lists = list;
		eQueue = queue;
	}
	
	public void run(){
		while(true){
			
			
			// this method will wait until notify is called,
			// it will return with an event to pass to a thread
			event eventFromQueue = eQueue.waitOnQueue();
			//System.out.println("setting up event sender thread");
			
			// start a thread to send out the event to subscribers
			eventSender es = new eventSender(lists, eventFromQueue);
			es.start();		
			
		}
	}
	
	

}
