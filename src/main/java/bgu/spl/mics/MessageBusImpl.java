package bgu.spl.mics;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Model;

import javax.swing.plaf.metal.MetalIconFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private final ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> subscriptionLists;
	private final ConcurrentHashMap<MicroService, ConcurrentLinkedQueue<Message>> microServiceQueues;
	private final ConcurrentHashMap<Event,Future> futures;
	private static MessageBusImpl instance = null;

	private MessageBusImpl(){
		subscriptionLists = new ConcurrentHashMap<>();
		microServiceQueues = new ConcurrentHashMap<>();
		futures = new ConcurrentHashMap<>();
	}
	public synchronized static MessageBusImpl getInstance(){
		if(instance == null){
			instance = new MessageBusImpl();
		}
		return instance;
	}
	public void print() {
		for (Map.Entry<MicroService, ConcurrentLinkedQueue<Message>> list : microServiceQueues.entrySet()) {
			//System.out.println(list.getKey().getName() + " " + list.getValue().size());
			if(list.getValue().size() > 5){
				list.getValue().clear();
				list.getValue().add(new TerminateBroadcast());
			}
		}
		for (Map.Entry<Event, Future> list : futures.entrySet()) {
			complete(list.getKey(),new Model());
		}
	}
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subscriptionLists.putIfAbsent(type, new ConcurrentLinkedQueue<>());
		subscriptionLists.get(type).add(m);
		//System.out.println("MicroService '" + m.getName() + "' Has been subscribed to event of type " + type.getSimpleName());
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		//synchronized (subscriptionLists) {
			subscriptionLists.putIfAbsent(type, new ConcurrentLinkedQueue<>());
			subscriptionLists.get(type).add(m);
			//System.out.println("MicroService '" + m.getName() + "' Has been subscribed to broadcast of type " + type.getSimpleName());
		//}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		futures.get(e).resolve(result);
		//if(e.getClass() == TrainModelEvent.class){
			//TrainModelEvent ev = (TrainModelEvent) e;
			//System.out.println(e.getClass().getSimpleName() + ", model:" + ev.getModel().getName()+ " " + "has been resolved");
		//}else {
			//System.out.println(e.getClass().getSimpleName() + " has been resolved");
		//}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		String str = "[";
		for (MicroService m : subscriptionLists.get(b.getClass())) {
			microServiceQueues.get(m).add(b);
			str+=m.getName() + ", ";
		}
		//if(b.getClass() != TickBroadcast.class)
			//System.out.println("Broadcast " + b.getClass().getSimpleName() + " was sent to " + str.substring(0,str.length() -2) + "]");
	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<>();
		futures.put(e,future);
		synchronized (subscriptionLists.get(e.getClass())){
			MicroService m = subscriptionLists.get(e.getClass()).poll();
			microServiceQueues.get(m).add(e);
			subscriptionLists.get(e.getClass()).add(m);
			//System.out.println("Event of type " + e.getClass().getSimpleName() + " was added to '" + m.getName() + "'s queue");
		}
		return future;
	}

	@Override
	public void register(MicroService m) {
		microServiceQueues.putIfAbsent(m, new ConcurrentLinkedQueue<>());
		//System.out.println("Microservice '" + m.getName() + "' (" + m.getClass().getSimpleName() + ") was registered");
	}

	@Override
	public void unregister(MicroService m) {
		//removing the MicroService from each message type's subscriptions
		if(isMicroServiceRegistered(m)){
			for (Map.Entry<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> msg : subscriptionLists.entrySet()) {
				synchronized (msg.getValue()) {
					msg.getValue().remove(m);
				}
			}
			//removing the MicroService from MicroServices list
			microServiceQueues.remove(m);
			//System.out.println("Microservice '" + m.getName() + "' (" + m.getClass().getSimpleName() + ") was unregistered");
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		//long start = System.currentTimeMillis();
		Message message = microServiceQueues.get(m).poll();
		//long end = System.currentTimeMillis();
		//System.out.println("'" + m.getName() + "' waited " + (end - start) + "ms for " + message.getClass().getSimpleName());
		return message;
	}

	@Override
	public <T> Boolean isMicroServiceSubscribedEvent(Class<? extends Event<T>> type, MicroService m){
		return subscriptionLists.get(type).contains(m);
	}
	@Override
	public Boolean isMicroServiceSubscribedBroadcast(Class<? extends Broadcast> type, MicroService m){
		return subscriptionLists.get(type).contains(m);
	}
	@Override
	public Boolean isMicroServiceRegistered(MicroService m){
		return microServiceQueues.containsKey(m);
	}
}
