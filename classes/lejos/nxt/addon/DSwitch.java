package lejos.nxt.addon;

import lejos.nxt.*;

/**
 * This class manage the device dSwitch which
 * is a controller designed to be used with Lego NXT Mindstorms hardware and software
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class DSwitch extends Motor {

	  public DSwitch(TachoMotorPort port){
	    super(port);
	  }
	  
	  public void turnOn(){
		  this.forward();
	  }
	  
	  public void turnOff(){
		  this.stop();
	  }
}