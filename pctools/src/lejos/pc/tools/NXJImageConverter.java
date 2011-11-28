/*
 * Convert and image into a form suitable for use with the leJOS graphics
 * classes.
 *
 * Original code by Programus, imported to leJOS by Andy
 */

package lejos.pc.tools;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NXJImageConverter {

	private final static int WHITE = 0xffffffff;
	private final static int BLACK = 0;
	
	public final static int BIT_8 = 0;
	public final static int BIT_16 = 1;
	public final static int BYTEA = 2;
	
	private static final Pattern STRING_PATTERN = Pattern.compile("\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)((([^\"]|\\\\\")*)|\\s*\"(([^\"]|\\\\\")*)\"\\s*)");

	public static BufferedImage removeColor(BufferedImage colorImage, int threshold) {
		if (colorImage.getType() == BufferedImage.TYPE_BYTE_BINARY) {
			return colorImage;
		}
		int w = colorImage.getWidth();
		int h = colorImage.getHeight();
		int[] argbs = colorImage.getRGB(0, 0, w, h, null, 0, w);
		int[] bws = new int[argbs.length];
		for (int i = 0; i < argbs.length; i++) {
			bws[i] = getH(argbs[i]) > threshold ? WHITE : BLACK;
		}
		BufferedImage image = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		image.setRGB(0, 0, w, h, bws, 0, w);
		return image;
	}

	public static byte[] nxtImageConvert(BufferedImage image) {
		if (image == null || image.getType() != BufferedImage.TYPE_BYTE_BINARY)
			throw new IllegalArgumentException();
		
		int w = image.getWidth();
		int h = image.getHeight();
		int n = h >> 3;
		if (h > n << 3) {
			n++;
		}

		byte[] data = new byte[n * w];
		int index = 0;
		for (int i = 0; i < h; i += 8) {
			for (int j = 0; j < w; j++) {
				byte d = 0;
				for (int k = 7; k >= 0; k--) {
					d <<= 1;
					int x = j;
					int y = i + k;
					if (y < h) {
						int argb = image.getRGB(x, y);
						d |= (byte) ((argb & 0xffffff) > 0 ? 0 : 1);
					}
				}
				data[index++] = d;
			}
		}

		return data;
	}
	
	private static String getImageCreateString(byte[] data, Dimension size) {
		StringBuilder sb = new StringBuilder("new Image(");
		// width and height
		sb.append(size.width).append(", ").append(size.height).append(", ");
		// new byte[]
		sb.append("new byte[] {");
		for (byte b : data) {
 			sb.append("(byte) 0x");
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() < 2) {
				sb.append('0');
			}
			sb.append(hex);
			sb.append(", ");
		}

		sb.append("})");

		return sb.toString();
	}

	public static String getImageCreateString(byte[] data, Dimension size,int mode) {
		if (mode == BYTEA)
			return getImageCreateString(data,size);
		StringBuilder sb = new StringBuilder();
		// width and height
		//sb.append(size.width).append(", ").append(size.height).append(", ");
		// new byte[]
		sb.append("(");
		sb.append(size.width);
		sb.append(",");
		sb.append(size.height);
		sb.append(") \"");
		for (int i = 0; i < data.length; i++){
			int one = data[i] & 0xFF;
			if (mode == BIT_8){
				appendChar(sb, one);
			}
			else if (mode == BIT_16){
				i++;
				int two = (i < data.length) ? data[i] & 0xFF : 0;
				appendChar(sb, (one << 8) | two);
			}
		}
		sb.append('"');
		
		return sb.toString();
	}
	
	private static void appendChar(StringBuilder sb, int c)
	{
		switch (c)
		{
			case '\r':
				sb.append("\\r");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			default:
				sb.append("\\u");
				String hex = Integer.toHexString(c & 0xFFFF);
				for (int i=hex.length(); i<4; i++)
					sb.append('0');
				sb.append(hex);
		}
	}
	
	private static BufferedImage getImageFromNxtImageCreateString(String string) {
		Pattern statePattern = Pattern.compile(".*\\s*new\\s+Image\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*new\\s+byte\\s*\\[\\s*\\]\\s*" +
			"\\{\\s*([^}]*)\\s*\\}\\s*\\)\\s*[;]?\\s*", Pattern.MULTILINE);
		Matcher stateMatcher = statePattern.matcher(string);
		if (!stateMatcher.matches())
			throw new NumberFormatException("illegal format");
		
		int w = Integer.parseInt(stateMatcher.group(1));
		int h = Integer.parseInt(stateMatcher.group(2));
		String byteArray = stateMatcher.group(3);
		Pattern byteDigitalPattern = Pattern.compile("\\s*(\\(\\s*byte\\s*\\))?\\s*((0x)?[0-9A-Fa-f]+)\\s*[,]?\\s*");
		Matcher byteDigitalMatcher = byteDigitalPattern.matcher(byteArray);
		int start = 0;
		int end = 0;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		while (byteDigitalMatcher.find(end)) {
			start = byteDigitalMatcher.start(2);
			end = byteDigitalMatcher.end(2);
			String str = byteArray.substring(start, end);
			int i = str.startsWith("0x") ? Integer.parseInt(str.substring(2), 16) : str.startsWith("0") ? Integer.parseInt(str, 8) : Integer.parseInt(str);
			os.write(i);
		}

		return nxtImageData2Image(os.toByteArray(), w, h);
	}
	
	private static String decodeUnicodeEscapes(String s) throws NumberFormatException
	{
		StringBuilder sb = new StringBuilder();
		int len = s.length();
		
		for (int i=0; i<len;)
		{
			char c1 = s.charAt(i++);
			if (c1 != '\\' || i >= len)
				sb.append(c1);
			else
			{
				char c2 = s.charAt(i++);
				if (c2 != 'u')
				{
					sb.append(c1);
					sb.append(c2);
				}
				else
				{
					int j = i+4;
					if (j > len)
						throw new NumberFormatException("incomplete unicode escape");
					
					int tmp = Integer.parseInt(s.substring(i, j), 16);
					if (tmp < 0)
						throw new NumberFormatException("illegal unicode escape");
					
					i = j;
					sb.append((char)tmp);
				}
			}
		}
		return sb.toString();
	}
	
	private static String decodeOtherEscapes(String s) throws NumberFormatException
	{
		StringBuilder sb = new StringBuilder();
		int len = s.length();
		
		for (int i = 0; i < s.length();){
			char chr1 = s.charAt(i++);
			if (chr1 == '\\')
			{
				if (i >= len)
					throw new NumberFormatException("incomplete escape sequence");
				
				char chr2 = s.charAt(i++); 
				switch (chr2)
				{
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
						// see http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#101089
						int r = chr2 - '0';
						int maxend = Math.min(i + (r < 4 ? 2 : 1), len);
						while (i < maxend)
						{
							int chr3 = s.charAt(i);
							if (chr3 < '0' || chr3 > '7')
								break;
							
							i++;
							r = (r << 3) + chr3 - '0';
						}
						chr1 = (char)r;
						break;
					case 'b':
						chr1 = '\b';
						break;
					case 't':
						chr1 = 't';
						break;
					case 'n':
						chr1 = '\n';
						break;
					case 'f':
						chr1 = '\f';
						break;
					case 'r':
						chr1 = '\r';
						break;
					case '"':
					case '\'':
					case '\\':
						chr1 = chr2;
						break;
					default:
						throw new NumberFormatException("unknown escape character");
				}
			}
			
			sb.append(chr1);
		}
		return sb.toString();
	}

	public static BufferedImage getImageFromNxtImageCreateString(String string, int mode)
	{
		/**
		 * Note: AFAIK, the Java language specification explains, that decoding has to be done
		 * in two stages: first decode the unicode-escapes and then all other escapes. During
		 * first state, the unicode-escapes may decode to backslashes, line feeds, quotes, and
		 * other characters, which are considered as normal character during second state, that
		 * is when decoding other escapes like \r, \n, \123, etc. 
		 */
		string = decodeUnicodeEscapes(string);
		if (mode == BYTEA)
			return getImageFromNxtImageCreateString(string);
		
		Matcher m = STRING_PATTERN.matcher(string);
		if (!m.matches())
			throw new NumberFormatException("illegal format");
		
		int w = Integer.parseInt(m.group(1));
		int h = Integer.parseInt(m.group(2));
		
//		for (int i=0; i<=m.groupCount(); i++)
//			System.out.println(i+" "+m.group(i));
		
		string = m.group(4);
		if (string == null)
			string = m.group(6);
		
		string = decodeOtherEscapes(string);
		
		int len = string.length();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		for (int i=0; i<len; i++)
		{
			char c = string.charAt(i);
			
			if (mode == BIT_16)
				os.write(c >> 8);
			else if (c >= 0x100)
				throw new NumberFormatException("16 bit character found");
			
			os.write(c);
		}
		
		return nxtImageData2Image(os.toByteArray(), w, h);
	}

	public static BufferedImage nxtImageData2Image(byte[] byteList, int w, int h) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
		int x = 0;
		int y = 0;
		int[] bws = new int[w * h];
		for (byte data : byteList) {
			for (int dy = 0; dy < 8; dy++) {
				int yy = y + dy;
				if (yy >= h) {
					break;
				}
				int index = yy * w + x;
				bws[index] = (data & 0x01) > 0 ? BLACK : WHITE;
				data >>>= 1;
			}
			x++;
			if (x >= w) {
				x = 0;
				y += 8;
			}
		}
		image.setRGB(0, 0, w, h, bws, 0, w);
		return image;
	}

	private static int getH(int argb) {
		int b = (argb & 0xff);
		int g = ((argb >>> 8) & 0xff);
		int r = ((argb >>> 16) & 0xff);
		int h = (r<<14) + (r<<12) + (g<<15) + (g<<12) + (b<<13);
		return h >> 16;
	}
}
