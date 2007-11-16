import lejos.nxt.*;

public class SpeedTest {

 static final int TOTALTIME = 60000;

 public static void main(String [] args) throws Exception {
  byte A = 0;
  LightSensor ls = new LightSensor(SensorPort.S3);
  UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
  /* Disable the speed regulation and close down the associated threads.
   * This test does not require this type of motion control.
   */
  Motor.A.regulateSpeed(false);
  Motor.B.regulateSpeed(false);
  Motor.C.regulateSpeed(false);
  Motor.A.shutdown();
  Motor.B.shutdown();
  Motor.C.shutdown();
  
  Motor.B.forward();
  Motor.C.forward();
  int iteration = 0;
  int startTime = (int)System.currentTimeMillis();
  int totalTime = 0;
  int tacho = 0;
  int distVal=0;
  int lightVal=0;
  while(totalTime < TOTALTIME) {
   lightVal = ls.readValue();
   distVal = us.getDistance();
   tacho = Motor.B.getTachoCount();
   int RN = (int)(Math.random() * 100) + 1;

   // Uncomment the following to produce display output as per the original test.
   //LCD.drawInt(tacho,0,0);
   //LCD.drawInt((lightVal + distVal + tacho)*100/RN, 0, 1);
   //LCD.drawInt(A, 0, 2);
   //LCD.drawInt(iteration, 0, 4);
   //LCD.refresh();

   // Set motor speed for B and C to RN (Using Coast)

   Motor.B.setPower(RN);
   Motor.C.setPower(RN);
   if(RN > 50) ++A;
   if(RN < 50) --A;
   
   if(A<0) Motor.A.backward(); else Motor.A.forward();

   totalTime = (int)System.currentTimeMillis() - startTime;
   iteration++;
  }
  LCD.drawInt(iteration, 0, 4);
  LCD.refresh();

  Thread.sleep(10000);
 }
}

