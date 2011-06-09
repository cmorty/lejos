package net.mosen.nxt.instrumentation;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;

import lejos.util.Delay;

import net.mosen.nxt.BTManager;

public class TestLogger {
    public TestLogger() {
    }

    public static void main(String[] args) {
        TestLogger testLogger = new TestLogger();
        testLogger.doTest();
    }
    
    private void doTest(){
        NXTConnectionManager connectionManager = new NXTConnectionManager();
        LCD.drawString("Connection Wait.. ",0,1);
//        boolean success = connectionManager.waitForUSBConnection(15000);
        boolean success = connectionManager.waitForBTConnection(15000);
        LCD.drawString("                  ",0,1);
        if (!success) {
            LCD.drawString("IO error! ",0,1);
            LCD.drawString("Press ENT ",0,3, true);
            Button.ENTER.waitForPress();
            return;
        }
        
        NXTDataLogger dlog = new NXTDataLogger(connectionManager);
        dlog.setImmediateFlush(true);
        
        double value=0;
        dlog.setHeaders(new String[]{"sine(v)","testRandom"}); //,"c3","c4","c5","c6","c7","c8","c9","c10","c11","c12","c13"}); // TODO fix bug in cahrt that does display data with no headers defined

        for (int i=0;i<1000;i++){
            dlog.logDouble(Math.sin(value));
            dlog.logDouble((Math.random()*3)-1.5);
//            if (i%5==0) {
//                //dlog.flush();
//                Delay.msDelay(30);
//            }
            value+=.1f;
            //Delay.msDelay(10);
            doWait(5);
        }
        dlog.closeConnection();
        
//        Button.ENTER.waitForPress();
    }
    
     private void doWait(int sleepval) {
         try {
             Thread.sleep(sleepval);
         } catch (InterruptedException e) {
             // TODO
         }
     }
}
