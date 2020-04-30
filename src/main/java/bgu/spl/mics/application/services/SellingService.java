package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	private MoneyRegister moneyRegister;
	private AtomicInteger currentTime;
	private CountDownLatch countDownLatch;

	public SellingService(String name, CountDownLatch countDown) {
		super(name);
		this.countDownLatch = countDown;
		moneyRegister = MoneyRegister.getInstance();
		currentTime = new AtomicInteger(1);
	}


	@Override
	protected void initialize() {
		//subscribing for timeService
		subscribeBroadcast(TickBroadcast.class, (TickBroadcast tickBroadcast)-> {
			this.currentTime.getAndIncrement();

			if (tickBroadcast.getCurrentTime() == tickBroadcast.getDuration()) {
				terminate();
			}
		});



		subscribeEvent(BookOrderEvent.class, (BookOrderEvent boe) -> {
			AtomicInteger processTick = new AtomicInteger(currentTime.get());
			// reference to the customer
			Customer c = boe.getCustomer();
			//this is sent to inventoryService - should return the price of the book if available, or -1
					synchronized (c){
						int bookPrice = (int) sendEvent(new CheckBookAvailabilityAndPrice(boe.getBook())).get();

						int amountInCreditCard = c.getAvailableCreditAmount();

			if (bookPrice != -1 && bookPrice <= amountInCreditCard) { // I can take the book
				if ((OrderResult) sendEvent(new TakeBookEvent(boe.getBook())).get() == OrderResult.SUCCESSFULLY_TAKEN) { // I actually took the book
					synchronized (moneyRegister) {
						moneyRegister.chargeCreditCard(c, bookPrice);
						AtomicInteger issuedTick = new AtomicInteger(currentTime.get());
						OrderReceipt orderReceipt = new OrderReceipt(this.getName(), boe.getBook(), boe.getOrderId(), c.getId(), bookPrice, issuedTick.get(), boe.getOrderTick(), processTick.get());
						moneyRegister.file(orderReceipt);
						c.getCustomerReceiptList().add(orderReceipt);
						//this method returns an Order Receipt to the API Service
						sendEvent(new DeliveryEvent(c.getDistance(), boe.getCustomer().getAddress()));
						complete(boe, orderReceipt);
					}
				}
			}
			c.getSemaphore().release();
		}});
		countDownLatch.countDown();
	}

}
