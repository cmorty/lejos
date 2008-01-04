package lejos.nxt;

import java.io.*;

/**
 * NXT sound routines.
 *
 */
public class Sound
{
  private Sound()
  {
  }

  /**
   * Play a system sound.
   * <TABLE BORDER=1>
   * <TR><TH>aCode</TH><TH>Resulting Sound</TH></TR>
   * <TR><TD>0</TD><TD>short beep</TD></TR>
   * <TR><TD>1</TD><TD>double beep</TD></TR>
   * <TR><TD>2</TD><TD>descending arpeggio</TD></TR>
   * <TR><TD>3</TD><TD>ascending  arpeggio</TD></TR>
   * <TR><TD>4</TD><TD>long, low buzz</TD></TR>
   * </TABLE>
   */
   
  public static int C2 = 1056;
  
  public static void systemSound (boolean aQueued, int aCode)
  {
  	if(aCode==0)
  	{
  		playTone(1200,200);
  	}
  	else if(aCode == 1)
  	{
  		playTone(1200,150);
  		pause(200);
  		playTone(1200,150);
  		pause(150);
  	}
     	else if(aCode == 2)
  	{// C major arpeggio
  		for(int i = 4; i<8; i++)
  		{
  			playTone(C2*i/4,100);
  			pause(100);
  		}
  	}
  	else if(aCode == 3)
  	{
  		for(int i = 7; i>3; i--)
  		{
  			playTone(C2*i/4,100);
  			pause(100);
  		}
  	}
  	else if(aCode == 4 )
  	{ 
  		playTone(100,500);
  		pause(500);
  	}
  }

  /**
   * Beeps once.
   */
  public static void beep()
  {
    systemSound (true, 0);
  }

  /**
   * Beeps twice.
   */
  public static void twoBeeps()
  {
    systemSound (true, 1);
  }

  /**
   * Downward tones.
   */
  public static void beepSequence()
  {
    systemSound (true, 3);
  }

  /**
   * Upward tones.
   */
 public static void beepSequenceUp()
 {
 	systemSound (true,2);
 }

  /**
   * Low buzz 
   */
  public static void buzz()
  {
    systemSound (true, 4);
  }
  
  public static void pause(int t)
	{
		try { Thread.sleep(t); }
		catch(InterruptedException e){}
	}
	
  /**
   * Plays a tone, given its frequency and duration. Frequency is audible from about 31 to 2100 Hertz. The
   * duration argument is in hundreds of a seconds (centiseconds, not milliseconds) and is truncated
   * at 256, so the maximum duration of a tone is 2.56 seconds.
   * @param aFrequency The frequency of the tone in Hertz (Hz).
   * @param aDuration The duration of the tone, in centiseconds. Value is truncated at 256 centiseconds.
   */
  public static native void playTone (int aFrequency, int aDuration);
  
  /**
   * Internal method used to play sound sample from a file
   * @param page the start page of the file
   * @param len the length of the file
   * @param freq the frequency 
   * @param vol the volume 1000 corresponds to 100%, 100 to 10%, 2500 to 250%
   */
  public static native void playSample(int page, int len, int freq, int vol);
  
  /**
   * 
   * @param file the 8-bit PWM (WAV) sample file
   * @param vol the volume percentage 0 - 100
   */
  public static void playSample(File file, int vol) {
	  playSample(file.getPage(), file.length(), 17600, vol * 10);
  }
  
}
