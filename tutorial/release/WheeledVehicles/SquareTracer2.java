import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * Trace  two squares, twice.  
 * @author Roger
 */
public class SquareTracer2 
{
    DifferentialPilot pilot ;
    public void  drawSquare(float length)
    {
        byte direction = 1;
        if(length < 0 )
        {
            direction = -1;
            length = -length;
        }
        for(int i = 0; i<4 ; i++)
        {
            pilot.travel(length);
            pilot.rotate(direction * 90);                 
        }
    }
    public static void main( String[] args)
    {
        System.out.println(" Square Tracer 2");
        Button.waitForAnyPress();
        SquareTracer2 sq = new SquareTracer2();
        sq.pilot = new DifferentialPilot(2.25f, 5.5f, Motor.A, Motor.C);
        byte direction = 1;
        int length = 20;
        for(int i = 0; i<4; i++)
        {
            sq.drawSquare(direction * length );
            if( i == 1)
            {
                sq.pilot.rotate( 90);
                direction = -1;
            }
        }
    }
}
