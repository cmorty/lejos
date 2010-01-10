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
