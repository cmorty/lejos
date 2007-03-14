import lejos.nxt.*;

public class TestSensor
{
  public static void main (String[] aArg)
  throws Exception
  {
    String tot = "Tot Mem:";
    String free = "Free Mem:";
    String batt = "Battery:";
    String butt = "Buttons:";
    String s1 = "Sensor1:";
    String s2 = "Sensor2:";
    String s3 = "Sensor3:";
    String s4 = "Sensor4:";
    String t = "T";
    String f = "F";

    TouchSensor touch = new TouchSensor(SensorPort.S1);
    LightSensor light = new LightSensor(SensorPort.S2);
    SoundSensor sound = new SoundSensor(SensorPort.S3);

    for(;;) {
       LCD.clear();
       LCD.drawString(batt,0,0);
       LCD.drawInt(Battery.getVoltageMilliVolt(),9,0);
       LCD.drawString(butt,0,1);
       LCD.drawInt(Button.readButtons(),9,1);
       LCD.drawString(s1,0,2);
       LCD.drawInt(SensorPort.S1.readRawValue(),9,2);
       LCD.drawString((touch.isPressed() ? t : f),14,2);
       LCD.drawString(s2,0,3);
       LCD.drawInt(SensorPort.S2.readRawValue(),9,3);
       LCD.drawInt(light.readValue(),14,3);
       LCD.drawString(s3,0,4);
       LCD.drawInt(SensorPort.S3.readRawValue(),9,4); 
       LCD.drawInt(sound.readValue(),14,4);
       LCD.drawString(s4,0,5);
       LCD.drawInt(SensorPort.S4.readRawValue(),9,5); 
       LCD.drawString(tot,0,6);
       LCD.drawInt((int)(Runtime.getRuntime().totalMemory()),9,6);
       LCD.drawString(free,0,7);
       LCD.drawInt((int)(Runtime.getRuntime().freeMemory()),9,7);
       LCD.refresh();
       Thread.sleep(1000);
    }
  }
}

