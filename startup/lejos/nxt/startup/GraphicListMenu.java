package lejos.nxt.startup;

import lejos.nxt.LCD;

/**
 * Abrams version of a more detailed GraphicMenu for the file menu.
 * @author Legoabram
 *
 */
public class GraphicListMenu extends GraphicMenu {
	
	public GraphicListMenu(String[] items, String[] icons) {
		super(items, icons, -1);
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
		LCD.bitBlt(new byte[400], 30, 100, 0, 0, 0, 16, 30, 100, LCD.ROP_COPY);
	}
	
	@Override
	protected int getIconSize(){return 7;}
}
