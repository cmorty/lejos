import lejos.nxt.*;

/**
 * Entry point for the program. This version uses
 * the enhanced Thread interface available in lejos1.0.2
 */
public class Subsumption2 {
	public static void main (String[] arg)
	  throws Exception {
		// Won't return until the 'RUN' button is pressed.
	  	runIt();
	  }
	  
	/**
	 * Return the FSM for wandering around aimlessly
	 */
	static Action2[] getWanderFSM() {
		Action2[] actions = new Action2[3];

		actions[0] = new Action2() {
			public int act() {
				Motor.C.setSpeed(400); Motor.C.forward();
				Motor.A.setSpeed(400); Motor.A.forward();
				return 5000;
			}

			public int nextState() {
				return 1;
			}
		};
		
		actions[1] = new Action2() {
			public int act() {
				Motor.C.setSpeed(200);
				return 2000;
			}

			public int nextState() {
				return 2;
			}
		};
		
		actions[2] = new Action2() {
			public int act() {
				Motor.C.setSpeed(200); Motor.C.backward();
				return 700;
			}

			public int nextState() {
				return 0;
			}
		};
		
		return actions;
	}

	/**
	 * Return the FSM for avoiding obstacles on the left.
	 */
	static Action2[] getAvoidLeftFSM() {
		Action2[] actions = new Action2[2];

		actions[0] = new Action2() {
			public int act() {
				Motor.C.setSpeed(400); Motor.C.backward();
				Motor.A.setSpeed(400); Motor.A.backward();
				return 1000;
			}

			public int nextState() {
				return 1;
			}
		};
		
		actions[1] = new Action2() {
			public int act() {
				Motor.C.forward();
				return 400;
			}

			public int nextState() {
				return Action2.END;
			}
		};

		return actions;
	}

	/**
	 * Return the FSM for avoiding obstacles on the right.
	 */
	static Action2[] getAvoidRightFSM() {
		Action2[] actions = new Action2[2];

		actions[0] = new Action2() {
			public int act() {
				Motor.C.setSpeed(400); Motor.C.backward();
				Motor.A.setSpeed(400); Motor.A.backward();
				return 500;
			}

			public int nextState() {
				return 1;
			}
		};
		
		actions[1] = new Action2() {
			public int act() {
				Motor.A.forward();
				return 400;
			}

			public int nextState() {
				return Action2.END;
			}
		};

		return actions;
	}


	/**
	 * Build up the behavioural model. Wire the sensors to the actuators.
	 * Kick 'em off. Wait until the RUN button is pressed and then return.
	 */
	public static void runIt() {
	  	Sense s1 = new SenseNoOwner(new Actuator(getWanderFSM()));
		s1.setPri(Thread.MIN_PRIORITY);

		Sense s2 = new SenseBumper(SensorPort.S3, new Actuator(getAvoidLeftFSM()));
		s2.setPri(Thread.MIN_PRIORITY+1);

		Sense s3 = new SenseBumper(SensorPort.S1, new Actuator(getAvoidRightFSM()));
		s3.setPri(Thread.MIN_PRIORITY+1);
	
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		s1.runIt();
		s2.runIt();	
		s3.runIt();

		Button.ENTER.waitForPressAndRelease();
	}
}

/**
 * Functor interface. Or, to put it another way, the interface to
 * actions stored in a finite state machine (fsm).
 */
interface Action2 {
	public static final int END = -1;
	public static final int START = 0;

	/**
	 * Perform some sequence of actions.
	 */
	public int act();

	/**
	 * Return what the next state should be.
	 */
	public int nextState();
}

/**
 * A runnable instance of an FSM,
 */
class Actuator extends Thread {
	// Any old object will do as the 'arbitrator'
	static Object arbitrator = new Object();
	
	// This must be set to the one actuator allowed to execute whilst the
	// monitor of 'arbitrator' is owned.
	static Actuator owner;
	
	protected Action2 actions[];
	protected int state = Action2.END;
	
	// Useful for debugging.	
	public static int tcount = 0;
	public int task;

	/**
	 * Constructor. Sets a task id that can be used to identify this instance.
	 * Sets the thread daemon flag to true.
	 *
	 * @param actions an array of Action items to be executed.
	 */
	public Actuator(Action2[] actions) {
		this.actions = actions;
		task = ++tcount;
		setDaemon(true);
	}
	
