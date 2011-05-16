import lejos.nxt.*;
import lejos.util.TextMenu;
import lejos.robotics.Color;


/**
 * Test program for the Lego Color Sensor.
 * @author andy
 */
public class ColorSensorTest
{
    static void displayColor(String name, int raw, int calibrated, int line)
    {
        LCD.drawString(name, 0, line);
        LCD.drawInt(raw, 5, 6, line);
        LCD.drawInt(calibrated, 5, 11, line);
    }

	public static void main(String [] args) throws Exception
    {
        String ports[] = {"Port 1", "Port 2", "Port 3", "Port 4"};
        TextMenu portMenu = new TextMenu(ports, 1, "Sensor port");
        String modes[] = {"Full", "Red", "Green", "Blue", "White", "None"};
        int colors[] = {Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, Color.NONE};
        String colorNames[] = {"None", "Red", "Green", "Blue", "Yellow",
                                "Megenta", "Orange", "White", "Black", "Pink",
                                "Grey", "Light Grey", "Dark Grey", "Cyan"};
        TextMenu modeMenu = new TextMenu(modes, 1, "Color mode");

        int portNo = portMenu.select();
        if (portNo < 0) return;
        for(;;)
        {
            ColorSensor cs = new ColorSensor(SensorPort.getInstance(portNo));
            int mode = modeMenu.select();
            if (mode < 0) return;
            cs.setFloodlight(colors[mode]);
            LCD.clear();
            while (!Button.ESCAPE.isPressed())
            {
                LCD.drawString("Mode: " + modes[mode], 0, 0);
                LCD.drawString("Color   Raw  Cal", 0, 1);
                if (mode == 0)
                {
                    ColorSensor.Color vals = cs.getColor();
                    ColorSensor.Color rawVals = cs.getRawColor();
                    displayColor("Red", rawVals.getRed(), vals.getRed(), 2);
                    displayColor("Green", rawVals.getGreen(), vals.getGreen(), 3);
                    displayColor("Blue", rawVals.getBlue(), vals.getBlue(), 4);
                    displayColor("None", rawVals.getBackground(), vals.getBackground(), 5);
                    LCD.drawString("Color:          ", 0, 6);
                    LCD.drawString(colorNames[vals.getColor() + 1], 7, 6);
                    LCD.drawString("Color val:          ", 0, 7);
                    LCD.drawInt(vals.getColor(), 3, 11, 7);
                }
                else
                {
                    LCD.drawString(modes[mode], 0, 3);
                    int raw = cs.getRawLightValue();
                    int val = cs.getLightValue();
                    LCD.drawInt(raw, 5, 6, 3);
                    LCD.drawInt(val, 5, 11, 3);
                }
            }
            LCD.clear();
        }
	}
}