package lejos.nxt;

/**
 * RCX sound routines.
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
   * <TR><TD>4</TD><TD>long, low beep</TD></TR>
   * <TR><TD>5</TD><TD>quick ascending arpeggio</TD></TR>
   * </TABLE>
   */
  public static void systemSound (boolean aQueued, int aCode)
  {
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
    systemSound (true, 2);
  }

  /**
   * Low buzz.
   */
  public static void buzz()
  {
    systemSound (true, 4);
  }

  /**
   * Plays a tone, given its frequency and duration. Frequency is audible from about 31 to 2100 Hertz. The
   * duration argument is in hundreds of a seconds (centiseconds, not milliseconds) and is truncated
   * at 256, so the maximum duration of a tone is 2.56 seconds.
   * @param aFrequency The frequency of the tone in Hertz (Hz).
   * @param aDuration The duration of the tone, in centiseconds. Value is truncated at 256 centiseconds.
   */
  public static void playTone (int aFrequency, int aDuration)
  {
  }
}
