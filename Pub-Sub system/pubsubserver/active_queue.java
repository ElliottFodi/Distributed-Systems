import java.util.LinkedList;
import java.util.Queue;

/*
 * queue that handles events, the queue is synchronized on so that events to not get 
 * corrupted due to multiple threads interacting with the queue
 */

public class active_queue {

	long eventID = 0;
	Queue<event> eventQueue = new LinkedList<event>();
	repository_of_lists lists;
	
	public active_queue( repository_of_lists passedLists){
		lists = passedLists;
	}
	
	
	public void addEventToQueue(event e){
		
		synchronized (eventQueue) {
			
			//give the event a unique id number
			e.setID(eventID);
			eventID++;
			eventQueue.add(e);
			
			//wake the blocked thread every time something is added
			eventQueue.notify();
			
		}
		lists.addEvent(e);
	}
	
	//remove the event from the queue
	public event removeEventFromQueue(){
		event e;
		synchronized (eventQueue) {
			e = eventQueue.remove();
		}
		return e;
	}
	
	//wait on the queue for notify to be called
	public event waitOnQueue(){
		synchronized (eventQueue) {
			
			while(eventQueue.size() == 0){
				try {
					eventQueue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		//System.out.println("removing item from q, q size: " + eventQueue.size());
		event e = removeEventFromQueue();
		//System.out.println("removed item from q, q size: " + eventQueue.size());

		return e;
	}
}
