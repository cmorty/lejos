package lejos.nxt;

import java.io.*;

/**
 * NXT sound routines.
 *
 */
public class Sound
{
  private static final int RIFF_HDR_SIZE = 44;
  private static final int RIFF_RIFF_SIG = 0x52494646;
  private static final int RIFF_WAVE_SIG = 0x57415645;
  private static final int RIFF_FMT_SIG = 0x666d7420;
  private static final short RIFF_FMT_PCM = 0x0100;
  private static final short RIFF_FMT_1CHAN = 0x0100;
  private static final short RIFF_FMT_8BITS = 0x0800;
  private static final int RIFF_DATA_SIG = 0x64617461;
  
  public static final int VOL_USEMASTER = -1;
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
   
  public static int C2 = 523;
  
  public static void systemSound (boolean aQueued, int aCode)
  {
  	if(aCode==0)
  	{
  		playTone(600,200);
  	}
  	else if(aCode == 1)
  	{
  		playTone(600,150);
  		pause(200);
  		playTone(600,150);
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
   * Returns the number of milliseconds remaining of the current tone or sample.
   * @return milliseconds remaining
   */
  public static native int getTime();
  
  /**
   * Plays a tone, given its frequency and duration. 
   * @param aFrequency The frequency of the tone in Hertz (Hz).
   * @param aDuration The duration of the tone, in milliseconds.
   * @param aVolume The volume of the playback 100 corresponds to 100%
   */
  public static native void playTone (int aFrequency, int aDuration, int aVolume);
  
  public static void playTone(int freq, int duration)
  {
	  playTone(freq, duration, VOL_USEMASTER);
  }
  
  /**
   * Internal method used to play sound sample from a file
   * @param page the start page of the file
   * @param offset the start of the samples in the file
   * @param len the length of the sample to play
   * @param freq the sampling frequency 
   * @param vol the volume 100 corresponds to 100%
   */
  public static native void playSample(int page, int offset, int len, int freq, int vol);
  
  /**
   * Play a wav file
   * @param file the 8-bit PWM (WAV) sample file
   * @param vol the volume percentage 0 - 100
   * @return The number of milliseconds the sample will play for or < 0 if
   *         there is an error.
   */
  public static int playSample(File file, int vol) {
	  // First check that we have a wave file. File must be at least 44 bytes
	  // in size to contain a RIFF header.
	  if (file.length() < RIFF_HDR_SIZE) return -1;
	  // Now check for a RIFF header
	  FileInputStream f = new FileInputStream(file);
	  DataInputStream d = new DataInputStream(f);
	  int sampleRate = 0;
	  int dataLen = 0;
	  try{
		if (d.readInt() != RIFF_RIFF_SIG) return -1;
		// Skip chunk size
		d.readInt();
		// Check we have a wave file
		if (d.readInt() != RIFF_WAVE_SIG) return -1;
		if (d.readInt() != RIFF_FMT_SIG) return -1;
		// Now check that the format is PCM, Mono 8 bits. Note that these
		// values are stored little endian.
		d.readInt(); // Skip chunk size
		if (d.readShort() != RIFF_FMT_PCM) return -1;
		if (d.readShort() != RIFF_FMT_1CHAN) return -1;
		sampleRate = d.readByte() & 0xff;
		sampleRate |= (d.readByte() & 0xff) << 8;
		sampleRate |= (d.readByte() & 0xff) << 16;
		sampleRate |= (d.readByte() & 0xff) << 24;
		d.readInt();
		d.readShort();
		if (d.readShort() != RIFF_FMT_8BITS) return -1;
		// Make sure we now have a data chunk
		if (d.readInt() != RIFF_DATA_SIG) return -1;
		dataLen = d.readByte() & 0xff;
		dataLen |= (d.readByte() & 0xff) << 8;
		dataLen |= (d.readByte() & 0xff) << 16;
		dataLen |= (d.readByte() & 0xff) << 24;		
		d.close();
	  }
	  catch (IOException e)
	  {
		  return -1;
	  }
	  playSample(file.getPage(), RIFF_HDR_SIZE, dataLen, sampleRate, vol);
	  return getTime();
  }

  /**
   * Play a wav file
   * @param file the 8-bit PWM (WAV) sample file
   * @return The number of milliseconds the sample will play for or < 0 if
   *         there is an error.
   */
  public static int playSample(File file) {
	  return playSample(file, VOL_USEMASTER);
  }
  
  /**
   * Set the master volume level
   * @param vol 
   */
  public static native void setVolume(int vol);
  
  /**
   * Get the current master volume level
   * @return
   */
  public static native int getVolume();
}
