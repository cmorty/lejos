
import lejos.nxt.*;
import lejos.robotics.RegulatedMotor;

/**
 * Entry point for the program. Creates an instance of Subsumption
 * and kicks of the lowest priority task (wander)
 */
public class Subsumption1  {
	public static Subsumption main;
	public static void main (String[] arg)
	  throws Exception {
	  	main = new Subsumption();
		main.tasks[0].execute();
	}
}

/**
 * Coordinates a prioritised set of tasks. Tasks with higher priority
 * completely block those of a lower priority. Creates the following
 * tasks with increasing order of priority:
 * - Wander:      wander around
 * - RightBumber: avoid obstacles on the right.
 * - LeftBumber:  avoid obstacles on the left.
 * Plus it gates access to the effectors (i.e. the motors) ensuring
 * that it's priority policy is strictly enforced.
 */
class Subsumption implements ButtonListener {
	public boolean running;
	public int owner;
	Task tasks[];
	
	public Subsumption() {
		running = true;
		Button.ENTER.addButtonListener(this);
		tasks = new Task[3];
		tasks[0]=new Wander();
		tasks[1]=new RightBumber();
		tasks[2]=new LeftBumber();
		tasks[0].start();
		tasks[1].start();
		tasks[2].start();
	}

	int getPriority(Task t) {
		for (int i=0; i<tasks.length; i++) {
			if (tasks[i] == t)
				return i;
		}
		
		return -1;
	}

	/**
	 * Arbitrates between the various tasks.
	 */	
	public synchronized void execute(Task requestor) {
		int pri = getPriority(requestor);

		// If its a lower priorty than the current task, ignore it.
		if (pri < owner)
			return;

		// This is the new owner of the single output that we have
		owner = pri;

		// Start new owner from beginning (even if it was the same one)
		tasks[owner].reset();
	}

	/**
	 * Only allow the owner to do stuff, just in case some other task calls us
	 */	
	public synchronized void setMotor(Task requestor, RegulatedMotor motor, int speed, boolean forward) {
		if (owner == getPriority(requestor)) {
			motor.setSpeed(speed);
			if (forward)
				motor.forward();
			else
				motor.backward();
		}
	}

	/**
	 * Task has finished. Re-start next runnable task.
	 */	
	public synchronized void release(Task releaser) {
		int pri = getPriority(releaser);
		
		// If it isn't the owner releasing, ignore it.
		if (owner == pri) {
			// Search for the first runnable task. There is always one.
			for(int i=pri-1; i >= 0; i--) {
				if (tasks[i].running()) {
					owner = i;
					tasks[owner].reset();
				}	
			}
		}
	}

	/**
	 * Called within the scope of a thread defined by Button.addButtonListener().
	 */			
	public void buttonPressed(Button b) {
		running=false;
	}
		
	public void buttonReleased(Button b) {
	}
}

/**
 * Functor interface. Or, to put it another way, the interface to
 * actions stored in a finite state machine (fsm).
 */
interface Action {
	public int act();
}

/**
 * All tasks (wander, leftbumber, rightbumber) extend this class. This class
 * defines the lifecycle of a task. Namely:
 * Reset:   Re-initialise task
 * Execute: Attempt to run (may not succeed if a higher priority task is running).
 * Run:     Execute an FSM.
 * Release: Stop running.
 */
abstract class Task extends Thread {
	public static final RegulatedMotor LEFT_MOTOR = Motor.C ;
	public static final RegulatedMotor RIGHT_MOTOR = Motor.A;
	public static final TouchSensor LEFT_BUMBER = new TouchSensor(SensorPort.S3);;
	public static final TouchSensor RIGHT_BUMBER = new TouchSensor(SensorPort.S1);
	public static final boolean FORWARD = true;
	public static final boolean BACKWARD = false;
	public static final int END = -1;
	public static final int START = 0;
	
	public Action actions[];
	public int fsm[];
	public int state = END;

	/**
	 * Reset the FSM to its initial state.
	 */	
	public void reset() {
		state = START;
	}

	/**
	 * The thread entry point. Either runs the action's FSM to completion -
	 * sleeping or yielding between each state - or until 'running' is false.
	 * When finished call 'release'.
	 * <P>
	 * FSM is really a bit of a misnomer as there are no input events so
	 * there is only one transition from each state to the next.
	 */
	public void run() {
		// Keep running until the program should exit.
		do {
			// Quiesce
			while (state != START && Subsumption1.main.running) {
				yield();
			}

			// Execute the FSM until it stops...
			do {
				int toSleepFor;
				
				synchronized (Subsumption1.main) {
					toSleepFor = actions[state].act();
					state = fsm[state];
				}
				if (toSleepFor > 0) {
					try {
						sleep(toSleepFor);
					} catch (InterruptedException ie) {
					}
				}
				else	
					yield();
			} while (state != END && Subsumption1.main.running);
			
			// Its over, release the actuators.
			release();
		} while (Subsumption1.main.running);
	}

