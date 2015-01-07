import java.util.LinkedList;
import java.util.Queue;


public class active_queue {

	long eventID = 0;
	Queue<event> eventQueue = new LinkedList<event>();
	repository_of_lists lists;
	
	public active_queue( repository_of_lists passedLists){
		lists = passedLists;
	}
	
	
	public void addEventToQueue(event e){
		
		// add an event to the queue
		synchronized (eventQueue) {
			
			// add a unique id to the event
			e.setEventID(eventID);
			eventID++;
			eventQueue.add(e);
			
			//wake the blocked thread every time something is added
			eventQueue.notify();
			
		}
		lists.addEvent(e);
	}
	
	public event removeEventFromQueue(){
		
		//remove an event from the queue
		event e;
		synchronized (eventQueue) {
			e = eventQueue.remove();
		}
		return e;
	}
	
	public event waitOnQueue(){
		
		// wait on the queue, when notify is called the waiting thread will wake 
		// up and start emptying the queue 
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
		System.out.println("removed item from q, q size: " + eventQueue.size());

		return e;
	}
}
