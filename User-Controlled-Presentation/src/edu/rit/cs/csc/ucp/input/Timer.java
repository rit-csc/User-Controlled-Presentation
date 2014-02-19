package edu.rit.cs.csc.ucp.input;

import edu.rit.cs.csc.ucp.Actions;
import edu.rit.cs.csc.ucp.Settings;


/**
 * @author dpk3062
 * 
 * Periodically sends 'move to the next slide' action signals.  
 */
public class Timer extends Input implements Runnable, Actions {
	
	//TODO have a constructor that takes in all the fields unstead of using Settings
	
	private boolean shouldRun = true;
	private int pauseTime = 0;
	
	private final Object runSync = new Object();
	private final Object pauseSync = new Object();
	
	//--------------------------------------------------------------------------
	// Methods dealing with running
	//--------------------------------------------------------------------------
	
	/**
	 * @return False if this timer should stop (and never start again)
	 */
	private boolean shouldRun() {
		synchronized(runSync) {
			return shouldRun && !Thread.currentThread().isInterrupted();
		}
	}
	
	/**
	 * Causes this timer to stop running as soon as possible
	 */
	public void stopRunning() {
		synchronized(runSync) {
			shouldRun = false;
		}
		Thread.currentThread().interrupt();
	}
	
	//--------------------------------------------------------------------------
	// Methods dealing with pausing
	//--------------------------------------------------------------------------
	
	/**
	 * Tells the timer to pause
	 */
	public void pause() {
		synchronized(pauseSync) {
			pauseTime = Settings.getPauseTime();
		}
		System.out.println("Pausing timer: " + this);
	}
	
	/**
	 * Gets then clears the pause time
	 * @return The amount of seconds to pause
	 */
	private int usePauseTime() {
		synchronized(pauseSync) {
			int time = pauseTime;
			pauseTime = 0;
			return time;
		}
	}
	
	/**
	 * Continually pauses the timer until all the pause time has been used up
	 */
	public void handlePauses() {
		
		int time = usePauseTime();
		while(time != 0 && shouldRun()) {
			try {
				Thread.sleep(time * 1000);
			} catch(InterruptedException e) {
				
			}
			time = usePauseTime();
		}
	}
	
	//--------------------------------------------------------------------------
	// Methods from Runnable
	//--------------------------------------------------------------------------
	
	@Override
	public void run() {
		System.out.println("Timer starting: " + this);
		
		while(shouldRun()) {
			
			//sleep while the user is doing things
			handlePauses();
			if(!shouldRun()) {
				break;
			}
			
			//tell everyone it's time to change
			for(Actions a: getListeners()) {
				a.nextSlide(false);
			}
			
			//normal slideshow interval
			try {
				Thread.sleep(Settings.getVisibleTime() * 1000);
			} catch(InterruptedException e) {
				
			}
		}
		
		System.out.println("Timer stopping: " + this);
	}
	
	//--------------------------------------------------------------------------
	// Methods from Actions
	//--------------------------------------------------------------------------
	
	@Override
	public void nextSlide(boolean userAction) {
		if(!userAction) {
			return;
		}
		
		pause();
	}
	
	@Override
	public void previousSlide(boolean userAction) {
		if(!userAction) {
			return;
		}
		
		pause();
	}
	
}
