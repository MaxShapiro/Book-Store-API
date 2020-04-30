package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private Customer customer;
	private CountDownLatch countDownLatch;
	private Integer orderId;

	public APIService(Customer customer , String name , CountDownLatch countDownLatch) {
		super(name);
		this.customer = customer;
		this.countDownLatch = countDownLatch;
		orderId = 0;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class,(TickBroadcast message)-> {
			if (message.getCurrentTime() == message.getDuration()) {
				terminate();
			}

			ArrayList<String> output;

			//sending BookOrderEvents in the appropriate Tick
			if (customer.getOrderList().containsKey(message.getCurrentTime())) {
				output = customer.getOrderList().get(message.getCurrentTime());
				for (String s : output) {
					sendEvent(new BookOrderEvent(s, customer, orderId+customer.getId(), message.getCurrentTime()));
					orderId++;
				}
			}
		});

		countDownLatch.countDown();
	}

}