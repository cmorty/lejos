package lejos.nxt;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 *Displays a list of items.  The select() method allows the user to scroll the list using the right and left keys to scroll forward and backward 
 * through the list. The location of the list , and an optional title can be specified.
 * @author Roger Glassey   Feb 20, 2007
 */

public class TextMenu  
{
	/**
	 * index of the list item at the top of the list; set by constructor, used by select()
 	 **/
	private int _topIndex = 0;  
	
	/** 
	 *	index of the currently selected item; updated by select() used by display();
	 */
	private int _selectedIndex = 0;
	
	/** 
	 * number of rows displayed; set by constructor, used by display()
	 */
	private int _size = 8;
	
	/**
	 *location of the top row of the list; set by constructor, used by display()
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
	 *optional menu title displayed immediately above the list of items
	 */
	private String _title;
	
	/**
	 * boolean to cause select to quit 
	 */
	private boolean _quit = false;
	
	/**
	 * effective length of items array  - number of items before null 
	 */
	private int _length;
	
	/**
	 * This constructor sets location of the top row of the item list to row 0 of the display.
	 */
	public TextMenu( String[] items)
	{
		this.setItems(items);
	}
	
	/**
	 * This constructor allows specification location of the item list .
	 */
	public TextMenu( String[] items, int topRow)
	{
		_topRow = topRow;
		this.setItems(items);
	}
	
	/**
	 * This constuctor allows the specfication of a title (of up to 16 characters) and the location of the item list <br>
	 * The title is displayed in the row above the item lise.
	 * @param items  -  string array containing the menu items. No items beyond the first null will be displayed.
	 */	
	public TextMenu(String[] items, int topRow, String title)
	{
		_title = title;
		_topRow = topRow;
		this.setItems(items);
	}
	
	/**
	 * set menu title. 
	 * @param title  the new title
	 */
	public void setTitle(String title) 
	{
		_title = title;
		if(_topRow == 0)_topRow = 1;
		if(_length <= 8)_size = _length;
		if(_size > 8 - _topRow) _size = 8 - _topRow;
	}
	
	/**
	 * set the array of items to be displayed
	 * @param items
	 */
	public void setItems(String[] items)
	{
		_items = items;
		if(items == null) return;
		int i = 0;
		while(i < items.length && items[i] != null)i++;
		_length = i;
//		LCD.drawInt(_topRow,2,0,7);
//		LCD.drawInt(_length, 2,5,7 );
		_size = _length;
		if(_size > 8 - _topRow) _size = 8 - _topRow;
//		LCD.drawInt(_size,2,9,7);
		_quit = false;
		_topIndex = 0;
	}
	
	/**
	 * Allows the user to scroll through the items, using the right and left buttons (forward and back)  The Enter key closes the menu <br>
	 * and returns the index of the selected item. <br>
	 * The menu display wraps items that scroll off the top will reappear on the bottom and vice versa.
	 * @return the index of the selected item
	 **/
	public int select()
	{
//		if (_length<_size) _size = _length;
		int button = 0;
		_selectedIndex = 0;
//		LCD.clear();
		display();
		while(!_quit)
		{
			while(Button.readButtons()>0 && !_quit)Thread.yield();// wait for release
			while(Button.readButtons()==0 && !_quit) Thread.yield();
			if (_quit) return -2; // quit by another thread
			try {Thread.sleep(20);} catch (InterruptedException ie) {} // wait to stabilize
			button=Button.readButtons();
			
			if(button == 1) return _selectedIndex;
			if(button == 8) return -1; //Escape
			if(button == 4)//scroll forward
			{
				_selectedIndex ++;
				// check for index out of bounds
				if(_selectedIndex >= _length) _selectedIndex -= _length;				
				int diff = _selectedIndex - _topIndex;
				if(diff < 0)diff += _length;
				if(diff >= _size) _topIndex = 1+ _selectedIndex  - _size;
			}
			if(button == 2)//scroll backward
			{
				_selectedIndex --;
				// check for index out of bounds
				if(_selectedIndex < 0) _selectedIndex  += _length;
				int diff = _selectedIndex - _topIndex;
				if(diff > _length) diff -= _length;
				if(diff < 0 || diff > _size)_topIndex = _selectedIndex;
			}
			display();
		}
		return -2;
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
	private  void display()
	{
		if(_title != null)LCD.drawString(_title,0,_topRow-1, true);
		for (int i = 0;i<_size;i++)
		{
			LCD.drawString(blank,0,i + _topRow, false);
			int indx = index(i);
			if(_items[indx] !=null)
			{
				LCD.drawString(_items[indx],1,i + _topRow, false);
				if(indx == _selectedIndex) LCD.drawString(_selChar,0,i + _topRow, false);
			}
		}
		LCD.refresh();
	}
	
	/**
	 * helper method used by display() to caluclate the index in the items array corresponding to a row of the 
	 * menu display
	 */
	private int index(int row)
	{
		return (_topIndex + row + _length)%_length;
	}
	
}

