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

public class Converter {

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
		if (image == null || image.getType() != BufferedImage.TYPE_BYTE_BINARY) {
			return null;
		}
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
			case '\0':
				sb.append("\\0");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\\':
			case '"':
				sb.append('\\');
				sb.append((char) c);
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
		if (!stateMatcher.matches()) {
			return null;
		}
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

	public static BufferedImage getImageFromNxtImageCreateString(String string,int mode) {
		if (mode == BYTEA)
			return getImageFromNxtImageCreateString(string);
		
		Matcher m = STRING_PATTERN.matcher(string);
		if (!m.matches())
			//TODO properly report error
			return null;
		
		int w = Integer.parseInt(m.group(1));
		int h = Integer.parseInt(m.group(2));
		
//		for (int i=0; i<=m.groupCount(); i++)
//			System.out.println(i+" "+m.group(i));
		
		string = m.group(4);
		if (string == null)
			string = m.group(6);
		
		int stringlen = string.length();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try{
			for (int i = 0; i < string.length(); i++){
				char chr = string.charAt(i);
				if (chr == '\\')
				{
					i++;
					if (i >= stringlen)
						//TODO report whether string is too short
						return null;
					
					char chr2 = string.charAt(i); 
					switch (chr2)
					{
						case 'u':
							int j = i+5;
							if (j > stringlen)
								//TODO report whether string is too short
								return null;
							
							chr = (char)Integer.parseInt(string.substring(i+1, j), 16);
							i+=4;
							break;
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
							// see http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#101089
							int end = i + 1;
							int maxend = Math.min(i + ((chr2 < '4') ? 3 : 2), stringlen);
							while (end < maxend)
							{
								int chr3 = string.charAt(end);
								if (chr3 < '0' || chr3 > '7')
									break;
								
								end++;
							}
							chr = (char)Integer.parseInt(string.substring(i, end), 8);
							break;
						case 'b':
							chr = '\b';
							break;
						case 't':
							chr = 't';
							break;
						case 'n':
							chr = '\n';
							break;
						case 'f':
							chr = '\f';
							break;
						case 'r':
							chr = '\r';
							break;
						case '"':
						case '\'':
						case '\\':
							chr = chr2;
							break;
						default:
							//TODO report unknown escape
							return null;
					}
				}
				
				if (mode == BIT_16)
					os.write(chr >> 8);
				os.write(chr);
			}
		}catch(Exception e){
			//TODO properly report error to caller
			return null;
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
