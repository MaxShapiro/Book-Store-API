package bgu.spl.mics.application.passiveObjects;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {

	//-------------Fields-------------

	// making a singleton
	private static class SingletonHolder {
		private static MoneyRegister instance = new MoneyRegister();
	}

	private int totalEarnings;
	private List<OrderReceipt> orderReceipts;
	private int orderId;

	//-------------Constructor-------------

	private MoneyRegister(){
		totalEarnings = 0;
		orderReceipts = new LinkedList<>();
		orderId = 0;
	}

	//-------------Methods-------------

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		if (r!=null) {
			if (orderReceipts == null) {
				orderReceipts = new ArrayList<>();
				totalEarnings = 0;
				orderId = 0;
			}
			orderReceipts.add(r);
			totalEarnings = totalEarnings + r.getPrice();
			orderId ++;
		}
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return totalEarnings;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		if (amount>=0 && c.getAvailableCreditAmount()>=amount)
			c.setAvailableCreditAmount(c.getAvailableCreditAmount()-amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		PrintFile p = new PrintFile(filename, orderReceipts);
		p.print();
	}

}
