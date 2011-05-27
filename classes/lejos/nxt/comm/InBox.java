package lejos.nxt.comm;

import java.util.Queue;

/**
 * Represents an LCP Inbox
 * 
 * @author Lawrie Griffiths
 *
 */
public class InBox extends Queue<String> {
	
	// Allow a message in the queue to be updated, or added 
	public synchronized void updateMessage(String key, String msg) {
		for (int i=0;i<elementCount;i++) {
			String s = elementAt(i);
			if (s.startsWith(key)) {
				setElementAt(msg, i);
				return;
			}
		}
		push(msg);
	}
}
