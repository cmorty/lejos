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
		//LCD.bitBlt(r, c, nDigits*LCD.CELL_WIDTH, LCD.CELL_HEIGHT, LCD.ROP_CLEAR);
		LCD.drawString(""+val, r, c, active);
	}

	public void setValue(int v) {
		val = v;
		//LCD.bitBlt(r, c, nDigits*LCD.CELL_WIDTH, LCD.CELL_HEIGHT, LCD.ROP_CLEAR);
		LCD.drawString(""+val, r, c, active);
	}

	public void increment() {
		val++;
		if (val > maxVal)
			val = minVal;

		//LCD.bitBlt(r, c, nDigits*LCD.CELL_WIDTH, LCD.CELL_HEIGHT, LCD.ROP_CLEAR);
		LCD.drawString(""+val, r, c, active);
	}

	public void decrement() {
		val--;
		if (val < minVal)
			val = maxVal;
		
		//LCD.bitBlt(r, c, nDigits*LCD.CELL_WIDTH, LCD.CELL_HEIGHT, LCD.ROP_CLEAR);
		LCD.drawString(""+val, r, c, active);
	}

	public int getVal() {
		return val;
	}
}

