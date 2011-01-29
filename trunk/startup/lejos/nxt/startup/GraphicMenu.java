package lejos.nxt.startup;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.TextMenu;

public class GraphicMenu extends TextMenu{
	private static final byte xArea = 0; // x of Menu Area
	//private static final byte yArea = 40; // y of Menu Area
	private static final byte xWidth = 20; // Distance between icon IDs on x axis
	private static final byte yWidth = 4; // " on y axis
	private static final byte xOffset = 2;
	private static final byte yOffset = 0;
	
	private static final int interval = 40;
	private static final int tickCount = 5;
	
	private String _parent = null;
	
	private int labelLine = 4;
	private int yArea = 40;

	private String[] _icons;
	
	/**
	 * This constructor sets location of the top row of the item list to row 0 of the display.
	 */
	public GraphicMenu( String[] items,String[] icons)
	{
		this(items,icons, null);
	}
	
	/**
	 * This constuctor allows the specfication of a title (of up to 16 characters) and the location of the item list <br>
	 * The title is displayed in the row above the item list.
	 * @param items  -  string array containing the menu items. No items beyond the first null will be displayed.
	 */	
	public GraphicMenu(String[] items,String[] icons, String title)
	{
		super(items,1,title);
		int topRow = 1;
		if (topRow < 0 || (topRow == 0 && title != null))
			throw new IllegalArgumentException("illegal topRow argument");
		
		_topRow = topRow;
		setTitle(title);
		this.setItems(items,icons);
	}
	
	public void setYLocation (int charLine){
		labelLine = charLine;
		yArea = (charLine+1)*8;
	}
	
	/**
	 * set the array of items to be displayed
	 * @param items
	 */
	public void setItems(String[] items,String[] icons)
	{
		_items = items;
		_icons = icons;
		
		if (items == null)
			_length = 0;
		else
		{
			int i = 0;
			while(i < items.length && items[i] != null)
				i++;
			_length = i;
		}
		_height = 8 - _topRow;
		if(_height > _length)
			_height = _length;		
	}
	
	@Override
	public int select(int selectedIndex, int timeout) 
	{ 
		if (selectedIndex >= _length)
			//might result in -1
			selectedIndex = _length -1;
		if (selectedIndex < 0)
			selectedIndex = 0;
		
//		if (_length<_size) _size = _length;
		_quit = false;
		resetTimeout();
//		LCD.clear();
		if (_topIndex > selectedIndex)
			_topIndex = selectedIndex;
		if (_topIndex > _length - _height)
			_topIndex = _length - _height;			
		display(selectedIndex, 0,0);
		int buttons = Button.readButtons();
		while(true)
		{
			int button;
			do
			{				
				if (_quit)
					return -2; // quit by another thread
				
				if (timeout > 0 && System.currentTimeMillis() - _startTime >= timeout) 
					return -3; // timeout
				
				Thread.yield();
				int buttons2 = Button.readButtons();
				button = (buttons2 & ~buttons);				
				buttons = buttons2;
			} while (button == 0);
			
			if(button == Button.ID_ENTER && selectedIndex >= 0 && selectedIndex < _length){
				clearArea();
				return selectedIndex;
			}
			if(button == Button.ID_ESCAPE)
				return -1; //Escape
			int temp = selectedIndex;
			int dir = 0;
			if(button == Button.ID_RIGHT)//scroll forward
			{
				selectedIndex++;
				// check for index out of bounds
				if(selectedIndex >= _length)
				{
					//selectedIndex = 0;
					selectedIndex--;
					//_topIndex = 0;
				}
				//else if(selectedIndex >= _topIndex + _height)
				//{
				//	_topIndex = selectedIndex - _height + 1;
				//}
				else{
					dir = -1;
				}
			}
			if(button == Button.ID_LEFT)//scroll backward
			{
				selectedIndex --;
				// check for index out of bounds
				if(selectedIndex < 0)
				{
					//selectedIndex  = _length - 1;
					selectedIndex++;
					//_topIndex = _length - _height;
				}
				//else if(selectedIndex < _topIndex)
				//{
				//	_topIndex = selectedIndex;
				//}
				else{
					dir = 1;
				}
			}
			animate(temp,selectedIndex,dir);
		}
	}
	
