package bgu.spl.mics.application.passiveObjects;


import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static bgu.spl.mics.application.passiveObjects.OrderResult.NOT_IN_STOCK;
import static bgu.spl.mics.application.passiveObjects.OrderResult.SUCCESSFULLY_TAKEN;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

	//-------------Fields-------------

	// the actual inventory date structure

	private ConcurrentHashMap<String,BookInventoryInfo> inventoryMap; //key: book name, Value: the actual book class
	//output serialized map
	private HashMap<String,Integer> outputMap;



	//-------------Constructor-------------
	// making a singleton
	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}


	//-------------Methods-------------

	/**
     * Retrieves the single instance of this class.
	 */
	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		inventoryMap = new ConcurrentHashMap<>();
		outputMap = new HashMap<>();
		//adding the books to the data structure
		if (inventory!=null) {
			for (BookInventoryInfo book : inventory) {
				inventoryMap.put(book.getBookTitle(), book);
				outputMap.put(book.getBookTitle(), book.getAmountInInventory());
			}
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */

	//
	public OrderResult take (String book) {
		int oldAmount;
		int newAmount;
		if (checkBookExistsInventory(book)) {
			if (inventoryMap.get(book).getSemaphore().tryAcquire()) {
				oldAmount = inventoryMap.get(book).getAmountInInventory();
				newAmount = oldAmount - 1;
				if (oldAmount != 0) {
					inventoryMap.get(book).setAmountInInventory(newAmount);
					outputMap.replace(book, oldAmount, newAmount);
					return SUCCESSFULLY_TAKEN;
				}
			}
		}
		return NOT_IN_STOCK;
	}


	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		int amount ;
		if (book!=""){
			if (checkBookExistsInventory(book)) {
				amount = inventoryMap.get(book).getAmountInInventory();
				if (amount > 0) {
					return inventoryMap.get(book).getPrice();
				}
			}
		}
		return -1;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     * @post the file exists
     */
	public void printInventoryToFile(String filename){
		PrintFile p = new PrintFile(filename, outputMap);
		p.print();
	}

	private boolean checkBookExistsInventory(String book){
		boolean output = false;
		if (inventoryMap.get(book)!=null)
			output = true;
		return output;
	}

}
