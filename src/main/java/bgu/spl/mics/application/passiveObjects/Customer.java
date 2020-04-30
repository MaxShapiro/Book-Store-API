package bgu.spl.mics.application.passiveObjects;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	//-------------Fields-------------
	private String name;
	private int id;
	private String address;
	private int distance;
	private int creditNumber;
	private int availableCreditAmount;
	private List<OrderReceipt> customerReceiptList;
	//A semaphore is needed so that only one Selling Service would be able to talk to the costumer
	private Semaphore semaphore;
	private  ConcurrentHashMap<Integer , ArrayList<String>> orderList;
	private OrderPair[] orderSchedule;
	private LinkedList<OrderPair> orderPairLinkedList;
	private CreditCard creditCard;


	//-------------Constructor-------------
	public Customer(String name, int id, String address, int distance,
					int creditNumber, int availableCreditAmount, OrderPair[] orderSchedule ){
		this.name = name;
		this.id = id;
		this.address = address;
		this.distance = distance;
		this.creditNumber = creditNumber;
		this.availableCreditAmount = availableCreditAmount;
		this.orderSchedule = orderSchedule;
		this.customerReceiptList = new ArrayList<>();
		this.orderList = new ConcurrentHashMap<>();
		this.semaphore = new Semaphore(1);
		this.orderPairLinkedList = new LinkedList<>();
	}

	//-------------Methods-------------
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return customerReceiptList;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return availableCreditAmount;
	}

	public void setAvailableCreditAmount(int newAmountInCreditCard) {
		this.availableCreditAmount = newAmountInCreditCard;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditNumber;
	}

	public ConcurrentHashMap<Integer , ArrayList<String>>  getOrderList() {
		return orderList;
	}

	public Semaphore getSemaphore() {
		return semaphore;
	}

	public void setCustomerService() {
		customerReceiptList = new ArrayList<>();
		orderList = new ConcurrentHashMap<>();
		semaphore = new Semaphore(1);

//		pushing the elements from orderSchedule to the orderList
		for (OrderPair op : orderSchedule) {
			//if Tick exist
			if (this.orderList.containsKey(op.getTick())) {
				orderList.get(op.getTick()).add(op.getBookTitle());
			} else {
					orderList.put(op.getTick(), new ArrayList<>());
					orderList.get(op.getTick()).add(op.getBookTitle());
			}
		}
	}
	public void setCustomer(){
		semaphore = new Semaphore(1);
		this.creditNumber = creditCard.getNumber();
		this.availableCreditAmount = creditCard.getAmount();
	}

}


