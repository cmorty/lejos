package lejos.nxt.startup;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.TextMenu;

public class GraphicMenu extends TextMenu{
	private String _parent = null;

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
		display(selectedIndex, _topIndex);
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
			
			if(button == Button.ID_ENTER && selectedIndex >= 0 && selectedIndex < _length)
				return selectedIndex;
			if(button == Button.ID_ESCAPE)
				return -1; //Escape
			if(button == Button.ID_RIGHT)//scroll forward
			{
				int temp = selectedIndex;
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
					try {
						rightRotateAnimate(temp);
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(button == Button.ID_LEFT)//scroll backward
			{
				int temp = selectedIndex;
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
					try {
						leftRotateAnimate(temp);
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			display(selectedIndex, _topIndex);
		}
	}
	
	@Override
	protected void display(int selectedIndex, int topIndex)
	{
		if(_title != null)
			LCD.drawString(_title, 0, _topRow);
		if(_parent != null)
			LCD.bitBlt(Utils.stringToBytes(_parent), 16, 16, 0, 0, 41,58, 16, 16, LCD.ROP_COPY);
		//Prepare Index Locations
		int length = _items.length;
		int[] index = new int[5];
		for (int i = 0; i < 5; i++)
			index[i] = selectedIndex + (i-2);
		//boolean wrap = (length>=5);
		//Clear Icon Area
		LCD.bitBlt(new byte[396], 99, 25, 0, 0, 0, 40, 99, 25, LCD.ROP_COPY);
		//if (((index[0] < 0) && wrap) || index[0] >= 0) // Left Most Icon
		if (index[0] >= 0)	
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[0]<0)?length+index[0]:index[0])]),16,16,0,0,2,51,16,16,LCD.ROP_COPY);
		//if (((index[1] < 0) && wrap) || index[1] >= 0) // Left Icon
		if (index[1] >= 0)	
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[1]<0)?length+index[1]:index[1])]), 16,16, 0, 0, 21, 45, 16,16, LCD.ROP_COPY);
		//Middle Icon
			LCD.bitBlt(Utils.stringToBytes(_icons[index[2]]), 16,16, 0, 0, 41, 41, 16,16, LCD.ROP_COPY);
		//if (((index[3] >= length) && wrap) || index[3] < length) // Right Icon
		if (index[3] < length)	
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[3]>=length)?index[3]-length:index[3])]), 16,16, 0, 0, 61, 45, 16,16, LCD.ROP_COPY);
		//if (((index[4] >= length) && wrap) || index[4] < length) // Right Most Icon
		if (index[4] < length)	
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[4]>=length)?index[4]-length:index[4])]), 16,16, 0,0, 80,51, 16,16, LCD.ROP_COPY);
		
		// Draw Label
		LCD.drawString(BLANK, 0, 4);
		if (_items[index[2]].length()>16)
			LCD.drawString(_items[index[2]],0, 4);
		else
			LCD.drawString(_items[index[2]], 8-(_items[index[2]].length()/2), 4);
		
		LCD.asyncRefresh();
	}
	
	/**
	 * GraphicMenu helper method to animate a right rotate animation.
	 * @param start
	 * @param end
	 */
	private void rightRotateAnimate(int start){
		int[] index = new int[5];
		int length = _items.length;
		for (int i = 0; i < 5; i++)
			index[i] = start + (i-2);
		//boolean wrap = (length>=5);
		//Clear Icon Area
		LCD.bitBlt(new byte[396], 99, 25, 0, 0, 0, 40, 99, 25, LCD.ROP_COPY);
		//if (((index[0] < 0) && wrap) || index[0] >= 0) // Left Most Icon
		if (index[0] >= 0)
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[0]<0)?length+index[0]:index[0])]),16,16,0,0,-10,54,16,16,LCD.ROP_COPY);
		//if (((index[1] < 0) && wrap) || index[1] >= 0) // Left Icon
		if (index[1] >= 0)	
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[1]<0)?length+index[1]:index[1])]), 16,16, 0, 0, 11, 47, 16,16, LCD.ROP_COPY);
		//Middle Icon
			LCD.bitBlt(Utils.stringToBytes(_icons[index[2]]), 16,16, 0, 0, 31, 42, 16,16, LCD.ROP_COPY);
		//if (((index[3] >= length) && wrap) || index[3] < length) // Right Icon
		if (index[3] < length)	
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[3]>=length)?index[3]-length:index[3])]), 16,16, 0, 0, 51, 42, 16,16, LCD.ROP_COPY);
		//if (((index[4] >= length) && wrap) || index[4] < length) // Right Most Icon
		if (index[4] < length)	
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[4]>=length)?index[4]-length:index[4])]), 16,16, 0,0, 71,47, 16,16, LCD.ROP_COPY);
		LCD.asyncRefresh();
	}
	private void leftRotateAnimate(int start){
		int[] index = new int[5];
		int length = _items.length;
		for (int i = 0; i < 5; i++)
			index[i] = start + (i-2);
		//boolean wrap = (length>=5);
		//Clear Icon Area
		LCD.bitBlt(new byte[396], 99, 25, 0, 0, 0, 40, 99, 25, LCD.ROP_COPY);
		//if (((index[0] < 0) && wrap) || index[0] >= 0) // Left Most Icon
		if (index[0] >= 0)	
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[0]<0)?length+index[0]:index[0])]), 16,16, 0, 0, 11, 47, 16,16, LCD.ROP_COPY);
		//if (((index[1] < 0) && wrap) || index[1] >= 0) // Left Icon
		if (index[1] >= 0)	
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[1]<0)?length+index[1]:index[1])]), 16,16, 0, 0, 31, 42, 16,16, LCD.ROP_COPY);
		//Middle Icon
			LCD.bitBlt(Utils.stringToBytes(_icons[index[2]]), 16,16, 0, 0, 51, 42, 16,16, LCD.ROP_COPY);
		//if (((index[3] >= length) && wrap) || index[3] < length) // Right Icon
		if (index[3] < length)	
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[3]>=length)?index[3]-length:index[3])]), 16,16, 0,0, 71,47, 16,16, LCD.ROP_COPY);
		//if (((index[4] >= length) && wrap) || index[4] < length) // Right Most Icon
		if (index[4] < length)	
			LCD.bitBlt(Utils.stringToBytes(_icons[((index[4]>=length)?index[4]-length:index[4])]), 16,16, 0,0, 90,54, 16,16, LCD.ROP_COPY);
		LCD.asyncRefresh();
	}

	public void setParentIcon(String str) {
		_parent = str;
	}
}