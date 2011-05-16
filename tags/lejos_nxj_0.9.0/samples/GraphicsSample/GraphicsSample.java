
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Font;
import lejos.nxt.*;
import lejos.util.Delay;
import java.io.FileInputStream;
import java.io.File;

/**
 * Demonstrate various leJOS graphics techniques.
 */
public class GraphicsSample
{

    Graphics g = new Graphics();
    final int SW = LCD.SCREEN_WIDTH;
    final int SH = LCD.SCREEN_HEIGHT;
    final int DELAY = 4000;
    final int TITLE_DELAY = 2000;
    Image duke = new Image(100, 64, new byte[]
            {
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x70, (byte) 0xf8, (byte) 0xf8,
                (byte) 0xf0, (byte) 0xc0, (byte) 0xbe, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xf8, (byte) 0xc0, (byte) 0xf0, (byte) 0xfc,
                (byte) 0xfc, (byte) 0xfe, (byte) 0x1c, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xff, (byte) 0xff, (byte) 0xfe, (byte) 0xfe, (byte) 0xfc,
                (byte) 0xf8, (byte) 0xf0, (byte) 0xe0, (byte) 0xc0, (byte) 0x80,
                (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0xc1, (byte) 0xc7,
                (byte) 0xcf, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0x20, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xfe, (byte) 0xf9, (byte) 0xf3, (byte) 0xe3,
                (byte) 0x83, (byte) 0x01, (byte) 0x03, (byte) 0x03, (byte) 0x07,
                (byte) 0x07, (byte) 0x0f, (byte) 0xff, (byte) 0xff, (byte) 0x03,
                (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xe0,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xbf, (byte) 0x5f,
                (byte) 0x8f, (byte) 0x57, (byte) 0xa7, (byte) 0x57, (byte) 0x8b,
                (byte) 0x57, (byte) 0xaf, (byte) 0x77, (byte) 0xaf, (byte) 0xff,
                (byte) 0xaf, (byte) 0x1d, (byte) 0x1c, (byte) 0x78, (byte) 0xf8,
                (byte) 0xf8, (byte) 0xf8, (byte) 0x1f, (byte) 0x07, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xc0, (byte) 0xe0, (byte) 0xf0, (byte) 0xfe, (byte) 0xff,
                (byte) 0x0f, (byte) 0x00, (byte) 0x07, (byte) 0x0e, (byte) 0x1d,
                (byte) 0x1a, (byte) 0x15, (byte) 0x3a, (byte) 0x15, (byte) 0x1a,
                (byte) 0x1d, (byte) 0x0a, (byte) 0x0f, (byte) 0x02, (byte) 0x03,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
                (byte) 0x07, (byte) 0x3f, (byte) 0xf8, (byte) 0xe0, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1f,
                (byte) 0xff, (byte) 0xe0, (byte) 0xfe, (byte) 0x3f, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0f, (byte) 0xff,
                (byte) 0xf0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0xe0, (byte) 0xf0, (byte) 0xfc,
                (byte) 0xff, (byte) 0xff, (byte) 0x0f, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x80, (byte) 0x80, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0,
                (byte) 0xe0, (byte) 0xe0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0,
                (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
                (byte) 0xff, (byte) 0xfe, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x3f, (byte) 0x7f, (byte) 0x60, (byte) 0x60, (byte) 0x30,
                (byte) 0x38, (byte) 0x18, (byte) 0x0c, (byte) 0x06, (byte) 0x07,
                (byte) 0x03, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x01, (byte) 0x03, (byte) 0x0e, (byte) 0x1c, (byte) 0x38,
                (byte) 0x60, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0x7c,
                (byte) 0x3f, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            });

    void titles()
    {
        LCD.setContrast(0);
        g.setFont(Font.getLargeFont());
        g.drawString("leJOS", SW/2, SH/2, Graphics.BOTTOM|Graphics.HCENTER);
        g.drawString("Graphics", SW/2, SH/2, Graphics.TOP|Graphics.HCENTER);
        for(int i = 0x20; i < 0x60; i++)
        {
            LCD.setContrast(i);
            Delay.msDelay(20);
        }
        g.setFont(Font.getDefaultFont());
        Button.waitForPress(TITLE_DELAY);
        for(int i = 0x60; i > 0; i--)
        {
            LCD.setContrast(i);
            Delay.msDelay(20);
        }
        g.clear();
        LCD.refresh();
        LCD.setContrast(0x60);
    }
    
    void credits()
    {
        g.setFont(Font.getLargeFont());
        g.drawString("FIN", SW/2, SH/2, Graphics.BASELINE|Graphics.HCENTER);
        Button.waitForPress(TITLE_DELAY);
        for(int i = 0x60; i > 0; i--)
        {
            LCD.setContrast(i);
            Delay.msDelay(20);
        }
        g.setFont(Font.getDefaultFont());        
    }

    void displayTitle(String text)
    {
        g.clear();
        g.drawString(text, SW / 2, SH / 2, Graphics.HCENTER | Graphics.BASELINE);
        Button.waitForPress(TITLE_DELAY);
        g.clear();
    }

    void characterSet()
    {
        displayTitle("Character Set");
        int chHeight = g.getFont().getHeight();
        int chWidth = g.getFont().stringWidth("M");
        for(int l = 0; l < 8; l++)
            for(int c = 0; c < 16; c++)
                g.drawChar((char)(l*16 + c), c*chWidth, l*chHeight, 0);
        Button.waitForPress(DELAY);
    }

    void textAnchors()
    {
        displayTitle("Text Anchors");
        int chHeight = g.getFont().getHeight();
        g.drawString("Left", SW / 2, 0, Graphics.LEFT);
        g.drawString("Center", SW / 2, chHeight, Graphics.HCENTER);
        g.drawString("Right", SW / 2, chHeight * 2, Graphics.RIGHT);
        g.drawString("Left", SW / 2, chHeight * 4, Graphics.LEFT, true);
        g.drawString("Center", SW / 2, chHeight * 5, Graphics.HCENTER, true);
        g.drawString("Right", SW / 2, chHeight * 6, Graphics.RIGHT, true);
        Button.waitForPress(DELAY);
    }

    void fonts()
    {
        displayTitle("Fonts");
        g.setFont(Font.getFont(0, 0, Font.SIZE_SMALL));
        g.drawString("Small", SW / 2, 16, Graphics.HCENTER | Graphics.BASELINE);
        g.setFont(Font.getFont(0, 0, Font.SIZE_MEDIUM));
        g.drawString("Medium", SW / 2, 32, Graphics.HCENTER | Graphics.BASELINE);
        g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE));
        g.drawString("Large", SW / 2, 54, Graphics.HCENTER | Graphics.BASELINE);
        g.setFont(Font.getDefaultFont());
        Button.waitForPress(DELAY);
    }

    void rotatedText()
    {
        displayTitle("Rotated Text");
        Font large = Font.getFont(0, 0, Font.SIZE_LARGE);
        Image base = Image.createImage(SW, large.getHeight());
        Graphics bg = base.getGraphics();
        bg.setFont(large);
        bg.drawString("Top", SW / 2, 0, Graphics.HCENTER);
        g.drawImage(base, 0, 0, 0);
        bg.clear();
        bg.drawString("Bottom", SW / 2, 0, Graphics.HCENTER);
        Image rotImage = Image.createImage(base, 0, 0, SW, base.getHeight(), Sprite.TRANS_ROT180);
        g.drawImage(rotImage, 0, SH - 1, Graphics.BOTTOM);
        bg.clear();
        bg.drawString("Left", SH / 2, 0, Graphics.HCENTER);
        rotImage = Image.createImage(base, 0, 0, SH, base.getHeight(), Sprite.TRANS_ROT270);
        g.drawImage(rotImage, 0, 0, 0);
        bg.clear();
        bg.drawString("Right", SH / 2, 0, Graphics.HCENTER);
        rotImage = Image.createImage(base, 0, 0, SH, base.getHeight(), Sprite.TRANS_ROT90);
        g.drawImage(rotImage, SW - 1, 0, Graphics.RIGHT);
        Button.waitForPress(DELAY);
    }

    void fileImage() throws Exception
    {
        displayTitle("File image");
        Image img = Image.createImage(new FileInputStream(new File("arm.lni")));
        g.drawRegion(img, 0, 0, SW, SH, Sprite.TRANS_NONE, SW / 2, SH / 2, Graphics.HCENTER | Graphics.VCENTER);
        Button.waitForPress(DELAY);
    }

    void lines()
    {
        displayTitle("Lines");
        for (int i = 1; i < SH / 2; i += 4)
        {
            g.drawLine(i - 4, i, SW - i, i);
            g.drawLine(SW - i, i, SW - i, SH - i);
            g.drawLine(i, SH - i, SW - i, SH - i);
            g.drawLine(i, i + 4, i, SH - i);
        }
        g.drawLine(0, 0, SW, SH);
        g.drawLine(SW, 0, 0, SH);
        Button.waitForPress(DELAY);
    }

    void rectangles()
    {
        displayTitle("Rectangles");
        for (int i = 1; i < 5; i++)
            g.drawRect(i * 20 - 5, 10 - 2 * i, i * 4, i * 4);
        for (int i = 1; i < 5; i++)
            g.fillRect(i * 20 - 5, 40 - 2 * i, i * 4, i * 4);
        Button.waitForPress(DELAY);
    }

    void circles()
    {
        displayTitle("Circles");
        for (int i = 1; i < 5; i++)
            g.drawArc(i * 20 - 5, 10 - 2 * i, i * 4, i * 4, 0, 360);
        for (int i = 1; i < 5; i++)
            g.fillArc(i * 20 - 5, 40 - 2 * i, i * 4, i * 4, 0, 360);
        Button.waitForPress(DELAY);
    }

    void scroll()
    {
        displayTitle("Scrolling");
        int line = g.getFont().getHeight();
        g.drawString("Hello from leJOS", SW / 2, SH - line, Graphics.HCENTER);
        g.setColor(Graphics.WHITE);
        for (int i = 0; i < 7; i++)
        {
            Delay.msDelay(250);
            g.copyArea(0, SH - (i+1) * line, SW, line, 0, SH - (i+2)*line, 0);
            g.fillRect(0, SH - (i+1) * line, SW, line);
        }
        for (int i = 6; i >= 0; i--)
        {
            Delay.msDelay(250);
            g.copyArea(0, SH - (i + 2) * line, SW, line, 0, SH - (i + 1) * line, 0);
            g.fillRect(0, SH - (i + 2) * line, SW, line);
        }
        Button.waitForPress(DELAY);
        LCD.setAutoRefresh(false);
        for (int i = 0; i < 7*line; i++)
        {
            Delay.msDelay(10);
            g.copyArea(0, SH - line - i, SW, line, 0, SH - line - (i + 1), 0);
            g.fillRect(0, SH - i, SW, 1);
            LCD.refresh();
        }
        for (int i = 7*line - 1; i >= 0; i--)
        {
            Delay.msDelay(10);
            g.copyArea(0, SH - line - (i + 1), SW, line, 0, SH - line - i, 0);
            g.fillRect(0, SH - line - (i + 1), SW, 1);
            LCD.refresh();
        }
        LCD.setAutoRefresh(true);
        LCD.refresh();
        Button.waitForPress(DELAY);
        g.setColor(Graphics.BLACK);
    }

    void image(int transform, String title)
    {
        displayTitle(title);
        g.drawRegion(duke, 0, 0, duke.getWidth(), duke.getHeight(), transform, SW / 2, SH / 2, Graphics.HCENTER | Graphics.VCENTER);
        Button.waitForPress(DELAY);
    }

    void images()
    {
        displayTitle("Image Display");
        image(Sprite.TRANS_NONE, "Normal");
        image(Sprite.TRANS_ROT90, "Rotate 90");
        image(Sprite.TRANS_ROT180, "Rotate 180");
        image(Sprite.TRANS_ROT270, "Rotate 270");
        image(Sprite.TRANS_MIRROR, "Mirror");
        image(Sprite.TRANS_MIRROR_ROT90, "Mirror 90");
        image(Sprite.TRANS_MIRROR_ROT180, "Mirror 180");
        image(Sprite.TRANS_MIRROR_ROT270, "Mirror 270");
    }

    void animation()
    {
        displayTitle("Animation");
        Image arms = new Image(216, 33, new byte[]
                {
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xf8, (byte) 0xfc, (byte) 0xfc, (byte) 0xf8,
                    (byte) 0xe0, (byte) 0xbe, (byte) 0xfe, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xd0, (byte) 0xe0,
                    (byte) 0xf8, (byte) 0xf8, (byte) 0xfc, (byte) 0x7c,
                    (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xf8, (byte) 0xfc, (byte) 0xfc, (byte) 0xf8,
                    (byte) 0x80, (byte) 0xf8, (byte) 0xfe, (byte) 0xfe,
                    (byte) 0xfe, (byte) 0xfe, (byte) 0x80, (byte) 0xc0,
                    (byte) 0xe0, (byte) 0xe0, (byte) 0xe0, (byte) 0xe0,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xf0, (byte) 0xf8, (byte) 0xf8, (byte) 0xf8,
                    (byte) 0xb0, (byte) 0xc0, (byte) 0xf0, (byte) 0xf8,
                    (byte) 0xfc, (byte) 0xf8, (byte) 0xb8, (byte) 0x00,
                    (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x80,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xe0,
                    (byte) 0xf0, (byte) 0xf0, (byte) 0xf8, (byte) 0xf0,
                    (byte) 0x00, (byte) 0xc0, (byte) 0xe0, (byte) 0xe0,
                    (byte) 0xf0, (byte) 0xe0, (byte) 0x80, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x80,
                    (byte) 0xc0, (byte) 0xe0, (byte) 0xe0, (byte) 0xc0,
                    (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x00,
                    (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0xc0, (byte) 0xe0,
                    (byte) 0xe0, (byte) 0xe3, (byte) 0xef, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xc7, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x60, (byte) 0xe0, (byte) 0xf0, (byte) 0xe0,
                    (byte) 0xe7, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0x7f, (byte) 0x0f, (byte) 0x01, (byte) 0x01,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x60,
                    (byte) 0xe0, (byte) 0xf0, (byte) 0xe0, (byte) 0xe0,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xfe, (byte) 0xff,
                    (byte) 0x7f, (byte) 0x1f, (byte) 0x0f, (byte) 0x07,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0xe0, (byte) 0xf0, (byte) 0xf0,
                    (byte) 0xe0, (byte) 0xc0, (byte) 0xee, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xfe,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xf7, (byte) 0xf9, (byte) 0xf8, (byte) 0x7c,
                    (byte) 0x7c, (byte) 0x7c, (byte) 0x30, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xc0,
                    (byte) 0xf0, (byte) 0xe0, (byte) 0xf0, (byte) 0xc0,
                    (byte) 0xc0, (byte) 0xf8, (byte) 0xfe, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xfd,
                    (byte) 0xfe, (byte) 0xff, (byte) 0xff, (byte) 0xdf,
                    (byte) 0xe7, (byte) 0xc7, (byte) 0xe0, (byte) 0xc0,
                    (byte) 0xc0, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0xc0,
                    (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x80,
                    (byte) 0xe0, (byte) 0xf0, (byte) 0xfc, (byte) 0xfe,
                    (byte) 0xfe, (byte) 0xbe, (byte) 0xee, (byte) 0xe0,
                    (byte) 0xf0, (byte) 0xf0, (byte) 0xf0, (byte) 0xf0,
                    (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xfe, (byte) 0xf8, (byte) 0xf0, (byte) 0xe1,
                    (byte) 0x81, (byte) 0x03, (byte) 0x01, (byte) 0x03,
                    (byte) 0x03, (byte) 0x07, (byte) 0x0f, (byte) 0xff,
                    (byte) 0xff, (byte) 0x3f, (byte) 0x07, (byte) 0x03,
                    (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xfe, (byte) 0xf8, (byte) 0xf0, (byte) 0xe0,
                    (byte) 0x80, (byte) 0x01, (byte) 0x01, (byte) 0x01,
                    (byte) 0x01, (byte) 0x03, (byte) 0x0f, (byte) 0x8f,
                    (byte) 0xff, (byte) 0xff, (byte) 0x3f, (byte) 0x0f,
                    (byte) 0x0f, (byte) 0x0f, (byte) 0x07, (byte) 0x03,
                    (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xfe, (byte) 0xf8, (byte) 0xf0, (byte) 0xe0,
                    (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x03,
                    (byte) 0x0f, (byte) 0x9f, (byte) 0xff, (byte) 0xff,
                    (byte) 0x7f, (byte) 0x3f, (byte) 0x1f, (byte) 0x1f,
                    (byte) 0x1f, (byte) 0x0f, (byte) 0x0f, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xfe, (byte) 0xf8, (byte) 0xf0, (byte) 0xe0,
                    (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01,
                    (byte) 0x03, (byte) 0x9f, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0x7f, (byte) 0x7f, (byte) 0x7f,
                    (byte) 0x7f, (byte) 0x7f, (byte) 0x3f, (byte) 0x2f,
                    (byte) 0x07, (byte) 0x03, (byte) 0x01, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xfe, (byte) 0xf8, (byte) 0xf0, (byte) 0xe0,
                    (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x01, (byte) 0x01, (byte) 0x17, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0x7f, (byte) 0x3f, (byte) 0x1f, (byte) 0x0f,
                    (byte) 0x07, (byte) 0x03, (byte) 0x07, (byte) 0x03,
                    (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xfe, (byte) 0xf8, (byte) 0xf0, (byte) 0xe0,
                    (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x03, (byte) 0x0f, (byte) 0xdf,
                    (byte) 0xff, (byte) 0xff, (byte) 0xfe, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                    (byte) 0xff, (byte) 0xfb, (byte) 0x7d, (byte) 0x78,
                    (byte) 0x7c, (byte) 0x70, (byte) 0x18, (byte) 0x00,
                    (byte) 0xaf, (byte) 0x77, (byte) 0xaf, (byte) 0xff,
                    (byte) 0xaf, (byte) 0x1d, (byte) 0x1c, (byte) 0x68,
                    (byte) 0xf8, (byte) 0xf8, (byte) 0xff, (byte) 0x1f,
                    (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xaf, (byte) 0x77, (byte) 0xaf, (byte) 0xff,
                    (byte) 0xaf, (byte) 0x1d, (byte) 0x1c, (byte) 0x78,
                    (byte) 0xf8, (byte) 0xf8, (byte) 0xfc, (byte) 0x1f,
                    (byte) 0x0f, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xaf, (byte) 0x77, (byte) 0xaf, (byte) 0xff,
                    (byte) 0xaf, (byte) 0x1d, (byte) 0x1c, (byte) 0x78,
                    (byte) 0xf8, (byte) 0xf8, (byte) 0xf8, (byte) 0x1c,
                    (byte) 0x1f, (byte) 0x0f, (byte) 0x03, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xaf, (byte) 0x77, (byte) 0xaf, (byte) 0xff,
                    (byte) 0xaf, (byte) 0x1d, (byte) 0x1c, (byte) 0x78,
                    (byte) 0xf8, (byte) 0xf8, (byte) 0xf8, (byte) 0x3c,
                    (byte) 0x1e, (byte) 0x07, (byte) 0x03, (byte) 0x01,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xaf, (byte) 0x77, (byte) 0xaf, (byte) 0xff,
                    (byte) 0xaf, (byte) 0x1d, (byte) 0x1c, (byte) 0x78,
                    (byte) 0xfc, (byte) 0xf8, (byte) 0xf8, (byte) 0x3c,
                    (byte) 0x1e, (byte) 0x0e, (byte) 0x07, (byte) 0x03,
                    (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                    (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0xaf, (byte) 0x77, (byte) 0xaf, (byte) 0xff,
                    (byte) 0xaf, (byte) 0x1d, (byte) 0x1c, (byte) 0x70,
                    (byte) 0xf8, (byte) 0xf8, (byte) 0xf8, (byte) 0x78,
                    (byte) 0x38, (byte) 0x38, (byte) 0x1c, (byte) 0x0e,
                    (byte) 0x0f, (byte) 0x07, (byte) 0x07, (byte) 0x07,
                    (byte) 0x0f, (byte) 0x0f, (byte) 0x0f, (byte) 0x07,
                    (byte) 0x0f, (byte) 0x03, (byte) 0x01, (byte) 0x01,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                });
        final int AW = 36;
        final int AH = 33;

        LCD.setAutoRefresh(false);
        for (int i = 0; i <= SH; i++)
        {
            g.drawImage(duke, 0, i - SH, 0);
            LCD.refresh();
            Delay.msDelay(20);
        }
        Delay.msDelay(1000);
        for (int wavecnt = 0; wavecnt < 3; wavecnt++)
        {
            for (int i = 0; i < 6; i++)
            {
                g.drawRegion(arms, AW * i, 0, AW, AH, 0, 51, 0, 0);
                LCD.refresh();
                Delay.msDelay(50);
            }
            for (int i = 7 - 1; i >= 0; i--)
            {
                g.drawRegion(arms, AW * i, 0, AW, AH, 0, 51, 0, 0);
                LCD.refresh();
                Delay.msDelay(50);
            }
            g.drawRegion(duke, 51, 0, AW, AH, 0, 51, 0, 0);
            LCD.refresh();
            Delay.msDelay(50);
        }

        Delay.msDelay(1000);
        // Remove the image using a split display...
        for (int i = 0; i < SW; i++)
        {
            g.drawRegionRop(duke, 0, 0, SW, SH, -i, 0, 0, 0x55aa00);
            g.drawRegionRop(duke, 0, 0, SW, SH, i, 0, 0, 0xaa5500);
            LCD.refresh();
            //Delay.msDelay(20);
        }
        LCD.setAutoRefresh(true);
        LCD.refresh();
        Button.waitForPress(DELAY);
    }

    public static void main(String[] options) throws Exception
    {
        GraphicsSample sample = new GraphicsSample();
        sample.titles();
        sample.characterSet();
        sample.textAnchors();
        sample.fonts();
        sample.rotatedText();
        //sample.fileImage();
        sample.lines();
        sample.rectangles();
        sample.circles();
        sample.scroll();
        sample.images();
        sample.animation();
        sample.credits();
    }
}
