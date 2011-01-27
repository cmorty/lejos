package lejos.nxt.startup;

import lejos.nxt.Button;
import lejos.nxt.LCD;

public class GraphicMenu{
	static final int BLUETOOTH = 0;
	static final int PROGRAM = 1;
	static final int FILES = 2;
	static final int SOUND = 3;
	static final int SYSTEM = 4;
	static final int VERSION = 5;
	static final int POWER = 6;
	static final int VISIBILITY = 7;
	static final int SEARCH = 8;
	static final int PIN = 9;
	
	String[] icons = {"\0\0\0\u00f0\u00fc\u009e\u003e\u007f\u0007\u00cf\u001f\u003e\u00fe\u00fc\u00f0\0\0\0\0\0\0\u003f\u00ff\u00e7\u00f3\u00f8\u0080\u00cc\u00e0\u00f3\u00ff\u00ff\u003f\0\0\0\0\0\0\0\0\u0001\u0001\u0003\u0003\u0003\u0003\u0001\u0001\0\0\0\0\0",
			"\0\u00fc\u0002\u0002\u0002\u0002\u0002\u00c2\u0032\u008a\u0062\u0002\u0002\u0002\u0002\u0002\u00fc\0\0\u00ff\0\0\u0020\"\u002a\u002a\u002b\u002a\"\u0028\n\u0006\u0000\u0000\u00ff\0\0\0\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\0\0",
			"\0\u00e0\u00d0\u00d0\u00b0\u0090\u0088\u0084\u0002\u0004\u000c\u0014\u0024\u0044\u0094\u00a8\u00f0\0\0\u00ff\0\0\0\0\0\0\u0001\u0002\u0002\u0002\u008a\u0056\u00aa\u0057\u00ff\0\0\0\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\0\0",
			"\0\0\u00c0\u0040\u0040\u00e0\u0010\u0008\u00fc\0\u0020\u00c8\u0010\u00e4\u0008\u00f0\0\0\0\0\u000f\n\u000c\u001f\u0034\u0068\u00ff\0\u0010\u004f\u0020\u009f\u0040\u003f\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0",
			"\0\0\0\u00fe\u0005\u00f7\u0057\u0095\u0017\u0017\u0015\u0017\u00f7\u0005\u00fe\0\0\0\0\0\0\u00ff\u00a0\u00cb\u009b\u00c2\u00bb\u00ba\u0083\u009a\u008b\u0080\u00ff\0\0\0\0\0\0\u0001\u0002\u0003\u0003\u0002\u0003\u0003\u0002\u0003\u0003\u0002\u0001\0\0\0",
			"\0\0\0\0\0\0\0\u0080\u0040\u0080\u004f\u000f\u00ff\u00fe\u00fe\u00fc\0\0\0\0\u007c\u00fc\u00fc\u00fc\u00e0\u00e2\u00e1\u00e2\u00e1\u00e0\u00ff\u00ff\u00ff\u007f\0\0\0\0\0\0\0\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\0\0\0\0\0",
			"\0\u00c0\u00f0\u0078\u001c\u000c\0\0\u00ff\u00ff\0\0\u000c\u001c\u0078\u00f0\u00c0\0\0\u000f\u003f\u0078\u00e0\u00c0\u00c0\u0080\u0081\u0081\u0080\u00c0\u00c0\u00e0\u0078\u003f\u000f\0\0\0\0\0\0\0\u0001\u0001\u0001\u0001\u0001\u0001\0\0\0\0\0\0",
			"\0\0\0\0\0\u0010\u0090\u00b0\u00b0\u00b0\u0060\u0060\u00e0\u00e0\u00c0\u0080\0\0\0\0\u0004\u000e\u0013\u0027\u002d\u002f\u002f\u0023\u0011\u0013\u0016\u000c\u0001\u0001\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0",
			"\0\0\u00e0\u0018\u0008\u0004\u0004\u0004\u0004\u0004\u0008\u0018\u00e0\0\0\0\0\0\0\0\u0003\u000c\u0008\u0012\u0014\u0014\u0014\u0012\u0018\u003c\u0067\u00c8\u00d0\u00e0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0",
			"\0\0\0\0\u00d8\u0024\u0024\u00d8\u0024\u0024\u00d8\u0024\u0024\u00d8\0\0\0\0\0\0\0\u007c\u0016\u001d\u0001\u0046\u007d\u0045\u0002\u007d\u0019\"\u007c\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0"
	};
	private boolean dispTop = true;
	
	/**
	 *location of the top row of the list; set by constructor, used by display()
	 */
	protected int _topRow = 0;
	
	/** 
	 * number of rows displayed; set by constructor, used by display()
	 */
	protected int _height = 8;
	
	/**
	 *optional menu title displayed immediately above the list of items
	 */
	protected String _title;
	private int _parent = -1;
	
	/**
	 *array of items to be displayed ;set by constructor, used by select();
	 */
	private String[] _items;
	private int[] _icons;
	
	/**
	 * effective length of items array  - number of items before null 
	 */
	protected int _length;
	
	/**
	 * index of the list item at the top of the list; set by constructor, used by select()
 	 **/
	protected int _topIndex = 0;  

	/**
	 * a blank line
	 */
	public static final String BLANK = "                ";
	
	/**
	 * boolean to cause select to quit 
	 */
	protected boolean _quit = false;
	
	/**
	 * start time for select()
	 */
	protected int _startTime;
	
