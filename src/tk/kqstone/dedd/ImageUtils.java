package tk.kqstone.dedd;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.io.IOException;

public class ImageUtils {

	public static BufferedImage rotate(BufferedImage oriImage, double radians) {
		if (oriImage == null) {
			return null;
		}

		int imageWidth = oriImage.getWidth(null);
		int imageHeight = oriImage.getHeight(null);

		int type = oriImage.getColorModel().getTransparency();
		BufferedImage newImage = null;
		newImage = new BufferedImage(imageWidth, imageHeight, type);
		Graphics2D graphics = newImage.createGraphics();
		// 旋转角度
		graphics.rotate(radians, imageWidth / 2, imageHeight / 2);
		// 绘图
		graphics.drawImage(oriImage, null, null);
		return newImage;
	}

	public static BufferedImage crop(BufferedImage oriImage, Rect cropRect) {
		if (oriImage == null || cropRect == null) {
			return null;
		}

		// cropRect area is too small, return original image
		if (cropRect.getWidth() < 5 || cropRect.getHeight() < 5) {
			return oriImage;

		}
		int x1 = cropRect.getx1() > 0 ? cropRect.getx1() : 0;
		int y1 = cropRect.gety1() > 0 ? cropRect.gety1() : 0;
		int x2 = cropRect.getx2() > oriImage.getWidth() ? oriImage.getWidth() : cropRect.getx2();
		int y2 = cropRect.gety2() > oriImage.getHeight() ? oriImage.getHeight() : cropRect.gety2();

		BufferedImage image = oriImage.getSubimage(x1, y1, x2 - x1, y2 - y1);
		return image;
	}

	public static BufferedImage lumAdjustment(BufferedImage image, int param) throws IOException {
		if (image == null) {
			return null;
		} else {
			if (param == 0)
				return image;
			int width = image.getWidth();
			int height = image.getHeight();
			BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			int argb, A, R, G, B;
			for (int i = 0; i < image.getWidth(); i++) {
				for (int j = 0; j < image.getHeight(); j++) {
					argb = image.getRGB(i, j);
					A = ((argb >> 24) & 0xff);
					R = ((argb >> 16) & 0xff) + param;
					G = ((argb >> 8) & 0xff) + param;
					B = (argb & 0xff) + param;

					argb = ((A & 0xff) << 24) | ((clamp(R) & 0xff) << 16) | ((clamp(G) & 0xff) << 8)
							| ((clamp(B) & 0xff));
					output.setRGB(i, j, argb);
				}
			}
			return output;
		}
	}

	public static BufferedImage adjustBrightAndYellow(BufferedImage image, int brightChange, int yellowChange)
			throws IOException {
		if (image == null) {
			return null;
		} else {
			if (brightChange == 0 && yellowChange == 0)
				return image;
			int width = image.getWidth();
			int height = image.getHeight();
			BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			int argb, A, R, G, B;
			for (int i = 0; i < image.getWidth(); i++) {
				for (int j = 0; j < image.getHeight(); j++) {
					argb = image.getRGB(i, j);
					A = ((argb >> 24) & 0xff);
					if (A != 0) {
						R = ((argb >> 16) & 0xff) + brightChange + yellowChange;
						G = ((argb >> 8) & 0xff) + brightChange + yellowChange;
						B = (argb & 0xff) + brightChange - 2 * yellowChange;

						argb = ((A & 0xff) << 24) | ((clamp(R) & 0xff) << 16) | ((clamp(G) & 0xff) << 8)
								| ((clamp(B) & 0xff));
					}
					output.setRGB(i, j, argb);
				}
			}
			return output;
		}
	}

	public static BufferedImage yellowAdjustment(BufferedImage image, int param) throws IOException {
		if (image == null) {
			return null;
		} else {
			if (param == 0)
				return image;
			int width = image.getWidth();
			int height = image.getHeight();
			BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			int argb, A, R, G, B;
			for (int i = 0; i < image.getWidth(); i++) {
				for (int j = 0; j < image.getHeight(); j++) {
					argb = image.getRGB(i, j);
					A = ((argb >> 24) & 0xff);
					R = ((argb >> 16) & 0xff);
					G = ((argb >> 8) & 0xff);
					B = (argb & 0xff) - param;

					argb = ((A & 0xff) << 24) | ((clamp(R) & 0xff) << 16) | ((clamp(G) & 0xff) << 8)
							| ((clamp(B) & 0xff));
					output.setRGB(i, j, argb);
				}
			}
			return output;
		}
	}

	public static BufferedImage thumbnail(BufferedImage srcImage, double scale) {
		if (scale >= 1.0d)
			return srcImage;
		int width = (int) (srcImage.getWidth() * scale);
		int height = (int) (srcImage.getHeight() * scale);
		BufferedImage dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = dst.createGraphics();
		g2d.drawImage(srcImage, 0, 0, width, height, null);
		g2d.dispose();
		return dst;
	}

	private static int clamp(int rgb) {
		if (rgb > 255)
			return 255;
		if (rgb < 0)
			return 0;
		return rgb;
	}

	public static BufferedImage screenCapture(Rect rect) throws AWTException {
		Rectangle r = new Rectangle(rect.getx1(), rect.gety1(), rect.getWidth(), rect.getHeight());
		BufferedImage image = new Robot().createScreenCapture(r);
		return image;
	}

	@Deprecated
	public static BufferedImage rotate(BufferedImage oriImage, int angel) {
		if (oriImage == null) {
			return null;
		}
		if (angel < 0) {
			// 将负数角度，纠正为正数角度
			angel = angel + 360;
		}
		int imageWidth = oriImage.getWidth(null);
		int imageHeight = oriImage.getHeight(null);
		// 计算重新绘制图片的尺寸
		Rectangle rectangle = calculatorRotatedSize(new Rectangle(new Dimension(imageWidth, imageHeight)), angel);
		// 获取原始图片的透明度
		int type = oriImage.getColorModel().getTransparency();
		BufferedImage newImage = null;
		newImage = new BufferedImage(rectangle.width, rectangle.height, type);
		Graphics2D graphics = newImage.createGraphics();
		// 平移位置
		graphics.translate((rectangle.width - imageWidth) / 2, (rectangle.height - imageHeight) / 2);
		// 旋转角度
		graphics.rotate(Math.toRadians(angel), imageWidth / 2, imageHeight / 2);
		// 绘图
		graphics.drawImage(oriImage, null, null);
		return newImage;
	}

	private static Rectangle calculatorRotatedSize(Rectangle src, int angel) {
		if (angel >= 90) {
			if (angel / 90 % 2 == 1) {
				int temp = src.height;
				src.height = src.width;
				src.width = temp;
			}
			angel = angel % 90;
		}
		double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
		double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
		double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
		double angel_dalta_width = Math.atan((double) src.height / src.width);
		double angel_dalta_height = Math.atan((double) src.width / src.height);

		int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
		int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
		int des_width = src.width + len_dalta_width * 2;
		int des_height = src.height + len_dalta_height * 2;
		return new java.awt.Rectangle(new Dimension(des_width, des_height));
	}

}