	/**
	 * Inform the coordinator that we have released the actuators.
	 */	
	public void release()  {
		Subsumption1.main.release(this);
	}

	/**
	 * Request control of the actuators
	 */	
	public void execute() {
		if (Subsumption1.main != null)
			Subsumption1.main.execute(this);
	}

	/**
	 * Return true if the FSM is executing, false otherwise.
	 */
	public boolean running()  {
		return state != END;
	}
	
	/**
	 * Convenience function to make it appear to subclasses that
	 * they have direct control of the actuators when they are in
	 * fact gated by the controller.
	 */	
	public void setMotor(RegulatedMotor motor, int speed, boolean forward) {
		Subsumption1.main.setMotor(this, motor, speed, forward);
	}
}

/**
 * Defines a finite state machine to avoid an obstacle on the left.
 */		
class LeftBumber extends Task implements SensorPortListener {
	public LeftBumber() {
		actions = new Action[3];
		actions[0] = new Action() {
			public int act() {
				setMotor(LEFT_MOTOR, 200, BACKWARD);
				setMotor(RIGHT_MOTOR, 200, BACKWARD);
				return 200;
			}
		};
		
		actions[1] = new Action() {
			public int act() {
				setMotor(LEFT_MOTOR, 200, FORWARD);
				return 200;
			}
		};

		// Shouldn't really need to do this one, but reset()
		// may not be immediate on lower priority tasks.
		actions[2] = new Action() {
			public int act() {
				setMotor(RIGHT_MOTOR, 200, FORWARD);
				return 0;
			}
		};

		fsm = new int[3];		
		fsm[0] = 1;
		fsm[1] = 2;
		fsm[2] = END;
		SensorPort.S3.addSensorPortListener(this);
	}

	/**
	 * This is actually executed in a thread established by
	 * LEFT_BUMBER.addSensorListener().
	 */	
	public void stateChanged(SensorPort bumber, int oldValue, int newValue) {
		Sound.playTone(440, 10);
		if (LEFT_BUMBER.isPressed()) {
			Sound.playTone(500, 10);
			execute();
		}
	}
}

/**
 * Defines a finite state machine to avoid an obstacle on the right.
 */		
class RightBumber extends Task implements SensorPortListener {
	public RightBumber() {
		actions = new Action[3];
		actions[0] = new Action() {
			public int act() {
				setMotor(LEFT_MOTOR, 200, BACKWARD);
				setMotor(RIGHT_MOTOR, 200, BACKWARD);
				return 200;
			}
		};
		
		actions[1] = new Action() {
			public int act() {
				setMotor(RIGHT_MOTOR, 200, FORWARD);
				return 200;
			}
		};

		// Shouldn't really need to do this one, but reset()
		// may not be immediate on lower priority tasks.
		actions[2] = new Action() {
			public int act() {
				setMotor(LEFT_MOTOR, 200, FORWARD);
				return 0;
			}
		};

		fsm = new int[3];		
		fsm[0] = 1;
		fsm[1] = 2;
		fsm[2] = END;
		SensorPort.S1.addSensorPortListener(this);
	}

	/**
	 * This is actually executed in a thread established by
	 * RIGHT_BUMBER.addSensorListener().
	 */	
	public void stateChanged(SensorPort bumber, int oldValue, int newValue) {
		Sound.playTone(1000, 10);
		if (RIGHT_BUMBER.isPressed()) {
			Sound.playTone(1400, 10);
			execute();
		}
	}
}

/**
 * Defines a finite state machine to wander around aimlessley. Note that
 * this does not really work very well as higher priority behaviours
 * could occur in the middle of a sleep which means that once the higher
 * priority behaviour terminates the robot will continue on its last
 * trajectory.
 */		
class Wander extends Task {
	public Wander() {
		actions = new Action[3];
		actions[0] = new Action() {
			public int act() {
				setMotor(LEFT_MOTOR, 200, FORWARD);
				setMotor(RIGHT_MOTOR, 200, FORWARD);
				return 5000;
			}
		};
		
		actions[1] = new Action() {
			public int act() {
				setMotor(LEFT_MOTOR, 100, FORWARD);
				setMotor(RIGHT_MOTOR, 200, FORWARD);
				return 2000;
			}
		};
		
		actions[2] = new Action() {
			public int act() {
				setMotor(LEFT_MOTOR, 200, BACKWARD);
				setMotor(RIGHT_MOTOR, 200, FORWARD);
				return 700;
			}
		};
		
		fsm = new int[3];		
		fsm[0] = 1;
		fsm[1] = 2;
		fsm[2] = 0;
	}
}

