package bgu.spl.mics;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

public class MessageBusImpl implements MessageBus {

	//-------------Fields-------------
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceQ;
	private ConcurrentHashMap<Event, Future> futureToEvent;
	private ConcurrentHashMap<Class<? extends Message>, LinkedList<MicroService>> roundEventRob;
	private ConcurrentHashMap<Class<? extends Message>, LinkedList<MicroService>> roundBroadcastRob;
	private final Object key;

	// making a singleton
	private static class SingletonHolder {
		private static MessageBusImpl messageBus = new MessageBusImpl();
	}

	//-------------Constructor-------------
	private MessageBusImpl() {
		microServiceQ = new ConcurrentHashMap<>();
		futureToEvent = new ConcurrentHashMap<>();
		roundEventRob = new ConcurrentHashMap<>();
		roundBroadcastRob = new ConcurrentHashMap<>();
		key = new Object();
	}

	//-------------Methods-------------
	public static MessageBusImpl getInstance() {
		return SingletonHolder.messageBus;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subscribe(roundEventRob, type, m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subscribe(roundBroadcastRob, type, m);
	}

	private void subscribe(ConcurrentHashMap<Class<? extends Message>, LinkedList<MicroService>> roundRob, Class<? extends Message> type, MicroService m) {
		synchronized (key) {
			if (type != null && m != null) {
				if (roundRob.get(type) == null) {
					roundRob.put(type, new LinkedList<>());
					roundRob.get(type).add(m);
				} else {
					if (!roundRob.get(type).contains(m))
						roundRob.get(type).add(m);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void complete(Event<T> e, T result) {
		if (e != null && result != null) {
			futureToEvent.get(e).resolve(result);
			futureToEvent.remove(e);
		}
	}


	@Override
	public void sendBroadcast(Broadcast b) {
		if (b != null && roundBroadcastRob.get(b.getClass()) != null && roundBroadcastRob.get(b.getClass()) != null && microServiceQ.size() != 0) {
			for (MicroService m : roundBroadcastRob.get(b.getClass()))
				if (microServiceQ.get(m) == null) {
					microServiceQ.put(m, new LinkedBlockingQueue<>());
					microServiceQ.get(m).add(b);
				} else
					microServiceQ.get(m).add(b);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future f = new Future();
		//adding this future to be connected to this event
		futureToEvent.put(e, f);
		//switching the first micro service to be the last - making a round robin work pattern
		MicroService m;
		synchronized (key) {
			m = roundEventRob.get(e.getClass()).pollFirst();
			roundEventRob.get(e.getClass()).addLast(m);
			if (m != null && microServiceQ.get(m) != null) {
				//adding the event to the queue of the micro service
				microServiceQ.get(m).add(e);
			} else {
				f.resolve(null);
				futureToEvent.remove(f);
			}
		}
		return f;
	}

	@Override
	public void register(MicroService m) {
		if (m != null) {
			BlockingQueue q = new LinkedBlockingQueue<Message>();
			microServiceQ.put(m, q);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public void unregister(MicroService m) {
		synchronized (key) {
			if (m != null && roundEventRob != null) {
				unreg(m, roundEventRob);
			}
			if (m != null && roundBroadcastRob != null) {
				unreg(m, roundBroadcastRob);
			}
			if (microServiceQ.get(m) != null) {
				for (Message message : microServiceQ.get(m)) {
					if (message instanceof Event)
						complete((Event) message, null);
				}
				microServiceQ.remove(m);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void unreg(MicroService m, ConcurrentHashMap<Class<? extends Message>, LinkedList<MicroService>> roundRob) {
		for (Class<? extends Message> key : roundRob.keySet()) {
			if (roundRob.get(key) != null) {
				if (roundRob.get(key).isEmpty()) {
					roundRob.get(key).remove(m);
					roundRob.remove(key);
				}
			}
			for (Future f : futureToEvent.values()) {
				f.resolve(null);
			}
			for (Message message : futureToEvent.keySet()) {
				complete((Event) message, null);
			}

		}
	}


	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return microServiceQ.get(m).take();
	}

}