	/**
	 * This constructor sets location of the top row of the item list to row 0 of the display.
	 */
	public GraphicMenu( String[] items,int[] icons)
	{
		this(items,icons, null);
	}
	
	/**
	 * This constuctor allows the specfication of a title (of up to 16 characters) and the location of the item list <br>
	 * The title is displayed in the row above the item list.
	 * @param items  -  string array containing the menu items. No items beyond the first null will be displayed.
	 */	
	public GraphicMenu(String[] items,int[] icons, String title)
	{
		int topRow = 1;
		if (topRow < 0 || (topRow == 0 && title != null))
			throw new IllegalArgumentException("illegal topRow argument");
		
		_topRow = topRow;
		setTitle(title);
		this.setItems(items,icons);
	}
	
	/**
	 * set menu title. 
	 * @param title  the new title
	 */
	public void setTitle(String title) 
	{
		_title = title;
		if(_topRow <= 0)
			_topRow = 1;		
		_height = 8 - _topRow;
		if(_height > _length)
			_height = _length;
	}
	
	/**
	 * set the array of items to be displayed
	 * @param items
	 */
	public void setItems(String[] items,int[] icons)
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
	
	/**
	 * Allows the user to scroll through the items, using the right and left buttons (forward and back)  The Enter key closes the menu <br>
	 * and returns the index of the selected item. <br>
	 * The menu display wraps items that scroll off the top will reappear on the bottom and vice versa.
	 * 
	 * The selectedIndex is set to the first menu item.
	 * 
	 * @return the index of the selected item
	 **/
	public int select() 
	{ 
	   return select(0,0); 
	} 
	
	/**
	 * Version of select without timeout
	 */
	public int select(int selectedIndex) {
		return select(selectedIndex, 0);
	}

	/**
	 * Allows the user to scroll through the items, using the right and left buttons (forward and back)  The Enter key closes the menu <br>
	 * and returns the index of the selected item. <br>
	 * The menu display wraps items that scroll off the top will reappear on the bottom and vice versa.
	 * 
	 * This version of select allows the selected index to be set when the menu is first displayed.
	 * 
	 * @param selectedIndex the index to start the menu on
	 * @return the index of the selected item
	 **/
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
		display(selectedIndex);
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
				selectedIndex++;
				// check for index out of bounds
				if(selectedIndex >= _length)
				{
					selectedIndex = 0;
					_topIndex = 0;
				}
				else if(selectedIndex >= _topIndex + _height)
				{
					_topIndex = selectedIndex - _height + 1;
				}
			}
			if(button == Button.ID_LEFT)//scroll backward
			{
				selectedIndex --;
				// check for index out of bounds
				if(selectedIndex < 0)
				{
					selectedIndex  = _length - 1;
					_topIndex = _length - _height;
				}
				else if(selectedIndex < _topIndex)
				{
					_topIndex = selectedIndex;
				}
			}
			display(selectedIndex);
		}
	}
	
	/**
	 * method to call from another thread to quit the menu
	 */
    public void quit()
    {
    	_quit = true;
    }
	
	/**
	 * helper method used by select()
	 */
	protected  void display(int selectedIndex)
	{
		if(_title != null)
			LCD.drawString(_title, 0, _topRow - 1);
		if(_parent != -1)
			LCD.bitBlt(Utils.stringToBytes(icons[_parent]), 18, 18, 0, 0, -9, 26, 18, 18, LCD.ROP_COPY);
		int top = (selectedIndex <= 0)?_length-1:selectedIndex-1;
		int mid = selectedIndex;
		int bot = (selectedIndex >= _length-1)?0:selectedIndex+1;
		if (_length > 1 && dispTop){
			LCD.drawString(BLANK, 4, 2);
			LCD.drawString(_items[top], 4, 2);
		}
		LCD.drawString(BLANK, 5, 4);
		LCD.drawString(_items[mid], 5, 4);
		LCD.drawString(BLANK, 4, 6);
		if (bot != top && _length > 1)
			LCD.drawString(_items[bot], 4, 6);
		if (_length > 1 && dispTop)
			LCD.bitBlt(Utils.stringToBytes(icons[_icons[top]]), 18, 18, 0, 0, 3, 8, 18, 18, LCD.ROP_COPY);
		LCD.bitBlt(Utils.stringToBytes(icons[_icons[mid]]), 18, 18, 0, 0, 10, 26, 18, 18, LCD.ROP_COPY);
		if (bot != top && _length > 1)
			LCD.bitBlt(Utils.stringToBytes(icons[_icons[bot]]), 18, 18, 0, 0, 3, 44, 18, 18, LCD.ROP_COPY);
		LCD.asyncRefresh();
	}
	
	/**
	 * Returns list of items in this menu; 
	 * @return the array of item names
	 */
	public String[] getItems()
	{
	   return _items;
	}
	
	/**
	 * Reset the timeout period.
	 */
	public void resetTimeout() {
		_startTime = (int) System.currentTimeMillis();
	}	
	
	public void displayTopRow(boolean disp){
		dispTop = disp;
	}
	public static void main (String[] args){
		String[] items = {"Run Default","Files","Bluetooth","Sound","System","Version"};
		int[] icons = {PROGRAM,FILES,BLUETOOTH,SOUND,SYSTEM,VERSION};
		GraphicMenu menu = new GraphicMenu(items,icons);
		menu.setParentIcon(1);
		menu.select();
	}

	public void setParentIcon(int i) {
		_parent = i;
	}
}