package org.lejos.sample.rcxtemptest;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.RCXTemperatureSensor;

/**
 * Demo for RCXTemperatureSensor
 *  
 */

public class RCXTempTest {
    public static void main(String[] args) throws Exception {
        RCXTemperatureSensor temperature = new RCXTemperatureSensor(SensorPort.S1);
        boolean s = true;
        while(!Button.ESCAPE.isDown()) {
            LCD.clear();
            if (s) 
                LCD.drawString( "C " + temperature.getCelcius(), 0, 0);
            else
                LCD.drawString( "F " + temperature.getFahrenheit(), 0, 0);
            s = !s;
            LCD.refresh();
            Thread.sleep(2000);
        }
        
    }
}
