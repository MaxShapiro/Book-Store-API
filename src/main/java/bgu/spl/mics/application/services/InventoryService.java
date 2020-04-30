package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.CheckBookAvailabilityAndPrice;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TakeBookEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory inventory;
	private AtomicInteger currentTime;
	private CountDownLatch countDownLatch;



	public InventoryService(String name , CountDownLatch countDownLatch) {
		super(name);
		inventory = Inventory.getInstance();
		currentTime = new AtomicInteger(1);
		this.countDownLatch = countDownLatch;
	}

	@Override
	protected void initialize() {

		subscribeEvent(CheckBookAvailabilityAndPrice.class, (CheckBookAvailabilityAndPrice cbae)->{
			complete(cbae, inventory.checkAvailabiltyAndGetPrice(cbae.getBookTitle()));
		});

		subscribeEvent(TakeBookEvent.class, (TakeBookEvent tbe)->{
			complete(tbe, inventory.take(tbe.getBookTitle()));
		});

		subscribeBroadcast(TickBroadcast.class, (TickBroadcast tickBroadcast)-> {

			this.currentTime.getAndIncrement();
			if (tickBroadcast.getCurrentTime() == tickBroadcast.getDuration()){
				terminate();
			}
		});
		countDownLatch.countDown();
	}

}
