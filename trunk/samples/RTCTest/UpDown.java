import lejos.nxt.LCD;

public class UpDown {
	private int r,c,val,minVal,maxVal,nDigits;
	private boolean active;

	public UpDown(int x,int y,int v,int minV,int maxV,int nD) {
		r = x;
		c = y;
		val = v;
		minVal = minV;
		maxVal = maxV;
		nDigits = nD;
		active = false;
		LCD.drawString(""+val, r, c, false);
	}

	public void setActive(boolean act) {
		active = act;
		redraw();
	}

	public void setValue(int v) {
		val = v;
		redraw();
	}

	public void increment() {
		val++;
		if (val > maxVal) val = minVal;
		redraw();
	}

	public void decrement() {
		val--;
		if (val < minVal) val = maxVal;
		redraw();
	}

	public int getVal() {
		return val;
	}
	
	private void redraw() {
		LCD.clear(r, c, nDigits);
		LCD.drawString(""+val, r, c, active);		
	}
}