	/**
	 * The thread entry point. Runs the Actuator's FSM to completion or until
	 * it looses ownership.  Wait on the arbitrator's monitor between each state.
	 * It might be nice if tasks were left running and just had their access to
	 * the actuators gated, but the same effect can be achieved by their just
	 * running a worker thread if they need some background processing done.
	 * <P>
	 * FSM is really a bit of a misnomer as there are no input events so
	 * there is only one transition from each state to the next.
	 */
	public void run() {
		// Keep running until the program should exit.
		synchronized (arbitrator) {
			do {
				// Wait until we get ownership.
				while (owner != this) {
					try  {
						arbitrator.wait();	// Release arbitrator until notified
					} catch (InterruptedException ie) {
					}
				}
				
				// Set state to start because we might have been terminated
				// prematurely and we always start from the beginning.
				state = Action2.START;
				
				// Loop until we end or we loose ownership.				
				while (owner == this && state != Action2.END) {
					try  {
						// Call wait() because it releases the arbitrator.
						arbitrator.wait(actions[state].act());
					} catch (InterruptedException ie) {
					}
					state = actions[state].nextState();
				}

				// If we ran to completion signify no owner.				
				if (state == Action2.END)
					owner = null;
				
				arbitrator.notifyAll();	
			} while (true);
		}
	}

	/**
	 * Attempt to run this Actuator.
	 */	
	public void execute() {
		synchronized (arbitrator) {
			// Basically, set a global flag that all threads can test
		// to see if they should stop running their FSM.
			owner = this;
			
			// Wake up anything waiting on 'arbitrator'.
			arbitrator.notifyAll();
		}
	}
}

/**
 * Base class for sensor listener thread. This is tightly coupled to
 * an actuator in this implementation. If its sensor listener is called
 * it grabs that sensor's monitor and calls notifuAll(). This should wake
 * up any threads wait()ing on that sensor.
 * <P>
 * Sub-classes should implement run() to wait on the sensor's monitor.
 */
abstract class Sense extends Thread implements SensorPortListener {
	Actuator actuator;
	int lastTime = 0;
	
	Sense(Actuator actuator) {
		this.actuator = actuator;
		setDaemon(true);
	}

	/**
	 * This is actually executed in a thread established by
	 * &lt;bumper&gt;.addSensorListener(). That thread executes at
	 * MAX_PRIORITY so just hand the call off.
	 */	
	public void stateChanged(SensorPort bumper, int oldValue, int newValue) {
		synchronized (bumper) {
			if (((int) System.currentTimeMillis() - lastTime) > 500) {
				lastTime = (int) System.currentTimeMillis();
				bumper.notifyAll();				
			}
		}
	}
	
	public void setPri(int priority) {
		actuator.setPriority(priority);
		setPriority(priority);
	}
	
	public void runIt() {
		actuator.start();
		start();
	}
}

/**
 * Defines a thread to detect an obstacle on the left. Waits on its bumper
 * and, when notified, will execute its actuator if the bumper's value is true.
 */
class SenseBumper extends Sense {
	SensorPort bumper;

	SenseBumper(SensorPort bumper, Actuator actuator) {
		super(actuator);

		this.bumper = bumper;
		
		// Add a listener for the bumper
		bumper.addSensorPortListener(this);
	}
	
	public void run() {
		// Never exit the thread
		while (true) {
			// Grab the monitor of the bumper
			synchronized (bumper) {
			
				// While bumper isn't pressed wait.
				do {
					try {
						bumper.wait();
					} catch (InterruptedException ie) {
					}
					Sound.playTone(440, 10);
				} while (bumper.readRawValue() > 600);
			}
			Sound.playTone(500, 10);
			if (bumper == SensorPort.S3) LCD.drawString("Left Bumper",0,0);
			if (bumper == SensorPort.S1) LCD.drawString("Right Bumper",0,0);
			LCD.refresh();
			// Execute our FSM
			actuator.execute();
		}
	}
}

/**
 * A class to sense when the arbitrator has no owner so we can give it one.
 * Waits on the arbitrator and when notified checks to see if there is an owner.
 * If not it executes its actuator.
 */
class SenseNoOwner extends Sense  {
	public SenseNoOwner(Actuator actuator) {
		super(actuator);
	}
	
	public void run() {
		while (true) {
			synchronized (Actuator.arbitrator) {
				// If there is no owner, we'll take it.
				if (Actuator.owner == null)
					actuator.execute();

				try {
					Actuator.arbitrator.wait();	// Wait until notified
				} catch (InterruptedException ie) {
				}
			}	
		}
	}
}


