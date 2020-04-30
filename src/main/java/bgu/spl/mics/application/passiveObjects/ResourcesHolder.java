package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	//-------------Fields-------------

	private ConcurrentLinkedQueue<DeliveryVehicle> deliveryVehicles;
	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> futures;
	private Semaphore deliveryVehiclesSemaphore; //initializing in load function

	//-------------Constructor--------------
	private ResourcesHolder(){
		deliveryVehicles = new ConcurrentLinkedQueue<>();
		futures = new ConcurrentLinkedQueue<>();
	}

	// making a singleton
	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	//-------------Methods-------------

	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {

		Future<DeliveryVehicle> future = new Future<>();
		//trying to acquire vehicle from the deliveryVehiclesList
		if(deliveryVehiclesSemaphore.tryAcquire()) {
			DeliveryVehicle deliveryVehicle = deliveryVehicles.poll();
			future.resolve(deliveryVehicle);
		}
		else {
			futures.add(future);
		}
		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if (vehicle!=null) {
			if(!futures.isEmpty()){
				Future<DeliveryVehicle> future;
				future = futures.poll();
				future.resolve(vehicle);
			}
			else {
				deliveryVehicles.add(vehicle);
				deliveryVehiclesSemaphore.release();
			}
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		if (vehicles!=null){
			for (DeliveryVehicle vehicle: vehicles)
				deliveryVehicles.add(vehicle);
			deliveryVehiclesSemaphore = new Semaphore(vehicles.length);
		}
	}

}
