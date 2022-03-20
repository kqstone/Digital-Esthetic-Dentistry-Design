package tk.kqstone.dedd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Bordered Panel contains draw method
 * 
 * @author kqstone
 */
public class DrawableBorderRect extends BorderRect {
	public static final Color DEFAULT_BORDER_COLOR = Color.BLUE;

	public static final float DEFAULT_SHOW_BORDER_WIDTH = 1.5f;

	public static final Color DEFAULT_PIT_COLOR = Color.GREEN;

	private Color borderColor = DEFAULT_BORDER_COLOR;

	/**
	 * showBorderWidth: width for Graphics to draw border
	 */
	private float showBorderWidth = DEFAULT_SHOW_BORDER_WIDTH;

	private Color pitColor = DEFAULT_PIT_COLOR;

	private BufferedImage image;

	private boolean drawBorder;

	private boolean drawImage;

	private int id;

	public DrawableBorderRect() {
		super();
		drawBorder = true;
		drawImage = true;
	}

	/**
	 * set border color
	 * 
	 * @param color Color
	 */
	public void setBorderColor(Color color) {
		borderColor = color;
	}

	public Color getBorderColor() {
		return this.borderColor;
	}

	public float getShowBorderWidth() {
		return showBorderWidth;
	}

	public void setShowBorderWidth(float showBorderWidth) {
		this.showBorderWidth = showBorderWidth;
	}

	public Color getPitColor() {
		return pitColor;
	}

	public void setPitColor(Color pitColor) {
		this.pitColor = pitColor;
	}

	/**
	 * set image to draw
	 * 
	 * @param image BufferedImage
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public boolean isDrawBorder() {
		return drawBorder;
	}

	public void setDrawBorder(boolean drawBorder) {
		this.drawBorder = drawBorder;
	}

	public boolean isDrawImage() {
		return drawImage;
	}

	public void setDrawImage(boolean drawImage) {
		this.drawImage = drawImage;
	}

	@Deprecated
	public Rect2D convertRect() {
		return new Rect2D.Float(this.getX1(), this.getY1(), this.getX2(), this.getY2());
	}

	/**
	 * Draw border for rect
	 * 
	 * @param g Graphics for draw
	 */
	public void drawBorderAndPits(Graphics g) {
		drawBorder(g);
		drawPits(g);
	}

	/**
	 * Draw image for rect
	 * 
	 * @param gGraphics for draw
	 */
	public void drawImage(Graphics g) {
		if (!this.drawImage)
			return;
		if (image != null) {
			g.drawImage(image, (int) Math.round(getX1()), (int) Math.round(getY1()), (int) Math.round(getWidth()),
					(int) Math.round(getHeight()), null);
		}

	}

	public void drawBorder(Graphics g) {
		if (!this.drawBorder)
			return;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		float[] dash = { 5.0f, 10.0f };
		Stroke stroke = new BasicStroke(showBorderWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.5f, dash,
				0.0f);
		g2d.setStroke(stroke);
		g2d.setColor(borderColor);
		g2d.drawRect((int) Math.round(getX1()), (int) Math.round(getY1()), (int) Math.round(getWidth()),
				(int) Math.round(getHeight()));
	}

	@Deprecated
	public void drawBorder(int x1, int y1, int x2, int y2, Graphics g) {
		if (!this.drawBorder)
			return;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		float[] dash = { 5.0f, 10.0f };
		Stroke stroke = new BasicStroke(showBorderWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.5f, dash,
				0.0f);
		g2d.setStroke(stroke);
		g2d.setColor(borderColor);
		g2d.drawRect(x1, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));
	}

	public void drawPits(Graphics g) {
		if (!this.drawBorder)
			return;
		int x1 = (int) Math.round(getX1());
		int y1 = (int) Math.round(getY1());
		int x2 = (int) Math.round(getX2());
		int y2 = (int) Math.round(getY2());
		int border = getBorder();
		g.setColor(pitColor);
		g.fillRect(x1 - border / 2, y1 - border / 2, border, border);
		g.fillRect((x1 + x2) / 2 - border / 2, y1 - border / 2, border, border);
		g.fillRect(x2 - border / 2, y1 - border / 2, border, border);
		g.fillRect(x2 - border / 2, (y1 + y2) / 2 - border / 2, border, border);
		g.fillRect(x2 - border / 2, y2 - border / 2, border, border);
		g.fillRect((x1 + x2) / 2 - border / 2, y2 - border / 2, border, border);
		g.fillRect(x1 - border / 2, y2 - border / 2, border, border);
		g.fillRect(x1 - border / 2, (y1 + y2) / 2 - border / 2, border, border);
	}

	@Deprecated
	public void drawPits(int x1, int y1, int x2, int y2, Graphics g) {
		if (!this.drawBorder)
			return;
		int border = getBorder();
		g.setColor(pitColor);
		g.fillRect(x1 - border / 2, y1 - border / 2, border, border);
		g.fillRect((x1 + x2) / 2 - border / 2, y1 - border / 2, border, border);
		g.fillRect(x2 - border / 2, y1 - border / 2, border, border);
		g.fillRect(x2 - border / 2, (y1 + y2) / 2 - border / 2, border, border);
		g.fillRect(x2 - border / 2, y2 - border / 2, border, border);
		g.fillRect((x1 + x2) / 2 - border / 2, y2 - border / 2, border, border);
		g.fillRect(x1 - border / 2, y2 - border / 2, border, border);
		g.fillRect(x1 - border / 2, (y1 + y2) / 2 - border / 2, border, border);

	}

}
