package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int duration; // For how many ticks the program will run
	private final int speed; // real-time value for tick

	public TimeService(int duration, int speed) {
		super("TimeService");
		this.duration = duration;
		this.speed = speed;
	}

	@Override
	protected synchronized void initialize() {
		subscribeBroadcast(TerminateBroadcast.class, (c) -> this.terminate());
		TickBroadcast broadcast = new TickBroadcast();
		Timer timer = new Timer();
		TimerTask assignment = new TimerTask() {
			@Override
			public void run() {
				sendBroadcast(broadcast);
				System.out.print("remaining ticks:" + duration +"\r");
				duration--;
				if(duration == 0){
					System.out.print("remaining ticks:" + duration);
					System.out.println("");
					timer.cancel();
					sendBroadcast(new TerminateBroadcast());
				}
			}
		};
		timer.scheduleAtFixedRate(assignment,0,speed);

	}

}
