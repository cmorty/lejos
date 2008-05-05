
import lejos.nxt.*;

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
			NXTeObj.LSC(0).addServo(1,"SAVOX, Digital SC-0352");//Servo 1 connected in location 1
			NXTeObj.LSC(0).addServo(3,"SAVOX, Digital SC-0352");//Servo 2 connected in location 3
			//NXTeObj.LSC(0).addServo(2,"HITEC, HS-785HB");
			NXTeObj.LSC(0).calibrate();			
			dm.echo("Load all servos");
			NXTeObj.LSC(0).loadAllServos();
			NXTeObj.LSC(0).Servo(0).setMinAngle(0);
			NXTeObj.LSC(0).Servo(0).setMaxAngle(2000);
			NXTeObj.LSC(0).Servo(1).setMinAngle(0);
			NXTeObj.LSC(0).Servo(1).setMaxAngle(2000);

			
			while(!Button.ESCAPE.isPressed()){

				if (Button.LEFT.isPressed()){
					NXTeObj.LSC(0).Servo(0).goToMinAngle();
					NXTeObj.LSC(0).Servo(1).goToMinAngle();
					while(NXTeObj.LSC(0).Servo(0).isMoving() == true){}
					angle = NXTeObj.LSC(0).Servo(0).getAngle();
					angle2 = NXTeObj.LSC(0).Servo(1).getAngle();
					dm.echo("Goto Min");
					dm.echo(angle);
				}

				if (Button.ENTER.isPressed()){
					NXTeObj.LSC(0).Servo(0).goToMiddleAngle();
					NXTeObj.LSC(0).Servo(1).goToMiddleAngle();
					while(NXTeObj.LSC(0).Servo(0).isMoving() == true){}
					angle = NXTeObj.LSC(0).Servo(0).getAngle();								
					angle = NXTeObj.LSC(0).Servo(1).getAngle();
					
					dm.echo("Goto Middle");
					dm.echo(angle);
					dm.echo(angle2);
				}
				
				if (Button.RIGHT.isPressed()){
					NXTeObj.LSC(0).Servo(0).goToMaxAngle();
					NXTeObj.LSC(0).Servo(1).goToMaxAngle();
					while(NXTeObj.LSC(0).Servo(0).isMoving() == true){}
					angle = NXTeObj.LSC(0).Servo(0).getAngle();	
					angle = NXTeObj.LSC(0).Servo(1).getAngle();
					
					dm.echo("Goto Middle");
					dm.echo(angle);
					dm.echo(angle2);
				}						
			}
			
		}catch(Exception e){
			dm.echo(e.getMessage());
		}

		dm.echo("Test finished");
	}
}
