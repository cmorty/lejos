package javax.microedition.lcdui;

import lejos.nxt.LCD;

/**
 * Preliminary Graphics class for LCD Screen
 * @author Brian Bagnall
 *
 */
public class Graphics
{

    /** drawArc and fillArc accuracy parameter */
    private static final int ARC_ACC = 5;

    /* Public color definitions */
    public static final int BLACK = 1;
    public static final int WHITE = 0;

    /* Public line stroke definitions */
    public static final int SOLID = 0;
    public static final int DOTTED = 2;
    private int rgbColor = BLACK;
    private int strokeStyle = SOLID;
    private Font font = Font.getDefaultFont();
    private byte[] imageBuf;
    private int width;
    private int height;

    /**
     * Default constructor returns a context that can be used to access the NXT
     * LCD display.
     */
    public Graphics()
    {
        imageBuf = LCD.getDisplay();
        width = LCD.SCREEN_WIDTH;
        height = LCD.SCREEN_HEIGHT;
    }

    /**
     * Create a graphics object that can be used to access the supplied memory
     * buffer.
     * @param data The memory buffer
     * @param width width of the buffer
     * @param height height of the buffer
     */
    public Graphics(byte []data, int width, int height)
    {
        imageBuf = data;
        this.width = width;
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getCenteredX(String str)
    {
        return width/2 - font.stringWidth(str)/2;
    }

    public Font getFont()
    {
        return font;
    }

    public void setColor(int rgb)
    {
        rgbColor = rgb;
    }

    public int getColor()
    {
        return rgbColor;
    }

    /**
     * Method to set a pixel to screen.
     * @param rgbColor the pixel color (0 = white, 1 = black)
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setPixel(int rgbColor, int x, int y)
    {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return; // Test-Modify for speed
        int bit = (y & 0x7);
        int index = (y / 8) * width + x;
        imageBuf[index] = (byte) ((imageBuf[index] & ~(1 << bit)) | (rgbColor << bit));
    }

    /**
     * Method to get a pixel from the screen.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the pixel color (0 = white, 1 = black)
     */
    public int getPixel(int x, int y)
    {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return 0;
        int bit = (y & 0x7);
        int index = (y / 8) * width + x;
        return ((imageBuf[index] >> bit) & 1);
    }

    /**
     * Output a string to the display. Allow any location. Allow the string to
     * be inverted.
     * @param str String to display
     * @param x X location (pixels)
     * @param y Y location (pixels)
     * @param invert set to true to displayed the string inverted
     */
    public void drawString(String str, int x, int y, boolean invert)
    {
        char[] strData = str.toCharArray();
        int w = font.width;
        if (invert)
        {
            for (int i = 0; (i < strData.length); i++)
            {
                drawChar(strData[i], x + i * w, y, true);
            }
        } else
        {
            for (int i = 0; (i < strData.length); i++)
            {
                drawChar(strData[i], x + i * w, y, LCD.ROP_COPY);
            }
        }

    }

    /**
     * Draw a single char to an arbitrary location on the screen.
     * @param c Character to display
     * @param x X location (pixels)
     * @param y Y location (pixels)
     * @param invert Set to true to invert the display
     */
    public void drawChar(char c, int x, int y, boolean invert)
    {
        int gw = font.glyphWidth;
        int gh = font.height;
        LCD.bitBlt(font.glyphs, gw * 128, gh, gw * c, 0, imageBuf, width, height, x, y, gw, gh, (invert ? LCD.ROP_COPYINVERTED : LCD.ROP_COPY));
        if (invert)
            LCD.bitBlt(imageBuf, width, height, 0, 0, imageBuf, width, height, x + gw, y, 1, gh, LCD.ROP_SET);
    }

    /**
     * Output a string to the display. Allow any location. Allow use of raster
     * operations.
     * @param str String to display
     * @param x X location (pixels)
     * @param y Y location (pixels)
     * @param rop Raster operation
     */
    public void drawString(String str, int x, int y, int rop)
    {
        char[] strData = str.toCharArray();
        int w = font.width;
        for (int i = 0; (i < strData.length); i++)
        {
            drawChar(strData[i], x + i * w, y, rop);
        }
    }

    /**
     * Draw a single char to an arbitrary location on the screen.
     * @param c Character to display
     * @param x X location (pixels)
     * @param y Y location (pixels)
     * @param rop Raster operation for how to combine with existing content
     */
    public void drawChar(char c, int x, int y, int rop)
    {
        int gw = font.glyphWidth;
        int gh = font.height;
        LCD.bitBlt(font.glyphs, gw * 128, gh, gw * c, 0, imageBuf, width, height, x, y, gw, gh, rop);
    }

    public void drawLine(int x0, int y0, int x1, int y1)
    {
        drawLine(x0, y0, x1, y1, strokeStyle);
    }

    private void drawLine(int x0, int y0, int x1, int y1, int style)
    {
        // Uses Bresenham's line algorithm
        int dy = y1 - y0;
        int dx = x1 - x0;
        int stepx, stepy;
        boolean skip = false;

        if (dy < 0)
        {
            dy = -dy;
            stepy = -1;
        } else
        {
            stepy = 1;
        }
        if (dx < 0)
        {
            dx = -dx;
            stepx = -1;
        } else
        {
            stepx = 1;
        }
        if (style == SOLID)
        {
            // Special case horizontal and vertical lines
            if (dy == 0 || dx == 0)
            {
                LCD.bitBlt(imageBuf, width, height, 0, 0, imageBuf, width, height, (stepx == 1 ? x0 : x1), (stepy == 1 ? y0 : y1),
                        (dx == 0 ? 1 : dx + 1), (dy == 0 ? 1 : dy + 1), (rgbColor == BLACK ? LCD.ROP_SET : LCD.ROP_CLEAR));
                return;
            }
        }
        dy <<= 1; // dy is now 2*dy
        dx <<= 1; // dx is now 2*dx

        setPixel(rgbColor, x0, y0);
        if (dx > dy)
        {
            int fraction = dy - (dx >> 1);  // same as 2*dy - dx
            while (x0 != x1)
            {
                if (fraction >= 0)
                {
                    y0 += stepy;
                    fraction -= dx; // same as fraction -= 2*dx
                }
                x0 += stepx;
                fraction += dy; // same as fraction -= 2*dy
                if ((style == SOLID) || !skip)
                    setPixel(rgbColor, x0, y0);
                skip = !skip;
            }
        } else
        {
            int fraction = dx - (dy >> 1);
            while (y0 != y1)
            {
                if (fraction >= 0)
                {
                    x0 += stepx;
                    fraction -= dy;
                }
                y0 += stepy;
                fraction += dx;
                if ((style == SOLID) || !skip)
                    setPixel(rgbColor, x0, y0);
                skip = !skip;
            }
        }
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        drawArc(x, y, width, height, startAngle, arcAngle, strokeStyle, false);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
    {
        // drawArc is for now only SOLID
        drawArc(x, y, width, height, startAngle, arcAngle, SOLID, true);
    }

    private void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle,
            int style, boolean fill)
    {
        // Scale up width and height to create more accurate ellipse form
        int xscale = (width < height) ? ARC_ACC : ((ARC_ACC * width + (width >> 1)) / height);
        int yscale = (width < height) ? ((ARC_ACC * height + (height >> 1)) / width) : ARC_ACC;

        // Calculate x, y center and radius from upper left corner
        int x0 = x + (width >> 1);
        int y0 = y + (height >> 1);
        int radius = (width < height) ? (width >> 1) : (height >> 1);
        while (startAngle < 0)
            startAngle += 360;
        while (startAngle > 360)
            startAngle -= 360;
        while (arcAngle > 360)
            arcAngle -= 360;
        while (arcAngle < -360)
            arcAngle += 360;  // negative arc angle is OK
        // Check and set start and end angle
        int endAngle = startAngle + arcAngle;
//      while (endAngle < 0)
//      {
//         endAngle = endAngle + 360;
//      }
//      while (endAngle > 360)
//      {
//         endAngle = endAngle - 360;
//      }
//      if (arcAngle < 0)
//      { // Switches start and end
//         int temp = startAngle;
//         startAngle = endAngle;
//         endAngle = (temp > 0) ? temp : temp + 360;  // was just :temp
//      }
        if (arcAngle >= 0)
        {
            if (endAngle > 360)  // need 2 segments
            {
                drawTheArc(radius, style, fill, xscale, yscale, startAngle, 360, x0, y0);
                drawTheArc(radius, style, fill, xscale, yscale, 0, endAngle - 360, x0, y0);
            } else
                drawTheArc(radius, style, fill, xscale, yscale, startAngle, endAngle, x0, y0);
        } //  else draw arc  frem end to start
        else if (endAngle < 0) // need 2 segments
        {
            drawTheArc(radius, style, fill, xscale, yscale, endAngle + 360, 360, x0, y0);
            drawTheArc(radius, style, fill, xscale, yscale, 0, startAngle, x0, y0);
        } else
            drawTheArc(radius, style, fill, xscale, yscale, endAngle, startAngle, x0, y0);

    }

    private void drawTheArc(int radius, int style, boolean fill, int xscale,
            int yscale, int startAngle, int endAngle, int x0, int y0)
    {
        // Initialize scaled up Bresenham's circle algorithm
        int f = (1 - ARC_ACC * radius);
        int ddF_x = 0;
        int ddF_y = -2 * ARC_ACC * radius;
        int xc = 0;
        int yc = ARC_ACC * radius;
        int dotskip = 0;
        while (xc < yc)
        {
            if (f >= 0)
            {
                yc--;
                ddF_y += 2;
                f += ddF_y;
            }

            xc++;
            ddF_x += 2;
            f += ddF_x + 1;

            // Skip points for dotted version
            dotskip = (dotskip + 1) % (2 * ARC_ACC);
            if ((style == DOTTED) && !fill && (dotskip < ((2 * ARC_ACC) - 1)))
                continue;

            // Scale down again
            int xxp = (xc * xscale + (xscale >> 1)) / (ARC_ACC * ARC_ACC);
            int xyp = (xc * yscale + (yscale >> 1)) / (ARC_ACC * ARC_ACC);
            int yyp = (yc * yscale + (yscale >> 1)) / (ARC_ACC * ARC_ACC);
            int yxp = (yc * xscale + (xscale >> 1)) / (ARC_ACC * ARC_ACC);

            // Calculate angle for partly circles / ellipses
            // NOTE: Below, (float) should not be needed. Not sure why Math.round() only accepts float.
            int tp = (int) Math.round(Math.toDegrees(Math.atan2(yc, xc)));
            if (fill)
            {
                /* TODO: Optimize more by drawing horizontal lines */
                if (((90 - tp) >= startAngle) && ((90 - tp) <= endAngle))
                    drawLine(x0, y0, x0 + yxp, y0 - xyp, style); // 0   - 45 degrees
                if ((tp >= startAngle) && (tp <= endAngle))
                    drawLine(x0, y0, x0 + xxp, y0 - yyp, style); // 45  - 90 degrees
                if (((180 - tp) >= startAngle) && ((180 - tp) <= endAngle))
                    drawLine(x0, y0, x0 - xxp, y0 - yyp, style); // 90  - 135 degrees
                if (((180 - (90 - tp)) >= startAngle) && ((180 - (90 - tp)) <= endAngle))
                    drawLine(x0, y0, x0 - yxp, y0 - xyp, style); // 135 - 180 degrees
                if (((270 - tp) >= startAngle) && ((270 - tp) <= endAngle))
                    drawLine(x0, y0, x0 - yxp, y0 + xyp, style); // 180 - 225 degrees
                if (((270 - (90 - tp)) >= startAngle) && ((270 - (90 - tp)) <= endAngle))
                    drawLine(x0, y0, x0 - xxp, y0 + yyp, style); // 225 - 270 degrees
                if (((360 - tp) >= startAngle) && ((360 - tp) <= endAngle))
                    drawLine(x0, y0, x0 + xxp, y0 + yyp, style); // 270 - 315 degrees
                if (((360 - (90 - tp)) >= startAngle) && ((360 - (90 - tp)) <= endAngle))
                    drawLine(x0, y0, x0 + yxp, y0 + xyp, style); // 315 - 360 degrees
            } else
            {
                if (((90 - tp) >= startAngle) && ((90 - tp) <= endAngle))
                    setPixel(rgbColor, x0 + yxp, y0 - xyp); // 0   - 45 degrees
                if ((tp >= startAngle) && (tp <= endAngle))
                    setPixel(rgbColor, x0 + xxp, y0 - yyp); // 45  - 90 degrees
                if (((180 - tp) >= startAngle) && ((180 - tp) <= endAngle))
                    setPixel(rgbColor, x0 - xxp, y0 - yyp); // 90  - 135 degrees
                if (((180 - (90 - tp)) >= startAngle) && ((180 - (90 - tp)) <= endAngle))
                    setPixel(rgbColor, x0 - yxp, y0 - xyp); // 135 - 180 degrees
                if (((270 - tp) >= startAngle) && ((270 - tp) <= endAngle))
                    setPixel(rgbColor, x0 - yxp, y0 + xyp); // 180 - 225 degrees
                if (((270 - (90 - tp)) >= startAngle) && ((270 - (90 - tp)) <= endAngle))
                    setPixel(rgbColor, x0 - xxp, y0 + yyp); // 225 - 270 degrees
                if (((360 - tp) >= startAngle) && ((360 - tp) <= endAngle))
                    setPixel(rgbColor, x0 + xxp, y0 + yyp); // 270 - 315 degrees
                if (((360 - (90 - tp)) >= startAngle) && ((360 - (90 - tp)) <= endAngle))
                    setPixel(rgbColor, x0 + yxp, y0 + xyp); // 315 - 360 degrees
            }
        }
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
    {

        int xc = x + (width / 2);
        int yc = y + (height / 2);
        int a = arcWidth / 2;
        int b = arcHeight / 2;

        int translateX = (width / 2) - (arcWidth / 2);
        int translateY = (height / 2) - (arcHeight / 2);

        // Draw 4 sides:
        int xDiff = arcWidth / 2;
        int yDiff = arcHeight / 2;
        drawLine(x, y + yDiff, x, y + height - yDiff);
        drawLine(x + width, y + yDiff, x + width, y + height - yDiff);
        drawLine(x + xDiff, y, x + width - xDiff, y);
        drawLine(x + xDiff, y + height, x + width - xDiff, y + height);


        /* e(x,y) = b^2*x^2 + a^2*y^2 - a^2*b^2 */
        int xxx = 0, yyy = b;
        int a2 = a * a, b2 = b * b;
        int crit1 = -(a2 / 4 + a % 2 + b2);
        int crit2 = -(b2 / 4 + b % 2 + a2);
        int crit3 = -(b2 / 4 + b % 2);
        int t = -a2 * yyy; /* e(xxx+1/2,y-1/2) - (a^2+b^2)/4 */
        int dxt = 2 * b2 * xxx, dyt = -2 * a2 * yyy;
        int d2xt = 2 * b2, d2yt = 2 * a2;

        while (yyy >= 0 && xxx <= a)
        {
            setPixel(BLACK, xc + xxx + translateX, yc + yyy + translateY); // Q4
            if (xxx != 0 || yyy != 0)
                setPixel(BLACK, xc - xxx - translateX, yc - yyy - translateY); // Q2
            if (xxx != 0 && yyy != 0)
            {
                setPixel(BLACK, xc + xxx + translateX, yc - yyy - translateY); // Q1
                setPixel(BLACK, xc - xxx - translateX, yc + yyy + translateY); // Q3
            }
            if (t + b2 * xxx <= crit1
                    || /* e(xxx+1,y-1/2) <= 0 */ t + a2 * yyy <= crit3)      /* e(xxx+1/2,y) <= 0 */

            {
                xxx++;
                dxt += d2xt;
                t += dxt;
            } // incx()
            else if (t - a2 * yyy > crit2) /* e(xxx+1/2,y-1) > 0 */

            {
                yyy--;
                dyt += d2yt;
                t += dyt;
            } else
            {
                {
                    xxx++;
                    dxt += d2xt;
                    t += dxt;
                } // incx()
                {
                    yyy--;
                    dyt += d2yt;
                    t += dyt;
                }
            }
        }
    }

    public void drawRect(int x, int y, int width, int height)
    {
        if ((width < 0) || (height < 0))
            return;

        if (height == 0 || width == 0)
        {
            drawLine(x, y, x + width, y + height);
        } else
        {
            drawLine(x, y, x + width - 1, y);
            drawLine(x + width, y, x + width, y + height - 1);
            drawLine(x + width, y + height, x + 1, y + height);
            drawLine(x, y + height, x, y + 1);
        }
    }

    public void fillRect(int x, int y, int w, int h)
    {
        if ((w < 0) || (h < 0))
            return;
        LCD.bitBlt(imageBuf, width, height, 0, 0, imageBuf, width, height, x, y, w, h, (rgbColor == BLACK ? LCD.ROP_SET : LCD.ROP_CLEAR));
    }

    public void drawString(String str, int x, int y)
    {
        drawString(str, x, y, LCD.ROP_COPY);
    }

    public void drawImage(Image img, int x, int y, boolean invert)
    {
        if (img == null)
        {
            return;
        }
        LCD.bitBlt(img.getData(), img.getWidth(), img.getHeight(), 0, 0, imageBuf, width, height, x, y, img.getWidth(), img.getHeight(), (invert ? LCD.ROP_COPYINVERTED : LCD.ROP_COPY));
    }

    public void drawImage(Image img, int sx, int sy, int x, int y, int w, int h, int rop)
    {
        if (img == null)
            LCD.bitBlt(imageBuf, width, height, 0, 0, imageBuf, width, height, x, y, w, h, rop);
        else
            LCD.bitBlt(img.getData(), img.getWidth(), img.getHeight(), sx, sy, imageBuf, width, height, x, y, w, h, rop);
    }

    public int getStrokeStyle()
    {
        return strokeStyle;
    }

    public void setStrokeStyle(int style)
    {
        if (style != SOLID && style != DOTTED)
        {
            throw new IllegalArgumentException();
        }
        strokeStyle = style;
    }

    // Temp for testing purposes until Canvas made.
    public void refresh()
    {
        LCD.setDisplay();
        LCD.refresh();
    }
    // Temp method for testing. Clears out graphics buffer
    // and refreshes screen.

    public void clear()
    {
        LCD.bitBlt(imageBuf, width, height, 0, 0, imageBuf, width, height, 0, 0, width, height, LCD.ROP_CLEAR);
    }

    public void autoRefresh(boolean on)
    {
        LCD.setAutoRefresh(on);
    }
}

