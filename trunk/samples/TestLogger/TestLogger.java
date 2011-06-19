import lejos.nxt.Button;
import lejos.nxt.LCD;

import lejos.util.NXTDataLogger;


public class TestLogger {
    public TestLogger() {
    }

    public static void main(String[] args) {
        TestLogger testLogger = new TestLogger();
        testLogger.doTest();
    }
    
    private void doTest(){
        NXTDataLogger dlog = new NXTDataLogger(); 
        
//        boolean success=dlog.waitForConnection(15000,NXTDataLogger.CONN_USB);
        boolean success=dlog.waitForConnection(15000,NXTDataLogger.CONN_BLUETOOTH);
        if (!success) {
            LCD.drawString("IO error! ",0,3);
            LCD.drawString("Press ENT ",0,4, true);
            Button.ENTER.waitForPress();
            return;
        }
        
        double value=0;
        // TODO fix bug in LogChartPanel that does not display data with no headers defined
//        dlog.setHeaders(new String[]{"sine(v)","testRandom"}); 
        dlog.setHeaders(new String[]{"sine(v)","upper","lower"}); 

        for (int i=0;i<975;i++){ // 975
            dlog.logDouble(Math.sin(value));
//            dlog.logDouble((Math.random()*3)-1.5);
            dlog.logDouble(1);
            dlog.logDouble(-1);

            value+=.1f;
        }
        dlog.closeConnection();
    }
}
