import lejos.nxt.*;

public class TestMotor
{
  public static void main (String[] aArg)
  throws Exception
  {
    String m1 = "Motor A: ";
    String m2 = "Motor B: ";
    String m3 = "Motor C: ";
    String p = "Port S1: ";

    TouchSensor touch = new TouchSensor(SensorPort.S1);

    for(;;) {
       LCD.clear();
       LCD.drawString(m1,0,1);
       LCD.drawInt(Motor.A.getTachoCount(),9,1);
       LCD.drawString(m2,0,2);
       LCD.drawInt(Motor.B.getTachoCount(),9,2);
       LCD.drawString(m3,0,3);
       LCD.drawInt(Motor.C.getTachoCount(),9,3);
       LCD.drawString(p,0,4);
       LCD.drawInt(SensorPort.S1.readRawValue(),9,4);
       Motor.A.setSpeed(100);
       if (touch.isPressed()) Motor.A.forward();
       Thread.sleep(1000);
       if (touch.isPressed()) Motor.A.stop();
       LCD.refresh();
       Thread.sleep(1000);
    }
  }
}

