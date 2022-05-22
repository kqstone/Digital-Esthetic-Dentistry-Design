package tk.kqstone.dedd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;

/**
 * SimpleDrawableBorderRect inherited from DrawableBorderRect, but rect
 * point1=(0,0)
 * 
 * @author kqstone
 *
 */
public class SimpleDrawableBorderRect extends DrawableBorderRect {

	/**
	 * Draw BorderAndPits for rect
	 * 
	 * @param g Graphics
	 */
	@Override
	public void drawBorderAndPits(Graphics g) {
		int x1 = getBorder() / 2;
		int y1 = getBorder() / 2;
		int x2 = (int) Math.round(x1 + getWidth());
		int y2 = (int) Math.round(y1 + getHeight());
		this.drawBorder(x1, y1, x2, y2, g);
		this.drawPits(x1, y1, x2, y2, g);
	}

	/**
	 * Draw image for rect
	 * 
	 * @param g Graphics
	 */
	@Override
	public void drawImage(Graphics g) {
		if (this.getImage() != null) {
			g.drawImage(this.getImage(), (int) Math.round(getBorder() / 2), (int) Math.round(getBorder() / 2),
					(int) Math.round(getWidth()), (int) Math.round(getHeight()), null);
		}

	}
	
	public BorderRect getInRect() {
		int border = this.getBorder();
		return new BorderRect(new Point2D.Float(border / 2, border / 2), new Point2D.Float(
				(float) (this.getX2() - this.getX1() + border / 2),
				(float) (this.getY2() - this.getY1() + border / 2)));
	}

}
