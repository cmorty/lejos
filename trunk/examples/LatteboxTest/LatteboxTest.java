
import lejos.nxt.*;

public class LatteboxTest{
	private static NXTe NXTeObj;
	private static DebugMessages dm;
	private static int angle;
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
			NXTeObj.LSC(0).addServo(0,"SAVOX, Digital SC-0352");
			NXTeObj.LSC(0).addServo(1,"SAVOX, Digital SC-0352");
			//NXTeObj.LSC(0).addServo(2,"HITEC, HS-785HB");
			NXTeObj.LSC(0).calibrate();			
			dm.echo("Load all servos");
			NXTeObj.LSC(0).loadAllServos();
			dm.echo(NXTeObj.LSC(0).Servo(0).getName());
			NXTeObj.LSC(0).Servo(0).setMinAngle(200);
			NXTeObj.LSC(0).Servo(0).setMaxAngle(1700);
			
			NXTeObj.LSC(0).Servo(0).setDelay(1, 2);
			
			while(!Button.ESCAPE.isPressed()){

				if (Button.LEFT.isPressed()){
					//NXTeObj.LSC(0).Servo(0).setAngle(1, 400);
					NXTeObj.LSC(0).Servo(0).goToMinAngle();				
					while(NXTeObj.LSC(0).Servo(0).isMoving() == true){
						//dm.echo(NXTeObj.LSC(0).Servo(0).readMotion());
					}
					angle = NXTeObj.LSC(0).Servo(0).getAngle(1);
					
					dm.echo("Goto Min");
					dm.echo(angle);
				}

				if (Button.ENTER.isPressed()){
					NXTeObj.LSC(0).Servo(0).goToMiddleAngle();
					while(NXTeObj.LSC(0).Servo(0).isMoving() == true){
						//dm.echo(NXTeObj.LSC(0).Servo(0).readMotion());
					}
					angle = NXTeObj.LSC(0).Servo(0).getAngle(1);								
					
					dm.echo("Goto Middle");
					dm.echo(angle);
				}
				
				if (Button.RIGHT.isPressed()){
					//NXTeObj.LSC(0).Servo(0).setAngle(1, 2000);
					NXTeObj.LSC(0).Servo(0).goToMaxAngle();
					while(NXTeObj.LSC(0).Servo(0).isMoving() == true){
						//dm.echo(NXTeObj.LSC(0).Servo(0).readMotion());
					}
					angle = NXTeObj.LSC(0).Servo(0).getAngle(1);								
					
					dm.echo("Goto Middle");
					dm.echo(angle);
				}						
			}
			
		}catch(Exception e){
			dm.echo(e.getMessage());
		}

		dm.echo("Test finished");
	}
}
