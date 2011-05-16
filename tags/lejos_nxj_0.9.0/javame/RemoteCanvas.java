import javax.microedition.lcdui.*;

public class RemoteCanvas extends Canvas {
// !! Could implement CommandListener if adding Back or Exit command to Midlet
	
	LCP myNXT;
	private final int motorPower = 80;
	private String message = "Use arrow or number keys";
	
	public RemoteCanvas(String name) {
		myNXT = new LCP(name);
		myNXT.connect(); // !! Changed from thread starting.
	}
	
	protected void paint(Graphics g) {
		g.setColor(224,229,233); // Lt. Gray
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    g.setColor(204,88,39); // Dk. Orange
	    g.drawString(message, 10, 10, Graphics.TOP|Graphics.LEFT);
	}

	private void controlRobot(int gameKey){
		switch (gameKey) {
			case Canvas.UP:
				myNXT.setOutputState(1,-motorPower,0,0);
				myNXT.setOutputState(2,-motorPower,0,0);
				break;
			case Canvas.DOWN:
				myNXT.setOutputState(1,motorPower,0,0);
				myNXT.setOutputState(2,motorPower,0,0);
				break;
			case Canvas.RIGHT:
				myNXT.setOutputState(1,-motorPower,0,0);
				myNXT.setOutputState(2,motorPower,0,0);
				break;
			case Canvas.LEFT:
				myNXT.setOutputState(1,motorPower,0,0);
				myNXT.setOutputState(2,-motorPower,0,0);
				break;
		}
	}
	
	protected void keyPressed(int key) {
		int gameKey = getGameAction(key);
		message = "Game key code: " + gameKey;
		this.repaint();
		controlRobot(gameKey);
	}
	
	protected void keyReleased(int key){
		myNXT.setOutputState(1, 0, 0, 0);
		myNXT.setOutputState(2, 0, 0, 0);
	}
}
