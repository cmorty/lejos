
import lejos.nxt.*;
import lejos.nxt.addon.*;
import lejos.util.TextMenu;

/*
 * Emulates the RCX Remote control
 *
 * @author Lawrie Griffiths <lawrie.griffiths@ntlworld.com>
 * 
 * Bugs:
 *   Beep does not work twice in a row with standard RCX firmware.
 *   Commands only work every other time with RCX leJOS RemoteControlTest
 *   example (due to the RCX leJOS Remote Control system not
 *   handling the 0x08 toggle bit in RCX opcodes).
 */
public class RCXRemote {
	public static void main(String[] args) throws Exception {
		RCXLink link = new RCXLink(SensorPort.S1);
		String[] menuItems = 
			{"P1", "P2", "P3", "P4", "P5",
			 "Stop", "Beep",
			 "A fwd", "A bwd", "B fwd", 
			 "B bwd", "C fwd", "C bwd",
			 "Msg 1", "Msg 2", "Msg 3"};	
		TextMenu menu = new TextMenu(menuItems,1,"RCX Remote");		
		int menu_item;
		
		do {
			menu_item = menu.select();
			
			if (menu_item >= 0 && menu_item <= 4) {
				link.runProgram(menu_item + 1);
			}
			
			if (menu_item == 5) link.stopAllPrograms();
			if (menu_item == 6) link.beep();
					
			if (menu_item == 7) link.forwardStep(0);
			if (menu_item == 8) link.backwardStep(0);
			if (menu_item == 9) link.forwardStep(1);
			if (menu_item == 10) link.backwardStep(1);
			if (menu_item == 11) link.forwardStep(1);
			if (menu_item == 12) link.backwardStep(1);
			
			if (menu_item >=13 && menu_item <= 15) {
				link.sendRemoteCommand(1 << (menu_item - 13));
			}
			
		} while (menu_item >= 0);		
	}	
}
