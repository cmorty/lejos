
import lejos.nxt.*;
import lejos.nxt.addon.*;
import lejos.util.*;

/**
 * Example created to test Lattebox NXTe Kit
 * 
 * this example manage 2 RC Servos connected to NXTe
 * 
 * @author Juan Antonio Brenha Moral
 */
public class LatteboxTest{
	private static NXTe NXTeObj;
	private static DebugMessages dm;
	private static int angle;
	private static int angle2;
	private static int motion;
	
	//Main
	public static void main(String[] args) throws Exception{
		dm = new DebugMessages();
		dm.setLCDLines(6);
		dm.echo("Testing NXTe");
		
		try{

			NXTeObj = new NXTe(SensorPort.S1);//NXTe Controller pluged in Port1
			NXTeObj.addLSC(0);
			dm.echo("Calibrating LSC");
			//Servo 1 connected in location 1			
			NXTeObj.getLSC(0).addServo(1,"SAVOX, Digital SC-0352");
			//Servo 2 connected in location 3
			NXTeObj.getLSC(0).addServo(3,"SAVOX, Digital SC-0352");
			//NXTeObj.LSC(0).addServo(2,"HITEC, HS-785HB");
			NXTeObj.getLSC(0).calibrate();			
			dm.echo("Load all servos");
			NXTeObj.getLSC(0).loadAllServos();
			
			while(!Button.ESCAPE.isPressed()){

				if (Button.LEFT.isPressed()){
					NXTeObj.getLSC(0).getServo(0).goToMinAngle();
					NXTeObj.getLSC(0).getServo(1).goToMinAngle();
					while(NXTeObj.getLSC(0).getServo(0).isMoving() == true){}
					angle = NXTeObj.getLSC(0).getServo(0).getAngle();
					angle2 = NXTeObj.getLSC(0).getServo(1).getAngle();
					dm.echo("Goto Min");
					dm.echo(angle);
				}

				if (Button.ENTER.isPressed()){
					NXTeObj.getLSC(0).getServo(0).goToMiddleAngle();
					NXTeObj.getLSC(0).getServo(1).goToMiddleAngle();
					while(NXTeObj.getLSC(0).getServo(0).isMoving() == true){}
					angle = NXTeObj.getLSC(0).getServo(0).getAngle();								
					angle = NXTeObj.getLSC(0).getServo(1).getAngle();
					
					dm.echo("Goto Middle");
					dm.echo(angle);
					dm.echo(angle2);
				}
				
				if (Button.RIGHT.isPressed()){
					NXTeObj.getLSC(0).getServo(0).goToMaxAngle();
					NXTeObj.getLSC(0).getServo(1).goToMaxAngle();
					while(NXTeObj.getLSC(0).getServo(0).isMoving() == true){}
					angle = NXTeObj.getLSC(0).getServo(0).getAngle();	
					angle = NXTeObj.getLSC(0).getServo(1).getAngle();
					
					dm.echo("Goto Max");
					dm.echo(angle);
					dm.echo(angle2);
				}						
			}
			
		}catch(Exception e){
			dm.echo(e.getMessage());
		}

		//At the end, unload all Servos
		NXTeObj.getLSC(0).unloadAllServos();		
		dm.echo("Test finished");
	}
}
