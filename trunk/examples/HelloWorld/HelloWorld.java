import lejos.nxt.*;

public class HelloWorld
{
  public static void main (String[] aArg)
  throws Exception
  {
     LCD.drawString("Hello World",3,4);
     Thread.sleep(2000);
  }
}
