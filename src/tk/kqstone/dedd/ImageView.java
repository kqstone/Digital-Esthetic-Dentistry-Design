package tk.kqstone.dedd;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * ImageView show image with image width/height ratio
 * 
 * @author kqstone
 *
 */
public class ImageView extends ZoomableJPanel {
	public final static int SCALE_ADAPT_WIDTH = 1;
	public final static int SCALE_ADAPT_HEIGHT = 2;
	public final static int SCALE_STRETCH = 0;
	private BufferedImage image;
	protected float proportion = 1.0f;
	protected int offsetX = 0;
	protected int offsetY = 0;

	private String text;
	private double scale = 1.0d;
	private int scaleType = SCALE_ADAPT_WIDTH;

	private Rectangle drawRect;

	private ImageChangeListener imageChangeListener;

	public int getScaleType() {
		return scaleType;
	}

	public double getScale() {
		return scale;
	}

	public Rectangle getDrawRect() {
		if (drawRect == null) {
			drawRect = new Rectangle(0, 0, 0, 0);
		}
		return drawRect;
	}

	public ImageView() {
		super();
		this.setOpaque(false);
		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				repaint();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * @param image to be shown in ImageView
	 */
	public void setImage(BufferedImage image) {

		this.image = image;
		if (image == null) {
			this.repaint();
			return;
		}
		if (imageChangeListener != null)
			imageChangeListener.imageChanged(image);
		int panlW = this.getWidth();
		int panlH = this.getHeight();
		int imgW = image.getWidth();
		int imgH = image.getHeight();
		int x;
		int y;
		int width;
		int height;
		double scale;
		if (imgW * panlH > imgH * panlW) {
			x = 0;
			width = panlW;
			height = imgH * panlW / imgW;
			y = (panlH - height) / 2;
			scale = (double) panlW / imgW;
			scaleType = SCALE_ADAPT_WIDTH;
		} else {
			y = 0;
			height = panlH;
			width = imgW * panlH / imgH;
			x = (panlW - width) / 2;
			scale = (double) panlH / imgH;
			scaleType = SCALE_ADAPT_HEIGHT;
		}
		this.scale = scale;
		this.drawRect = new Rectangle(x, y, width, height);
		this.repaint();
	}

	public void setText(String text) {
		this.text = text;
		this.repaint();
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public void addImageChangeListener(ImageChangeListener listener) {
		this.imageChangeListener = listener;
	}

	/**
	 * show full image with correct ratio, without stretch or crop
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			int panlW = this.getWidth();
			int panlH = this.getHeight();
			int imgW = image.getWidth();
			int imgH = image.getHeight();
			int x;
			int y;
			int width;
			int height;
			double scale;
			if (imgW * panlH > imgH * panlW) {
				x = 0;
				width = panlW;
				height = imgH * panlW / imgW;
				y = (panlH - height) / 2;
				scale = (double) panlW / imgW;
				scaleType = SCALE_ADAPT_WIDTH;
			} else {
				y = 0;
				height = panlH;
				width = imgW * panlH / imgH;
				x = (panlW - width) / 2;
				scale = (double) panlH / imgH;
				scaleType = SCALE_ADAPT_HEIGHT;
			}
			this.scale = scale;
			this.drawRect = new Rectangle(x, y, width, height);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(image, Math.round(x * proportion + offsetX), Math.round(y * proportion + offsetY),
					Math.round(width * proportion), Math.round(height * proportion), null);
		} else {
			if (text != null) {
				FontMetrics fm = getFontMetrics(getFont());
				int fw = fm.stringWidth(text);
				int fh = fm.getHeight();
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.drawString(text, (this.getWidth() - fw) / 2, (this.getHeight() - fh) / 2);
			}
		}
	}

	@Override
	public synchronized void zoom(float proportion, int offsetX, int offsetY) {

		this.proportion = proportion;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.repaint();
	}

}