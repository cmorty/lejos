
import java.io.*;
import lejos.nxt.*;

public class Menu {

	public static void main(String[] args) throws Exception {
		File[] files = File.listFiles();
		
		int len = 0;
		for(int i=0;i<files.length && files[i] != null;i++) len++;
		String[] fileNames = new String[len];
		
		for(int i=0;i<len;i++) fileNames[i] = files[i].getName();
		
		TextMenu menu = new TextMenu(fileNames, len, "Execute program");
		
	    int selection = menu.select();
	    
	    if (selection >= 0) {
	    	LCD.clear();
	    	LCD.refresh();
	    	files[selection].exec();
	    } 
	}

}
