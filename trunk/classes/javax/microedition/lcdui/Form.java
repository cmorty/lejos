package javax.microedition.lcdui;

import lejos.nxt.ArrayList;

/**
 * 
 * @author Andre Nijholt
 */
public class Form extends Screen {
	private ItemStateListener itemStateListener;
	
	private ArrayList items = new ArrayList();
	private int curItemIdx = 0;
	private boolean selectedItem;
	private int height;
	private int width;
	
	public Form(String title) {
		this.title = title;
	}
	
	public Form(String title, Item[] items) {
		this.title = title;
		for (int i = 0; (items != null) && (i < items.length); i++) {
			this.items.add(items[i]);
		}
	}
	
	public int append(Image img) {
		items.add(img);
		return (items.size() - 1);
	}

	public int append(Item item) {
		items.add(item);
		return (items.size() - 1);
	}

	public int append(String str)  {
		items.add(str);
		return (items.size() - 1);
	}
	
	public void delete(int itemNum) {
		items.remove(itemNum);
	}
	
	public void deleteAll() {
		items.clear();
	}
	
	public Object get(int itemNum) {
		return items.get(itemNum);
	}
	
	public void set(int itemNum, Item item) {
		items.set(itemNum, item);
	}
	
	public void insert(int itemNum, Item item) {
		items.add(itemNum, item);
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
		
	public int size() {
		return items.size();
	}

	public void setItemStateListener(ItemStateListener i) {
		this.itemStateListener = i;
	}

	protected void callItemStateListener() {
		for (int i = 0; (i < items.size()) && (itemStateListener != null); i++) {
			Object o = items.get(i);
			if (o instanceof Item) {
				itemStateListener.itemStateChanged((Item) o);
			}
		}
	}

	protected void keyPressed(int keyCode) {
		if (selectedItem && curItemIdx >= 0) {
			if ((keyCode == Screen.KEY_RIGHT) 
					|| (keyCode == Screen.KEY_LEFT) 
					|| (keyCode == Screen.KEY_ENTER)) {
				// Update currently selected item
				((Item) items.get(curItemIdx)).keyPressed(keyCode);
			} else if (keyCode == Screen.KEY_BACK) {				
				Object o = items.get(curItemIdx);
				if (o instanceof TextField) {
					// Update currently selected TextField until keyboard enter pressed
					((Item) items.get(curItemIdx)).keyPressed(keyCode);					
				} else {
					// Leave current selection
					selectedItem = false;
				}
			}				
		} else {
			// Select new item
			if (keyCode == Screen.KEY_RIGHT) {
				for (int i = curItemIdx + 1; i != curItemIdx; i++) {
					// Wrap when last item checked
					if (i >= items.size()) {
						i = 0;
					}
					
					Object o = items.get(i);
					if ((o instanceof Item) && (((Item) o).isInteractive())) {
						curItemIdx = i;
						break;
					}
				}
			} else if (keyCode == Screen.KEY_LEFT) {
				for (int i = curItemIdx - 1; i != curItemIdx; i--) {
					// Wrap when first item checked
					if (i < 0) {
						i = (items.size() - 1);
					}
					
					Object o = items.get(i);
					if ((o instanceof Item) && (((Item) o).isInteractive())) {
						curItemIdx = i;
						break;
					}
				}
			} else if (keyCode == Screen.KEY_BACK) {
				for (int i = 0; i < commands.size(); i++) {
					callCommandListener();
				}
			} else if (keyCode == Screen.KEY_ENTER) {
				selectedItem = true;		
			}
		}
		repaint();
	}

	public void paint(Graphics g) {
		int curX = 0;
		int curY = 0;
		int curWidth = 100;
		int curHeight = 24;

		g.clear();
		for (int i = 0; i < items.size(); i++) {
			Object o = items.get(i);
			if (o instanceof Image) {
				
			} else if (o instanceof String) {
				
			} else if (o instanceof Item) {
				Item item = (Item) o;
				
				curWidth = item.getMinimumWidth();
				if (curWidth > (Display.SCREEN_WIDTH >> 1)) {
					curWidth = Display.SCREEN_WIDTH;
				}
				curHeight = item.getMinimumHeight();
				item.paint(g, curX, curY, curWidth, curHeight, (i == curItemIdx));

				// Start on next line
				curY += (((curHeight + Display.CHAR_HEIGHT - 1) / Display.CHAR_HEIGHT)) * Display.CHAR_HEIGHT;
				curX = 0; // TODO
			}
		}
	}
}
