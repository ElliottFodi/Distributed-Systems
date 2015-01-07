
import java.util.HashMap;
import java.util.Set;


public class stock {
	
	// title of the topic, also ID's the topic
	String titleOfStock = "";
	
	//key == userID and value is just a boolean
	//Advertisement list 
	HashMap<String,Boolean> usersSubscribedToStock = new HashMap<String,Boolean>();
	
	public stock(String stock_title){
		titleOfStock = stock_title;
	}
	
	public void addUserToAdvertList(String passed_user_id){

		usersSubscribedToStock.put(passed_user_id, true);
	}
	
	public void removeUserFromAdvertList(String passed_user_id){

		usersSubscribedToStock.remove(passed_user_id);
	}
	
	public String[] getUsersInAdvertList(){
		Set<String> set = usersSubscribedToStock.keySet();
		String[] users = (String[]) set.toArray( new String[set.size()]);
		return users;
	}
	
	public boolean isUserInAdvertList(String passed_uID){
		if(usersSubscribedToStock.get(passed_uID) == null){
			// user is not int he list
			return false;
		}else{
			// user is in the list
			return true;
		}
	}
}
