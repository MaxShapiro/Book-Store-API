package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CarAvailableEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.FinishedVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;


/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	private CountDownLatch countDownLatch;
	private  int tickB;
	private  int duration;
	private int speed;


	public LogisticsService(String name , CountDownLatch countDownLatch, int speed) {
		super(name);
		this.countDownLatch=countDownLatch;
		this.speed = speed;
	}

	@Override
	protected void initialize() {
		//subscribing for TickBroadcast
		subscribeBroadcast(TickBroadcast.class ,(TickBroadcast tickBroadcast)->{
			tickB=tickBroadcast.getCurrentTime();
			duration=tickBroadcast.getDuration();
			if(tickBroadcast.getCurrentTime()==tickBroadcast.getDuration()) {
				terminate();
			}
		});
		//subscribing for deliveryEvent
		subscribeEvent(DeliveryEvent.class , (DeliveryEvent deliveryEvent)->{

			Future<Future<DeliveryVehicle>> futureVehicle = sendEvent(new CarAvailableEvent());
			if (futureVehicle!=null){
				Future<DeliveryVehicle> f = futureVehicle.get();
				if(f!=null){
					DeliveryVehicle vehicle = f.get();
					if (vehicle!=null){
						vehicle.deliver(deliveryEvent.getAddress(),deliveryEvent.getDistance());
						sendEvent(new FinishedVehicleEvent(f));
					}
				}
			}
		});
		countDownLatch.countDown();
	}

}