	private void animate(int selectedIndex, int finalIndex,int animateDirection){
		int count = 1;
		while (count < tickCount){
			display(selectedIndex,animateDirection,(int) ((10.0/tickCount)*count));
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count++;
		}
		display(finalIndex,0,0);
	}
	
	/**
	 * Displays the Graphic Menu at the index provided with the animation details provided.
	 * @param selectedIndex
	 * @param animateDirection #-1 to 1 (-1=Left 0=None 1=Right)
	 * @param tick #0-10 (0 = No change 10 = Full move)
	 */
	protected void display(int selectedIndex, int animateDirection, int tick)
	{
		if(_title != null)
			LCD.drawString(_title, 0, _topRow);
		clearArea();
		if(_parent != null)
			LCD.bitBlt(Utils.stringToBytes(_parent), 16, 16, 0, 0, xArea+41,yArea+18, 16, 16, LCD.ROP_COPY);
		//Prepare Index Locations
		int length = _items.length;
		int[] index = new int[5];
		for (int i = 0; i < 5; i++)
			index[i] = selectedIndex + (i-2);
		//boolean wrap = (length>=5);
		//Clear Icon Area
		//if (((index[0] < 0) && wrap) || index[0] >= 0) // Left Most Icon
		if (index[0] >= 0)	
			drawIconAtTick(_icons[((index[0]<0)?length+index[0]:index[0])],0,0+animateDirection,tick);
		//if (((index[1] < 0) && wrap) || index[1] >= 0) // Left Icon
		if (index[1] >= 0)	
			drawIconAtTick(_icons[((index[1]<0)?length+index[1]:index[1])],1,1+animateDirection,tick);
		//Middle Icon
			drawIconAtTick(_icons[index[2]],2,2+animateDirection,tick);
		//if (((index[3] >= length) && wrap) || index[3] < length) // Rightd Icon
		if (index[3] < length)	
			drawIconAtTick(_icons[((index[3]>=length)?index[3]-length:index[3])],3,3+animateDirection,tick);
		//if (((index[4] >= length) && wrap) || index[4] < length) // Right Most Icon
		if (index[4] < length)	
			drawIconAtTick(_icons[((index[4]>=length)?index[4]-length:index[4])],4,4+animateDirection,tick);
		// Draw Label
		LCD.drawString(BLANK, 0, labelLine);
		if (_items[index[2]].length()>16)
			LCD.drawString(_items[index[2]],0, labelLine);
		else
			LCD.drawString(_items[index[2]], 8-(_items[index[2]].length()/2), labelLine);
		
		LCD.asyncRefresh();
	}
	
	public void clearArea(){
		LCD.bitBlt(new byte[396], 99, 25, 0, 0, 0, yArea, 99, 25, LCD.ROP_COPY);
	}
	
	/**
	 * Helper method to draw a menu icon at a variable location (determined by tick) between two icon positions.
	 * @param sID -1 to 6
	 * @param eID -1 to 6
	 * @param tick #0-10
	 */
	private void drawIconAtTick(String icon,int sID, int eID,int tick){
		// Determine sID Coordinates
		int fx = xArea + xOffset+(sID*xWidth);
		int fy = yArea + yOffset+(Math.abs(sID-2)*yWidth);
		// Determine eID Coordinates
		int sx = xArea + xOffset+(eID*xWidth);
		int sy = yArea + yOffset+(Math.abs(eID-2)*yWidth);
		// Determine Icon Offset from sID
		int ix = (int) (((sx-fx)/10.0)*tick);
		int iy = (int) (((sy-fy)/10.0)*tick);
		// Paint Icon
		LCD.bitBlt(Utils.stringToBytes(icon), 16,16, 0,0, fx+ix,fy+iy, 16,16, LCD.ROP_COPY);
	}

	public void setParentIcon(String str) {
		_parent = str;
	}
}