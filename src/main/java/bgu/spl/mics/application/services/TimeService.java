package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Timer;
import java.util.TimerTask;


/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

	private int speed;
	private int duration;
	private int currentTime;



	public TimeService(String name ,int speed, int duration) {
		super(name);
		this.speed=speed;
		this.duration=duration;
		this.currentTime = 1;
	}




	@Override
	protected void initialize() {
		while (currentTime<duration+1) {
			sendBroadcast(new TickBroadcast(currentTime, duration));
			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			currentTime++;
		}
		if (currentTime == duration+1) {
			terminate();
		}
	}


	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getCurrentTime() {
		return currentTime;
	}

}
