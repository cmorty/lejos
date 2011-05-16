import java.util.*;
import lejos.nxt.*;

public class SpeedTest {

 static final int TOTALTIME = 60000;

 public static void main(String [] args) throws Exception {
  byte A = 0;
  Random rand = new Random();
  LightSensor ls = new LightSensor(SensorPort.S3);
  UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
  us.continuous();
  NXTMotor MA = new NXTMotor(MotorPort.A);
  NXTMotor MB = new NXTMotor(MotorPort.B);
  NXTMotor MC = new NXTMotor(MotorPort.C);
  
  MB.forward();
  MC.forward();
  int iteration = 0;
  int startTime = (int)System.currentTimeMillis();
  int totalTime = 0;
  int tacho = 0;
  int distVal=0;
  int lightVal=0;
  while(totalTime < TOTALTIME) {
   lightVal = ls.readValue();
   //distVal = us.getDistance();
   tacho = MB.getTachoCount();
   int RN = rand.nextInt( 100) + 1;
   int V3 = (lightVal + distVal + tacho)*100/RN;

   // Uncomment the following to produce display output as per the original test.
   LCD.drawInt(tacho,0,0);
   LCD.drawInt(V3, 0, 1);
   LCD.drawInt(A, 0, 2);
   LCD.drawInt(iteration, 0, 4);

   // Set motor speed for B and C to RN (Using Coast)

   MB.setPower(RN);
   MC.setPower(RN);
   if(RN > 50) ++A;
   if(RN < 50) --A;
   
   if(A<0) MA.backward(); else MA.forward();

   totalTime = (int)System.currentTimeMillis() - startTime;
   iteration++;
  }

  MA.stop();
  MB.stop();
  MC.stop();

  LCD.drawInt(iteration, 0, 4);

  Thread.sleep(10000);
 }
}

