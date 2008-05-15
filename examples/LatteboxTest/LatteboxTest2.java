
import lejos.nxt.*;

public class LatteboxTest2{
	private static NXTe NXTeObj;
	private static DebugMessages dm;
	private static int angle;
	private static int angle2;
	private static int motion;
	private static boolean direction = false;
	
	//Main
	public static void main(String[] args) throws Exception{
		dm = new DebugMessages();
		dm.setLCDLines(6);
		dm.echo("Testing NXTe");
		
		try{

			NXTeObj = new NXTe(SensorPort.S1);//NXTe Controller plugged in Port1
			NXTeObj.addLSC(0);
			dm.echo("Calibrating LSC");
			//Servo 1 connected in location 1			
			NXTeObj.LSC(0).addServo(1,"Hitec, HSR-1422CR");
			NXTeObj.LSC(0).calibrate();
			NXTeObj.LSC(0).Servo(0).load();
			NXTeObj.LSC(0).Servo(0).setMinAngle(0);
			NXTeObj.LSC(0).Servo(0).setMaxAngle(2000);
			
			
			while(!Button.ESCAPE.isPressed()){

				if (Button.LEFT.isPressed()){
					if(direction == true){
						NXTeObj.LSC(0).Servo(0).goToMinAngle();
						direction = false;
					}else{
						NXTeObj.LSC(0).Servo(0).goToMaxAngle();
						direction = true;
					}
					
					while(NXTeObj.LSC(0).Servo(0).isMoving() == true){}
					angle = NXTeObj.LSC(0).Servo(0).getAngle();
					dm.echo("Goto Min");
					dm.echo(angle);
				}

				if (Button.ENTER.isPressed()){
					NXTeObj.LSC(0).Servo(0).unload();
					dm.echo("Unload servo");
				}
				
				if (Button.RIGHT.isPressed()){
					NXTeObj.LSC(0).Servo(0).load();
					dm.echo("Load servo");
				}						
			}
			
		}catch(Exception e){
			dm.echo(e.getMessage());
		}

		//At the end, unload all Servos
		NXTeObj.LSC(0).unloadAllServos();
		dm.echo("Test finished");
	}
}
