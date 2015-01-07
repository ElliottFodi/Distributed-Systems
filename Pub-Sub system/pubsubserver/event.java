
public class event {

	// event id
	long id = -1;
	
	// title of the event
	String title = "";
	
	// content of the event
	String content = "";
	
	//topic that the event is linked to 
	String topic_title = "";
	
	public event(long event_id, String event_title, String event_content, String event_topic_title){
		id = event_id;
		title = event_title;
		content = event_content;
		topic_title = event_topic_title;
	}
	
	public void setID(long ID){
		id = ID;
	}
	public long getID(){
		return id;
	}
	
	public String getTopic(){
		return topic_title;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getContent(){
		return content;
	}
}
