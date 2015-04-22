package lejos.pc.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class LCDDisplay extends JPanel
{
	private static final long serialVersionUID = 1L;
	private static final int LCD_WIDTH = 100;
    private static final int LCD_HEIGHT = 64;
    
    private BufferedImage lcd = new BufferedImage(LCD_WIDTH, LCD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private Graphics2D lcdGC = lcd.createGraphics();

    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        super.paint(g);
        int width = getWidth();
        int height = getHeight();
        int imgWidth = lcd.getWidth();
        int imgHeight = lcd.getHeight();
        // Draw a scaled version of the display, keep the aspect ratio and
        // centre it.
        if (width < (height*imgWidth)/imgHeight)
        {
            imgHeight = (width*imgHeight)/imgWidth;
            imgWidth = width;
        }
        else
        {
            imgWidth = (height*imgWidth)/imgHeight;
            imgHeight = height;
        }
        g2d.drawImage(lcd, (width-imgWidth)/2, (height-imgHeight)/2, imgWidth, imgHeight, null);

    }
    
    public void clear()
    {
        lcdGC.setColor(new Color(155, 205, 155, 255));
        lcdGC.fillRect(0, 0, lcd.getWidth(), lcd.getHeight());
    }

    public void update(byte [] buffer)
    {
        int offset = 0;
        int row = 0;
        lcdGC.setColor(new Color(155, 205, 155, 255));
        lcdGC.fillRect(0, 0, lcd.getWidth(), lcd.getHeight());
        lcdGC.setColor(new Color(0, 0, 0, 255));
        for(row = 0; row < 64; row += 8)
            for(int x = 0; x < LCD_WIDTH; x++)
            {
                byte vals = buffer[offset++];
                for(int y = 0; y < 8; y++)
                {
                    if ((vals & 1) != 0)
                        lcdGC.fillRect(x, y+row, 1, 1);
                    vals >>= 1;
                }
            }
        this.repaint();
    }

}

