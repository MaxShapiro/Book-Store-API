package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

	//-------------Fields-------------
	private final String bookTitle;
	private final int price;
	private Semaphore semaphore;
	private AtomicInteger amount;



	//-------------Constructor-------------
	public BookInventoryInfo(String bookTitle, int amountInInventory, int price){
		this.amount = new AtomicInteger();
		this.bookTitle = bookTitle;
		this.amount.set(amountInInventory);
		this.price = price;
		this.semaphore = new Semaphore(amountInInventory);
	}


	//-------------Methods-------------
	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.
     */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.
     */
	public int getAmountInInventory() {
		return amount.get();
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return price;
	}

	public void setAmountInInventory(int amountInInventory) {
		this.amount.set(amountInInventory);
	}

	public Semaphore getSemaphore() {
		return semaphore;
	}

	public void setBook(){
		semaphore = new Semaphore(amount.get());
	}

}
