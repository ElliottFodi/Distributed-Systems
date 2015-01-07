
public class event {

	// event id
	long id = -1;
	
	// content of the event
	long sellPrice = -1;
	
	// name of the seller which is also its ID
	String sellerID = "";
	
	// name of the stock which is also its ID
	String stockID = "";
	
	public event(long event_id, long passed_sell_price, String passed_stock_id, String passed_seller_id){
		id = event_id;
		sellPrice = passed_sell_price;
		stockID = passed_stock_id;
		sellerID = passed_seller_id;
	}
	
	public void setEventID(long ID){
		id = ID;
	}
	public long getEventID(){
		return id;
	}
	
	public String getStockID(){
		return stockID;
	}
	
	public long getSellPrice(){
		return sellPrice;
	}
	
	public String getSellerID(){
		return sellerID;
	}
}
