/*
 * Convert and image into a form suitable for use with the leJOS graphics
 * classes.
 *
 * Original code by Programus, imported to leJOS by Andy
 */

package lejos.pc.tools;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {

	private final static int WHITE = 0xffffffff;
	private final static int BLACK = 0;
	
	public final static int BIT_8 = 0;
	public final static int BIT_16 = 1;
	public final static int BYTEA = 2;

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
	
	public static String getImageCreateString(byte[] data, Dimension size) {
		StringBuilder sb = new StringBuilder("new Image(");
		// width and height
		sb.append(size.width).append(", ").append(size.height).append(", ");
		// new byte[]
		sb.append("new byte[] {");
		for (byte b : data) {
 			sb.append("(byte) 0x");
			String hex = Integer.toHexString(0xff & (int)b);
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
		StringBuilder sb = new StringBuilder("");
		// width and height
		//sb.append(size.width).append(", ").append(size.height).append(", ");
		// new byte[]
		sb.append("(");
		sb.append(size.width).append(",").append(size.height);
		sb.append(")");
		for (int i = 0; i < data.length; i+=(mode+1)){
			byte one = data[i];
			String hexOne = Integer.toHexString(0xff & (int)one);
			if (mode == BIT_8){
				if (one == (byte)0x00)
					sb.append("\\0");
				else{
					sb.append("\\u00");
					if (hexOne.length() < 2)
						sb.append("0");
					sb.append(hexOne);
				}
			}
			else if (mode == BIT_16){
				byte two = data[i+1];
				String hexTwo = Integer.toHexString(0xff & (int)two);
				if (one == (byte)0x00 && two == (byte)0x00)
					sb.append("\\0");
				else{
					sb.append("\\u");
					if (hexOne.length() < 2)
						sb.append("0");
					sb.append(hexOne);
					if (hexTwo.length() < 2)
						sb.append("0");
					sb.append(hexTwo);
				}
			}
		}

		return sb.toString();
	}
	
	public static BufferedImage getImageFromNxtImageCreateString(String string) {
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
		List<Byte> byteList = new LinkedList<Byte>();
		while (byteDigitalMatcher.find(end)) {
			start = byteDigitalMatcher.start(2);
			end = byteDigitalMatcher.end(2);
			String str = byteArray.substring(start, end);
			int i = str.startsWith("0x") ? Integer.parseInt(str.substring(2), 16) : str.startsWith("0") ? Integer.parseInt(str, 8) : Integer.parseInt(str);
			byteList.add((byte) i);
		}

		return NxtImageData2Image(byteList, w, h);
	}

	public static BufferedImage getImageFromNxtImageCreateString(String string,int mode) {
		if (mode == BYTEA)
			return getImageFromNxtImageCreateString(string);
		List<Byte> byteList = new LinkedList<Byte>();
		int w = 0;
		int h = 0;
		try{
		for (int i = 0; i < string.length(); i++){
			char chr = string.charAt(i);
			if (chr == '\\'){
				if (string.charAt(i+1) == 'u'){
					if (mode == BIT_16)
						byteList.add(Integer.decode("0X"+string.substring(i+2, i+4)).byteValue());
					byteList.add(Integer.decode("0X"+string.substring(i+4,i+6)).byteValue());
					i+=5;
				}
				else{
					// TODO Add More Escape Character Convertors
					if (string.charAt(i+1) == '0'){
						if (mode == BIT_16)
							byteList.add((byte)0x00);
						byteList.add((byte)0x00);
						i+=1;
					}
				}
			}
			else if (chr == '('){ // Process Height/Width
				String dim = string.substring(i+1, string.indexOf(")",i));
				String[] split = dim.split(",");
				w = Integer.parseInt(split[0]);
				h = Integer.parseInt(split[1]);
				i = string.indexOf(")",i);
			}
			else{
				if (mode == BIT_16)
					byteList.add((byte)0x00);
				byteList.add((byte)chr);
			}
		}
		return NxtImageData2Image(byteList, w, h);
		}catch(Exception e){
			return null;
		}
	}

	public static BufferedImage NxtImageData2Image(List<Byte> byteList, int w, int h) {
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
		argb >>>= 8;
		int g = (argb & 0xff);
		argb >>>= 8;
		int r = (argb & 0xff);
		r <<= 16;
		g <<= 16;
		b <<= 16;
		int h = (r>>2) + (r>>4) + (g>>1) + (g>>4) + (b>>3);
		return h >> 16;
	}
}
