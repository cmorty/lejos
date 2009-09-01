import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.robotics.Colors;
import lejos.util.TextMenu;


/**
 * Test program for the Lego Color Sensor.
 * @author andy
 */
public class ColorSensorTest
{

	public static void main(String [] args) throws Exception
    {
        String ports[] = {"Port 1", "Port 2", "Port 3", "Port 4"};
        TextMenu portMenu = new TextMenu(ports, 0, "Sensor port");
        String modes[] = {"Full", "Red", "Green", "Blue", "None"};
        TextMenu modeMenu = new TextMenu(modes, 0, "Color mode");
        int rawVals[] = new int[4];
        int vals[] = new int[4];

        int portNo = portMenu.select();
        if (portNo < 0) return;
        for(;;)
        {
            ColorLightSensor cs = new ColorLightSensor(SensorPort.PORTS[portNo], ColorLightSensor.TYPE_COLORNONE);
            int mode = modeMenu.select();
            if (mode < 0) return;
            cs.setType(ColorLightSensor.TYPE_COLORFULL + mode);
            LCD.clear();
            while (!Button.ESCAPE.isPressed())
            {
                LCD.drawString("Mode: " + modes[mode], 0, 0);
                LCD.drawString("Color   Raw  Cal", 0, 1);
                if (mode == 0)
                {
                    cs.readValues(vals);
                    cs.readRawValues(rawVals);
                    Colors.Color color = cs.readColor();
                    int colorVal = cs.readValue();
                    for(int i = 0; i < vals.length; i++)
                    {
                        LCD.drawString(modes[i+1], 0, i + 2);
                        LCD.drawInt(rawVals[i], 5, 6, i + 2);
                        LCD.drawInt(vals[i], 5, 11, i + 2);
                    }
                    LCD.drawString("Color:          ", 0, 6);
                    LCD.drawString(color.name(), 7, 6);
                    LCD.drawString("Value: ", 0, 7);
                    LCD.drawInt(colorVal, 5, 7, 7);
                }
                else
                {
                    LCD.drawString(modes[mode], 0, 3);
                    int raw = cs.readRawValue();
                    int val = cs.readValue();
                    LCD.drawInt(raw, 5, 6, 3);
                    LCD.drawInt(val, 5, 11, 3);
                }
            }
            LCD.clear();
        }
	}
}