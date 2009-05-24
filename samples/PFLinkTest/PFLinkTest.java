import lejos.nxt.*; 
import lejos.nxt.addon.PFLink; 

/** 
 * Test class for PFLink, used to remote-control the lego bulldozer 
 */ 
public class PFLinkTest { 

    private static final void sleep(int milli) { 
        try { 
            Thread.sleep(milli); 
        } catch (InterruptedException e) { 
        } 
    } 

    public static void main(String[] _Args) { 

        PFLink link = new PFLink(SensorPort.S1); 

        LCD.drawString(link.getVersion(), 0, 0); 
        LCD.drawString(link.getProductID(), 0, 1); 
        LCD.drawString(link.getSensorType(), 0, 2); 
        
        link.initialize(PFLink.NR_RANGE_LONG); 

        //Needs only to be called the first time to update the NRLink EEPROM 
        //Don't call this too often 
        //link.installDefaultMacros(); 
        //link.initialize(PFLink.NR_RANGE_SHORT); 

        while (!Button.ESCAPE.isPressed()) { 

            LCD.drawString("FORWARD         ", 0, 3); 
            link.runMacro(PFLink.COMBO_CH1_A_FORWARD_B_REVERSE); 
            sleep(1000); 

            LCD.drawString("BACK         ", 0, 3); 
            link.runMacro(PFLink.COMBO_CH1_A_REVERSE_B_FORWARD); 
            sleep(1000); 

            LCD.drawString("ROTATE RIGHT     ", 0, 3); 
            link.runMacro(PFLink.COMBO_CH1_A_FORWARD_B_FORWARD); 
            sleep(2000); 

            LCD.drawString("ROTATE_LEFT       ", 0, 3); 
            link.runMacro(PFLink.COMBO_CH1_A_REVERSE_B_REVERSE); 
            sleep(2000); 
        } 
    } 
} 

