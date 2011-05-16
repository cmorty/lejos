import java.io.*;
import javax.microedition.lcdui.*;
import lejos.nxt.*;
import lejos.nxt.addon.*;
import lejos.nxt.comm.*;
import lejos.nxt.remote.*;
import lejos.robotics.*;

/**
 * Test motor implementations that support the DCMotor interface
 * including NXT motors, RCX motors and PF motors,
 * connected directly, remotely, or via a multiplexer or other device.
 */
public class MotorTester implements CommandListener {
  private static final int CMDID_EXIT_APP         = 2;
  private static final Command EXIT_COMMAND = new Command(CMDID_EXIT_APP, Command.STOP, 2);
  
  private List typeMenu, connMenu, portMenu, sensorMenu, testMenu, menu;
  private Display display;
  private Ticker motorTicker = new Ticker("Select motor type");
  private Ticker portTicker = new Ticker("Select motor port");
  private Ticker sensorTicker = new Ticker("Select sensor port");
  private Ticker connTicker = new Ticker("Select conn type");
  private Ticker testTicker = new Ticker("Testing");
  
  private String motorText, portText, connText; 
  private int motorType, connType, motorPort;
  private DCMotor motor = null;
  
  public static void main(String[] options) {
     (new MotorTester()).run();
  }
  
  public void run() {
    typeMenu = new List("Motor Type", Choice.IMPLICIT);
    typeMenu.append("NXT", null);
    typeMenu.append("RCX", null);
    typeMenu.append("PF", null);
    typeMenu.addCommand(EXIT_COMMAND);
    typeMenu.setCommandListener(this);
    typeMenu.setTicker(motorTicker);
    
    portMenu = new List("Port", Choice.IMPLICIT);
    portMenu.append("MotorPort.A", null);
    portMenu.append("MotorPort.B", null);
    portMenu.append("MotorPort.C", null);
    portMenu.addCommand(EXIT_COMMAND);
    portMenu.setCommandListener(this);
    portMenu.setTicker(portTicker);
    
    sensorMenu = new List("Port", Choice.IMPLICIT);
    sensorMenu.append("SensorPort.S1", null);
    sensorMenu.append("SensorPort.S2", null);
    sensorMenu.append("SensorPort.S3", null);
    sensorMenu.append("SensorPort.S4", null);
    sensorMenu.addCommand(EXIT_COMMAND);
    sensorMenu.setCommandListener(this);
    sensorMenu.setTicker(sensorTicker);
    
    connMenu = new List("Connection", Choice.IMPLICIT);
    connMenu.append("Direct", null);
    connMenu.append("Rem BT", null);
    connMenu.append("Rem RS485", null);
    connMenu.append("NXTMMX", null);
    connMenu.append("RCXMMX", null);
    connMenu.append("PFMATE", null);
    connMenu.append("IRLink", null);
    connMenu.append("NRLink", null);
    
    connMenu.addCommand(EXIT_COMMAND);
    connMenu.setCommandListener(this);
    connMenu.setTicker(connTicker);
    
    testMenu = new List("Test", Choice.IMPLICIT);
    testMenu.append("forward", null);
    testMenu.append("backward", null);
    testMenu.append("stop", null);
    testMenu.append("float", null);
    testMenu.addCommand(EXIT_COMMAND);
    testMenu.setCommandListener(this);
    testMenu.setTicker(testTicker);
    
    // Make the system active
    menu = typeMenu;
    display = Display.getDisplay();
    display.setCurrent(menu);
    display.show(true);
  }

  public void commandAction(Command c, Displayable d) {
    if (c.getCommandId() == CMDID_EXIT_APP) {
      System.exit(0);
    } else if (c.getCommandId() == 0) { //select
      if (menu == typeMenu) {
        menu = connMenu;
        motorType = typeMenu.getSelectedIndex();
        motorText = typeMenu.getString(motorType);
      } else if (menu == portMenu || menu == sensorMenu) {
        motorPort = menu.getSelectedIndex();
        portText = menu.getString(motorPort);
        menu = testMenu;
        testTicker.setString("Testing " + motorText + " " + portText + " " + connText);
       
        if (connType == 0) { // Direct
          MotorPort port = MotorPort.getInstance(motorPort);
          switch (motorType) {
            case 0: motor = new NXTMotor(port); break; // NXT
            case 1: motor = new RCXMotor(port); break; // RCX
            case 2: motor = new RCXMotor(port); break; // PF
          }
        } else if (connType == 1) { // Remote Bluetooth
          try {
            RemoteNXT nxt = new RemoteNXT("NXT",Bluetooth.getConnector());
            motor = nxt.A;
          } catch (IOException e) {}
        } else if (connType == 2) { // Remote RS485
          try {
            RemoteNXT nxt = new RemoteNXT("NXT",RS485.getConnector());
            motor = nxt.A;
          } catch (IOException e) {}
        } else if (connType == 3) { // NXTMMX
        	SensorPort port = SensorPort.getInstance(motorPort);
        	NXTMMX mmx = new NXTMMX(port);
        	motor = new MMXRegulatedMotor(mmx, NXTMMX.MMX_MOTOR_1);
        } else if (connType == 4) { // RCMMMX
          SensorPort port = SensorPort.getInstance(motorPort);
          RCXMotorMultiplexer mmx = new RCXMotorMultiplexer(port);
          motor = mmx.A;
        } else if (connType == 5) { //PFMate
          if (motorType != 2) return; // Only PF motors are supported via PFMate
          SensorPort port = SensorPort.getInstance(motorPort);
          PFMate pfm = new PFMate(port, 1); // use channel 1
          motor = pfm.A; // Use motor A
        } else if (connType == 6) { // IRLink
          if (motorType != 2) return; // Only PF motors are supported via IRLink
          SensorPort port = SensorPort.getInstance(motorPort);
          IRLink irl = new IRLink(port);
          PFMotorPort mp = new PFMotorPort(irl,0,0); // channel 1, slot 0
          motor = new RCXMotor(mp);
        } else if (connType == 7) { //NRLink
          if (motorType != 1) return; // Only RCX Motors are supported by NRLink
          SensorPort port = SensorPort.getInstance(motorPort);
          RCXLink link = new RCXLink(port);
          RCXRemoteMotorPort mp = new RCXRemoteMotorPort(link, 0);
          motor = new RCXMotor(mp);
        }
      } else if (menu == connMenu) {
        connType = connMenu.getSelectedIndex();
        menu = (connType < 2 ? portMenu : sensorMenu);
        connText = connMenu.getString(connType);
      } else if (menu == testMenu) {
        if (motor == null) return;
        int action = testMenu.getSelectedIndex();
        
        motor.setPower(60);
        switch (action) {
        case 0: motor.forward();break;
        case 1: motor.backward();break;
        case 2: motor.stop();break;
        case 3: motor.flt();break;
        }
      }
      display.setCurrent(menu);
    }
  }
}

