package tk.kqstone.dedd;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

/**
 * BorderRectangle
 * 
 * @author kqstone
 * 
 */
public class BorderRect extends Rect2D.Float {

	/**
	 * default border width border width can be set with setBorder(int border)
	 */
	public static final int DEFAULT_BORDER_WIDTH = 10;

	/**
	 * border width
	 */
	private int border = DEFAULT_BORDER_WIDTH;

	public BorderRect() {
	}

	public BorderRect(Point2D p1, Point2D p2) {
		super(p1, p2);
	}

	public BorderRect(Point2D p1, Point2D p2, int border) {
		super(p1, p2);
		this.border = border;
	}

	/**
	 * set border width
	 * 
	 * @param border int border width
	 */
	public void setBorder(int border) {
		this.border = border;
	}

	/**
	 * get border width
	 * 
	 * @return border width
	 */
	public int getBorder() {
		return border;
	}

	/**
	 * judge the given point position flag to this rectangle
	 * 
	 * @return int PointFlag. see Rect
	 */
	@Override
	public int getPointFlag(Point2D point) {
		Point2D nwP = super.getPoint(POINT_NW);
		Point2D neP = super.getPoint(POINT_NE);
		Point2D swP = super.getPoint(POINT_SW);
		Point2D seP = super.getPoint(POINT_SE);
		Rect2D nwR = new Rect2D.Float(nwP, border);
		Rect2D neR = new Rect2D.Float(neP, border);
		Rect2D swR = new Rect2D.Float(swP, border);
		Rect2D seR = new Rect2D.Float(seP, border);
		Rect2D nbR = new Rect2D.Float(new Point2D.Float((float) (getX1() + getX2()) / 2, (float) getY1()), border,
				border);
		Rect2D sbR = new Rect2D.Float(new Point2D.Float((float) (getX1() + getX2()) / 2, (float) getY2()), border,
				border);
		Rect2D ebR = new Rect2D.Float(new Point2D.Float((float) getX2(), (float) (getY1() + getY2()) / 2), border,
				border);
		Rect2D wbR = new Rect2D.Float(new Point2D.Float((float) getX1(), (float) (getY1() + getY2()) / 2), border,
				border);
		Rect2D inR = new Rect2D.Float(getX1() + border / 2, getY1() + border / 2, getX2() - border / 2,
				getY2() - border / 2);
		if (nwR.contain(point)) {
			return Rect.POINT_NW;
		}
		if (neR.contain(point)) {
			return Rect.POINT_NE;
		}
		if (swR.contain(point)) {
			return Rect.POINT_SW;

		}
		if (seR.contain(point)) {
			return Rect.POINT_SE;
		}
		if (nbR.contain(point)) {
			return BorderRect.POINT_ON_NB;
		}
		if (sbR.contain(point)) {
			return BorderRect.POINT_ON_SB;

		}
		if (ebR.contain(point)) {
			return BorderRect.POINT_ON_EB;
		}
		if (wbR.contain(point)) {
			return BorderRect.POINT_ON_WB;
		}
		if (inR.contain(point)) {
			return Rect.POINT_IN_RECT;
		}
		return Rect.POINT_OUT_RECT;

	}

}
