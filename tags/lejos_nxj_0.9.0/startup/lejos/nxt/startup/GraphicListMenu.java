package lejos.nxt.startup;

import lejos.nxt.LCD;

/**
 * Abrams version of a more detailed GraphicMenu for the file menu.
 * 
 * @author Abram Early
 *
 */
public class GraphicListMenu extends GraphicMenu {
	
	public GraphicListMenu(String[] items, String[] icons) {
		super(items, icons, -1);
	}
	
	@Override
	protected void animate(int selectedIndex, int finalIndex, int animateDirection)
	{
		this.display(finalIndex, animateDirection, 0);
	}
	
	@Override
	protected void display(int selectedIndex, int animateDirection, int tick)
	{
		//LCD.asyncRefreshWait();
		if(_title != null)
			LCD.drawString(_title, 0, _topRow - 1);
		int max = _topRow + _height;
		for (int i = _topRow; i < max; i++){
			LCD.drawString(BLANK, 0, i);
			int idx = i - _topRow + _topIndex;
			if (idx >= 0 && idx < _length){
				LCD.bitBlt(_icons[idx], 7, 8, 0, 0, 6, 8*i, 7, 8, LCD.ROP_COPY);
				LCD.drawChar(idx == selectedIndex ? SEL_CHAR : ' ', 0, i);
				LCD.drawString(_items[idx], 3, i);
			}
		}
		LCD.asyncRefresh();
	}
	@Override
	public void clearArea(){
		LCD.bitBlt(null, 30, 100, 0, 0, 0, 16, 30, 100, LCD.ROP_CLEAR);
	}
	
	@Override
	protected int getIconSize(){return 7;}// Returns the icon size (In Bytes)
	
	@Override
	protected boolean get2IconMode(){return true;}// Wrap With 2 Icons
}
