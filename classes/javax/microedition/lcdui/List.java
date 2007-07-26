package javax.microedition.lcdui;

import java.util.ArrayList;

/**
 * 
 * @author Andre Nijholt
 */
public class List extends Screen implements Choice {
	private static final int MAX_NOOF_SCROLL_LINES = 5;
	
	/** Default command for implicit lists */
	private final Command SELECT_COMMAND = new Command(0, Command.SCREEN, 0);

	protected int listType;
	protected ArrayList listItems;
	private int fitPolicy;
	
	/** Scrolling administration */
	private int scrollFirst = 0;
	private int scrollCurr 	= 0;
	private int scrollLast 	= MAX_NOOF_SCROLL_LINES;
	
	public List(String title, int listType) {
		this.title = title;
		this.listType = listType;
		listItems = new ArrayList();
	}
	
	List(String title, int listType, String[] stringElements, Image[] imageElements) {
		this.title = title;
		this.listType = listType;

		listItems = new ArrayList(stringElements.length);
		for (int i = 0; i < stringElements.length; i++) {
			listItems.add(new ListItem(stringElements[i], imageElements[i]));
		}
	}

	public int append(String stringPart, Image imagePart) {
		listItems.add(new ListItem(stringPart, imagePart));
		return listItems.size();
	}
	
	public void delete(int elementNum) {
		listItems.remove(elementNum);
	}
	
	public void deleteAll() {
		listItems.clear();
	}

	public int getFitPolicy() {
		return fitPolicy;
	}
	
	public Font getFont(int elementNum) {
		return ((ListItem) listItems.get(elementNum)).font;
	}
	
	public Image getImage(int elementNum) {
		return ((ListItem) listItems.get(elementNum)).img;
	}

	public int getSelectedFlags(boolean[] selectedArray_return) {
		selectedArray_return = new boolean[listItems.size()];
		for (int i = 0; i < selectedArray_return.length; i++) {
			selectedArray_return[i] = ((ListItem) listItems.get(i)).selected;
		}
		
		return selectedArray_return.length;
	}

	public int getSelectedIndex() {
		for (int i = 0; i < listItems.size(); i++) {
			if (((ListItem) listItems.get(i)).selected) {
				return i;
			}
		}

		return -1;
	}
	public String getString(int elementNum) {
		return ((ListItem) listItems.get(elementNum)).str;
	} 
	
	public void insert(int elementNum, String stringPart, Image imagePart) {
		listItems.add(elementNum, new ListItem(stringPart, imagePart));
	}
	
	public boolean isSelected(int elementNum) {
		return ((ListItem) listItems.get(elementNum)).selected;
	}

	public void set(int elementNum, String stringPart, Image imagePart) {
		listItems.set(elementNum, new ListItem(stringPart, imagePart));
	}
	
    public void setFitPolicy(int fitPolicy) {
    	this.fitPolicy = fitPolicy;
    }
    
    public void setFont(int elementNum, Font font) {
    	((ListItem) listItems.get(elementNum)).font = font;
    } 
    
    public void setSelectedFlags(boolean[] selectedArray) {
		for (int i = 0; i < listItems.size(); i++) {
			((ListItem) listItems.get(i)).selected = selectedArray[i];
		}
    } 

    public void setSelectedIndex(int elementNum, boolean selected) {
    	((ListItem) listItems.get(elementNum)).selected = selected;
    }
    
    public int size() {
    	return listItems.size();
    }
    
	protected void keyPressed(int keyCode) {
		if (keyCode == KEY_RIGHT) {
			if (fitPolicy == Choice.TEXT_WRAP_ON) {
				scrollCurr = (scrollCurr + 1) % listItems.size();
			} else if (scrollCurr < (listItems.size() - 1)) {
				scrollCurr++;
			}
			repaint();
		} else if (keyCode == KEY_LEFT) {
			if (fitPolicy == Choice.TEXT_WRAP_ON) {
				scrollCurr = (scrollCurr == 0) 
					? (listItems.size() - 1) : (scrollCurr - 1);
			} else if (scrollCurr > 0) {
				scrollCurr--;
			}
			repaint();
		} else if (keyCode == KEY_BACK) {
			for (int i = 0; i < commands.size(); i++) {
				callCommandListener();
			}
		} else if (keyCode == KEY_ENTER) {
			if ((listType == Choice.IMPLICIT) || (listType == Choice.EXCLUSIVE)) {
				// Set single selection for these types
				for (int i = 0; i < listItems.size(); i++) {
					if ((scrollCurr == i)) {
						// Toggle selection (discard current state when IMPLICIT)
						ListItem li = ((ListItem) listItems.get(scrollCurr));
						setSelectedIndex(scrollCurr, (listType == Choice.IMPLICIT)
								? true : !li.selected);						
					} else {
						// Multiple items cannot be selected for this listType
						setSelectedIndex(i, false);
					}
				}
			} else {
				// Toggle selection
				ListItem li = ((ListItem) listItems.get(scrollCurr));
				setSelectedIndex(scrollCurr, !li.selected);
			}
			
			// Send selection command for implicit list only
			if (listType == Choice.IMPLICIT) {
				cmdListener.commandAction(SELECT_COMMAND, this);
			}				
		}
	}

    
    protected void paint(Graphics g) {
    	int lineIdx = 1;
    	g.clear();

    	if (title != null) {
    		g.drawString(title, 0, lineIdx++);
    	}

    	// Update scrolling administration
    	if (scrollCurr > scrollLast) {
    		scrollFirst++;
    		scrollLast++;
    	} else if (scrollCurr < scrollFirst) {
    		scrollFirst--;
    		scrollLast--;
    	}

    	// Display list items with current highlighted
		for (int i = scrollFirst; (i < listItems.size()) && (i <= scrollLast); i++) {
			ListItem li = ((ListItem) listItems.get(i));
			g.drawString(li.str, 2, lineIdx, (i == scrollCurr));
			
			// Draw selection state
			if ((listType == Choice.EXCLUSIVE) || (listType == Choice.MULTIPLE)) {
				if (li.selected) {
					g.fillArc(2, lineIdx * 8, 8, 8, 0, 360);
				} else {
					g.drawArc(2, lineIdx * 8, 8, 8, 0, 360);
				}
			}
			
			lineIdx++;
		}
    }
    
    private class ListItem {
    	String str;
    	Image img;
    	boolean selected;
    	Font font;

    	ListItem(String stringPart, Image imagePart) {
    		this.str = stringPart;
    		this.img = imagePart;
    		this.selected = false;
    		this.font = null; // TODO
    	}    	
    }
}
