//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

/*
 * pull events from the queue and spawn a thread to send out the events
 */

public class processEventsThread extends Thread {

	// event Queue class to access the queue
	repository_of_lists lists;
	active_queue eQueue;
	
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
			eventSender es = new eventSender(lists, eventFromQueue);
			es.start();		
			
		}
	}
	
	

}
