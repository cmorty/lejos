package lejos.tutorial.speech;

import josx.rcxcomm.*;
import java.io.*;
import josx.platform.rcx.*;

public class Rover implements SensorConstants, RemoteVisionConstants {
  public void run() throws IOException {

    // Open the port for remote commands

    RCXPort port = new RCXPort();
    DataInputStream in = new DataInputStream(port.getInputStream());
    DataOutputStream out = new DataOutputStream(port.getOutputStream());

    // Set long range for IR
    
    LLC.setRangeLong();

    // Set the power low for camera tilting

    Motor.B.setPower(0);

    // Set Sensor 1 to detect bumper hit

    Sensor.S1.setTypeAndMode(SENSOR_TYPE_TOUCH, SENSOR_MODE_BOOL);
    Sensor.S1.addSensorListener (new SensorListener() {
      public void stateChanged (Sensor src, int oldValue, int newValue) {

        // Back off when we touch something

        if (newValue == 0) return;
        Motor.A.backward();
        Motor.C.backward();
        sleep(500);
        Motor.A.stop();
        Motor.C.stop(); 
      }
    });

    // Execute remote commands from the PC

    for(;;) {
      int op = in.readByte();

      if (op == METHOD_JOSX_VISION_RCX_FORWARD_V) {
        Motor.A.forward();
        Motor.C.forward();
      } else if (op == METHOD_JOSX_VISION_RCX_BACKWARD_V) {
        Motor.A.backward();
        Motor.C.backward();
      } else if (op == METHOD_JOSX_VISION_RCX_STOP) {
        Motor.A.stop();
        Motor.C.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_SPIN_LEFT_V) {
        Motor.A.backward();
        Motor.C.forward();
      } else if (op == METHOD_JOSX_VISION_RCX_SPIN_RIGHT_V) {
        Motor.A.forward();
        Motor.C.backward();
      } else if (op == METHOD_JOSX_VISION_RCX_FORWARD_I) {
        Motor.A.forward();
        Motor.C.forward();
        sleep((in.readByte() & 0xFF) * 100);
        Motor.A.stop();
        Motor.C.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_BACKWARD_I) {
        Motor.A.backward();
        Motor.C.backward();
        sleep((in.readByte() & 0xFF) * 100);
        Motor.A.stop();
        Motor.C.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_SPIN_LEFT_I) {
        Motor.A.backward();
        Motor.C.forward();
        sleep((in.readByte() & 0xFF) * 100);
        Motor.A.stop();
        Motor.C.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_SPIN_RIGHT_I) {
        Motor.A.forward();
        Motor.C.backward();
        sleep((in.readByte() & 0xFF) * 100);
        Motor.A.stop();
        Motor.C.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_TURN_LEFT_V) {
        Motor.A.stop();
        Motor.C.forward();
      } else if (op == METHOD_JOSX_VISION_RCX_TURN_LEFT_I) {
        Motor.A.stop();
        Motor.C.forward();
        sleep((in.readByte() & 0xFF) * 100);
        Motor.C.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_TURN_RIGHT_V) {
        Motor.A.stop();
        Motor.C.forward();
      } else if (op == METHOD_JOSX_VISION_RCX_TURN_RIGHT_I) {
        Motor.A.stop();
        Motor.C.forward();
        sleep((in.readByte() & 0xFF) * 100);
        Motor.C.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_SCAN_LEFT_V) {
        Motor.C.backward();
      } else if (op == METHOD_JOSX_VISION_RCX_SCAN_LEFT_I) {
        Motor.C.backward();
        sleep((in.readByte() & 0xFF) * 100);
        Motor.C.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_SCAN_RIGHT_V) {
        Motor.C.forward();
      } else if (op == METHOD_JOSX_VISION_RCX_SCAN_RIGHT_I) {
        Motor.C.forward();
        sleep((in.readByte() & 0xFF) * 100);
        Motor.C.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_TILT_UP_I) {
        Motor.B.backward();
        sleep((in.readByte() & 0xFF) * 100);
        Motor.B.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_TILT_DOWN_I) {
        Motor.B.forward();
        sleep((in.readByte() & 0xFF) * 100);
        Motor.B.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_SET_POWER) {
        int m = in.readByte();
        int p = in.readByte();
        if ((m & 1) == 1) Motor.A.setPower(p);
        if ((m & 2) == 2) Motor.B.setPower(p);
        if ((m & 4) == 4) Motor.C.setPower(p);
      } else if (op == METHOD_JOSX_VISION_RCX_CONTROL_MOTORS) {
        int m = in.readByte();
        int d = in.readByte();
        if ((m & 1) == 1) if ((d & 1) == 1) Motor.A.forward(); else Motor.A.backward();
        if ((m & 2) == 2) if ((d & 2) == 2) Motor.B.forward(); else Motor.B.backward();
        if ((m & 4) == 4) if ((d & 4) == 4) Motor.C.forward(); else Motor.C.backward();
        sleep((in.readByte() & 0xFF) * 100);
        Motor.A.stop();
        Motor.B.stop();
        Motor.C.stop();
      } else if (op == METHOD_JOSX_VISION_RCX_PLAY_TONE) {
        int f = in.readShort();
        int d = in.readByte();
        Sound.playTone(f,d);
      }
    }
  } 

  public static void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ie) {}
  }

  public static void main(String [] args) throws IOException {
    (new Rover()).run();
  }
}     

