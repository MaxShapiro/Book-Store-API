package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.messages.CarAvailableEvent;
import bgu.spl.mics.application.messages.FinishedVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;


/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private CountDownLatch countDownLatch;
	private ResourcesHolder resourcesHolder;
	private CopyOnWriteArrayList<Future> futurs;

	public ResourceService(String name , CountDownLatch countDownLatch) {
		super(name);
		this.countDownLatch = countDownLatch;
		resourcesHolder = ResourcesHolder.getInstance();
		futurs = new CopyOnWriteArrayList<>();
	}

	@Override
	protected void initialize() {
		//subscribing for TickBroadcast
		subscribeBroadcast(TickBroadcast.class ,(TickBroadcast tickBroadcast)->{
			if(tickBroadcast.getCurrentTime()==tickBroadcast.getDuration()){
				if(!futurs.isEmpty()){
					for(Future f : futurs)
						f.resolve(null);
				}
				terminate();
			}
		});

		subscribeEvent(CarAvailableEvent.class, (CarAvailableEvent cae)->{
			Future<DeliveryVehicle> futureV = resourcesHolder.acquireVehicle();
			futurs.add(futureV);
			complete(cae, futureV);
		});

		subscribeEvent(FinishedVehicleEvent.class, (FinishedVehicleEvent fve)->{
			resourcesHolder.releaseVehicle(fve.getFutureDeliveryVehicle().get());
			futurs.remove(fve);
			complete(fve, null);
		});

		countDownLatch.countDown();
	}
}
