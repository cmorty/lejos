import java.util.*;

/**
 * This test has been designed to test the class
 * String Tokenizer which is used in GPS Class
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class STTest{
	private static DebugMessages dm;
		
	public static void main(String args[]) throws Exception{
		dm = new DebugMessages();
		dm.setLCDLines(7);
		dm.setDelay(500);
		dm.setDelayEnabled(true);

		dm.echo("Testing");
		dm.echo("StringTokenizer");
		try {Thread.sleep(1000);} catch (Exception e) {}
		dm.clear();
		Vector wordList = new Vector();
		String message="$GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47";
		StringTokenizer st = new StringTokenizer(message,",");
		while (st.hasMoreTokens()) // make sure there is stuff to get
		{
			dm.echo(st.nextToken());
		}

		dm.clear();
		credits();
	}
	
	private static void credits(){
		dm.echo("");
		dm.echo("LEGO Mindstorms");
		dm.echo("NXT Robots  ");
		dm.echo("run better with");
		dm.echo("Java leJOS");
		dm.echo("");
		dm.echo("www.lejos.org");
		try {Thread.sleep(20000);} catch (Exception e) {}
	}
}