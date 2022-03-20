package tk.kqstone.dedd;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public final class TeethImage {
	private static final int[] SITES = { 11, 12, 13, 21, 22, 23 };
	private static final String SUFFIX_DIR = "/img/t";
	private static final String SUFFIX_EXTENSION = ".png";
	private static final String SUFFIX_CONTOUR = "_contour";
	private static final String SUFFIX_REAL = "_real";
	private static final String SUFFIX_REAL_FRONT = "_real_front";
	private static Map<Integer, BufferedImage> teethContour = new HashMap<>();
	private static Map<Integer, BufferedImage> teethReal = new HashMap<>();
	private static Map<Integer, BufferedImage> teethRealFront = new HashMap<>();

	static {
		for (int site : SITES) {
			String path;
			path = SUFFIX_DIR + site + SUFFIX_CONTOUR + SUFFIX_EXTENSION;
			teethContour.put(site, creatImageFromPath(path));
			path = SUFFIX_DIR + site + SUFFIX_REAL + SUFFIX_EXTENSION;
			teethReal.put(site, creatImageFromPath(path));
			path = SUFFIX_DIR + site + SUFFIX_REAL_FRONT + SUFFIX_EXTENSION;
			teethRealFront.put(site, creatImageFromPath(path));
		}
	}

	public static BufferedImage getContour(int site) {
		return teethContour.get(site);
	}

	public static BufferedImage getReal(int site) {
		return teethReal.get(site);
	}

	public static BufferedImage getRealFront(int site) {
		return teethRealFront.get(site);
	}

	public static Map<Integer, BufferedImage> getTeethContour() {
		return teethContour;
	}

	public static Map<Integer, BufferedImage> getTeethReal() {
		return teethReal;
	}

	public static Map<Integer, BufferedImage> getTeethRealFront() {
		return teethRealFront;
	}

	private static BufferedImage creatImageFromPath(String path) {
		InputStream input = TeethImage.class.getResourceAsStream(path);
		BufferedImage image = null;
		try {
			image = ImageIO.read(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}
}
