package lejos.nxt;

/**
 *Displays a list of items.  The select() method allows the user to scroll the list using the right and left keys to scroll forward and backward 
 * through the list. The number maximum number of rows, and an optional title can be specified.
 * @author Roger Glassey   Feb 20, 2007
 */

public class TextMenu  // implements Menu 
{
	/**
	 * index of the list item at the top of the menu; set by constructor, used by select()
 	 **/
	private int _topIndex = 0;  
	
	/** 
	 *	index of the currently selected item; updated by select() used by display();
	 */
	private int _selectedIndex = 0;
	
	/** 
	 *	maximum number of rows displayed; set by constructor, used by display()
	 */
	private int _size = 8;
	
	/**
	 *location of the top row of the menu; set by constructor, used by display()
	 */
	private int _topRow = 0;
	
	/**
	 *array of items to be displayed ;set by constructor, used by select();
	 */
	private String [] _items;
	
	/**
 	 *identifies the currently selected item
 	 */
	private String _selChar = ">";
	
	/**
	 * a blank line
	 */
	public static String blank = "                ";
	
	/**
	 *optional menu title displayed in line 0
	 */
	private String _title;
	
	/**
	 * This constructor sets the menu size to 8 rows, the top linw is in display row 0
	 */
	public TextMenu( String[] items)
	{
		_items = items;
	}
	
	/**
	 * This constructor allows specification of the size and top row of the menu.
	 */
	public TextMenu( String[] items, int size, int topRow)
	{
		this(items);
		_topRow = topRow;
		_size = size;
	}
	
	/**
	 * This constuctor allows the specfication of a title (of up to 16 characters) displayed in row 0, and the size of the menu. <br>
	 * The top row of the menu itself is row 1 of the display
	 * @param items  -  string array containing the menu items.  Null strings will produce a blank line in the display.
	 */	
	public TextMenu(String[] items, int size, String title)
	{
		this(items,size,1);
		_title = title;
	}
	
	/**
	 * Allows the to scroll through the items, using the right and left buttons. The Enter key closes the menu <br>
	 * and returns the index of the selected item. <br>
	 * The menu display wraps items that scroll off the top will reappear on the bottom and vice versa.

	 * @return the index of the selected item
	 **/
	public int select()
	{
		if (_items.length<_size) _size = _items.length;
		int button = 0;
		display();
		while(true)
		{
			while(Button.readButtons()>0)Thread.yield();// wait for release
			while(Button.readButtons()==0) Thread.yield();
			try {Thread.sleep(20);} catch (InterruptedException ie) {} // wait to stabilize
			button=Button.readButtons();
			
			if(button == 1) return _selectedIndex;
			if(button == 8) return -1; //Escape
			if(button == 4)//scroll forward
			{
				_selectedIndex ++;
				if(_selectedIndex >= _items.length) _selectedIndex  -= _items.length;				
				int diff = _selectedIndex - _topIndex;
				if(diff  < 0 || diff >= _size)  _topIndex = _selectedIndex +1 - _size;
			}
			if(button == 2)//scroll backward
			{
				_selectedIndex --;
				if(_selectedIndex<0) _selectedIndex  += _items.length;
				int diff = _selectedIndex - _topIndex;
				if(diff  < 0 || diff >= _size)  _topIndex = _selectedIndex;
			}
			display();
		}
	}
	
	/**
	 * helper method used by select()
	 */
	private  void display()
	{
		if(_title != null)LCD.drawString(_title,0,0);
		for (int i = 0;i<_size;i++)
		{
			LCD.drawString(blank,0,i + _topRow);
			int indx = index(i);
			if(_items[indx] !=null) LCD.drawString(_items[indx],1,i + _topRow);
		}
		LCD.drawString(_selChar,0,_selectedIndex-_topIndex + _topRow);
		LCD.refresh();
	}
	
	/**
	 * helper method used by display() to caluclate the index in the items array corresponding to a row of the 
	 * menu display
	 */
	private int index(int row)
	{
		return (_topIndex + row + _items.length)%_items.length;
	}
	
}

