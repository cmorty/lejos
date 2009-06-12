import lejos.nxt.*;
import lejos.util.TextMenu;

import java.io.*;

/**
 * Demonstrates playing 8-bit WAV files.
 * 
 * Use nxjbrowse to upload 8-bit WAV files. On Windows XP,
 * ringin.wav and ringout.wav are 8-bit WAV file, 
 * which can be found in the Media subfolder of
 * the Windows folder.
 * 
 * @author Lawrie Griffiths
 *
 */
public class SoundSample {
	public static void main(String [] options) throws Exception {
		File[] allFiles = File.listFiles();
		String s;
		int len = 0;
		
		// Calculate number of WAV files
		
		for(int i=0;i<allFiles.length && allFiles[i] != null;i++) {
			s = allFiles[i].getName();
			int l = s.length();
			if (l > 4 && s.charAt(l-3) =='w' && s.charAt(l-2) == 'a' && s.charAt(l-1) == 'v') len++;	
		}
		
		// Make array of WAV files and fileNames
		
		String[] fileNames = new String[len];
		File[] files = new File[len];
		int j = 0;
		for(int i=0;i<allFiles.length && allFiles[i] != null;i++) {
			s = allFiles[i].getName();
			int l = s.length();
			if (l > 4 && s.charAt(l-3) =='w' && s.charAt(l-2) == 'a' && s.charAt(l-1) == 'v') {
				fileNames[j] = s;
				files[j++] = allFiles[i];
			}
		}
		
		LCD.drawString("Play a WAV file",0,0);
		
		// Create menu of WAV files
		TextMenu fileMenu = new TextMenu(fileNames,1);
		
		// Play files until the user quits
		int selected;
		do {
			selected = fileMenu.select();
			if (selected >= 0) {
				Sound.playSample(files[selected],100);
			}
		} while (selected >= 0);

	}
}
