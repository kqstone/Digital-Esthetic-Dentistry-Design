package tk.kqstone.dedd;

import java.awt.Point;
import java.io.Serializable;

/**
 * Rectangle
 * 
 * @author kqstone
 *
 */
public class Rect implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * NorthWest point flag
	 */
	public static final int POINT_NW = 1;
	/**
	 * NorthEast point flag
	 */
	public static final int POINT_NE = 2;
	/**
	 * SourthWest point flag
	 */
	public static final int POINT_SW = 3;
	/**
	 * SourthEast point flag
	 */
	public static final int POINT_SE = 4;

	/**
	 * Point in Rectangle flag
	 */
	public static final int POINT_IN_RECT = 5;
	/**
	 * Point out Rectangle flag
	 */
	public static final int POINT_OUT_RECT = 6;

	public static final int POINT_DEFAULT = 0;

	/**
	 * North line flag
	 */
	public static final int LINE_N = 11;
	/**
	 * Sourth line flag
	 */
	/**
	 * East line flag
	 */
	public static final int LINE_S = 12;
	/**
	 * East line flag
	 */
	public static final int LINE_E = 13;
	/**
	 * West line flag
	 */
	public static final int LINE_W = 14;

	private Point point1;
	private Point point2;

	public Rect() {

	}

	/**
	 * initialize Rect with 2 points
	 * 
	 * @param p1 Point
	 * @param p2 Point
	 */
	public Rect(Point p1, Point p2) {
		this(p1.x, p1.y, p2.x, p2.y);

	}

	/**
	 * initialize Rect with 2 coordinates (x1, y1) and (x2, y2)
	 * 
	 * @param x1 int
	 * @param y1 int
	 * @param x2 int
	 * @param y2 int
	 */
	public Rect(int x1, int y1, int x2, int y2) {
		if (x1 > x2) {
			x1 = x1 ^ x2;
			x2 = x2 ^ x1;
			x1 = x1 ^ x2;
		}
		if (y1 > y2) {
			y1 = y1 ^ y2;
			y2 = y2 ^ y1;
			y1 = y1 ^ y2;
		}
		this.point1 = new Point(x1, y1);
		this.point2 = new Point(x2, y2);

	}

	/**
	 * initialize Rect with centerPoint and width, height
	 * 
	 * @param centerPoint Point
	 * @param width       int
	 * @param height      int
	 */
	public Rect(Point centerPoint, int width, int height) {
		int x1 = centerPoint.x - width / 2;
		int y1 = centerPoint.y - height / 2;
		int x2 = centerPoint.x + width / 2;
		int y2 = centerPoint.y + height / 2;
		this.point1 = new Point(x1, y1);
		this.point2 = new Point(x2, y2);
	}

	/**
	 * initialize Square with centerPoint and edge
	 * 
	 * @param centerPoint Point
	 * @param edge        int
	 */
	public Rect(Point centerPoint, int edge) {
		this(centerPoint, edge, edge);
	}

	/**
	 * set Rect points
	 * 
	 * @param p1 Point
	 * @param p2 Point
	 */
	public void setPoints(Point p1, Point p2) {
		int x1 = p1.x;
		int y1 = p1.y;
		int x2 = p2.x;
		int y2 = p2.y;
		if (x1 > x2) {
			x1 = x1 ^ x2;
			x2 = x2 ^ x1;
			x1 = x1 ^ x2;
		}
		if (y1 > y2) {
			y1 = y1 ^ y2;
			y2 = y2 ^ y1;
			y1 = y1 ^ y2;
		}
		this.point1 = new Point(x1, y1);
		this.point2 = new Point(x2, y2);
	}

	public void setPoints(Point[] points) {
		if (points.length != 2) {
			IllegalArgumentException e = new IllegalArgumentException("length of points should be 2");
			throw e;
		} else {
			setPoints(points[0], points[1]);
		}

	}

	public void setRect(Rect rect) {
		this.point1 = rect.point1;
		this.point2 = rect.point2;
	}

	/**
	 * get Rect Points
	 * 
	 * @return { point1, point2 }
	 */
	public Point[] getPoints() {
		Point[] points = { point1, point2 };
		return points;
	}

	/**
	 * get point with pointFlag(POINT_NW,POINT_NE,POINT_SW,POINT_SE)
	 * 
	 * @param pointFlag
	 * @return Point
	 */
	public Point getPoint(int pointFlag) {
		Point p = null;
		switch (pointFlag) {
		case POINT_NW:
			p = point1;
			break;
		case POINT_NE:
			p = new Point(point2.x, point1.y);
			break;
		case POINT_SW:
			p = new Point(point1.x, point2.y);
			break;
		case POINT_SE:
			p = point2;
			break;
		default:
			break;
		}
		return p;
	}

	/**
	 * 
	 * Judge whether the point is in rect
	 * 
	 * @param point
	 * @return true if point in Rect, false when out
	 */
	public boolean contain(Point point) {
		if (point.x >= point1.x && point.x <= point2.x && point.y >= point1.y && point.y <= point2.y) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * get pointFlag with point
	 * 
	 * @param point to be judged
	 * @return
	 */
	public int getPointFlag(Point point) {
		if (point.equals(point1)) {
			return POINT_NW;
		} else if (point.equals(point2)) {
			return POINT_SE;
		} else if (point.equals(new Point(point2.x, point1.y))) {
			return POINT_NE;
		} else if (point.equals(new Point(point1.x, point2.y))) {
			return POINT_SW;
		} else {
			if (this.contain(point)) {
				return POINT_IN_RECT;
			} else {
				return POINT_OUT_RECT;
			}

		}

	}

	/**
	 * get rec width
	 * 
	 * @return width
	 */
	public int getWidth() {
		return point2.x - point1.x;
	}

	/**
	 * get rec height
	 * 
	 * @return height
	 */
	public int getHeight() {
		return point2.y - point1.y;
	}

	/**
	 * get the rect center point
	 * 
	 * @return Point center point of rect
	 */
	public Point getCenterPoint() {
		int x = (point1.x + point2.x) / 2;
		int y = (point1.y + point2.y) / 2;
		return new Point(x, y);
	}

	public int getx1() {
		return point1.x;
	}

	public int getx2() {
		return point2.x;
	}

	public int gety1() {
		return point1.y;
	}

	public int gety2() {
		return point2.y;
	}

	/**
	 * offset one edge
	 * 
	 * @param offset int
	 * @param flag   int point flag
	 * @return itself with offset rect
	 */
	public Rect offsetside(int offset, int flag) {
		int x1 = point1.x;
		int y1 = point1.y;
		int x2 = point2.x;
		int y2 = point2.y;
		switch (flag) {
		case LINE_N:
			y1 -= offset;
			break;
		case LINE_S:
			y2 -= offset;
			break;
		case LINE_E:
			x1 -= offset;
			break;
		case LINE_W:
			x2 -= offset;
			break;
		}
		point1 = new Point(x1, y1);
		point2 = new Point(x2, y2);
		return this;
	}

	/**
	 * move the rect
	 * 
	 * @param offsetx
	 * @param offsety
	 * @return
	 */
	public Rect offset(int offsetx, int offsety) {
		int x1 = point1.x - offsetx;
		int y1 = point1.y - offsety;
		int x2 = point2.x - offsetx;
		int y2 = point1.y - offsety;
		this.point1 = new Point(x1 - offsetx, y1 - offsety);
		this.point2 = new Point(x2 - offsetx, y2 - offsety);
		return this;
	}

	/**
	 * resize the rect
	 * 
	 * @param scalex double
	 * @param scaley double
	 */
	public Rect resize(double scalex, double scaley) {
		int x1 = (int) (point1.x * scalex);
		int y1 = (int) (point1.y * scaley);
		int x2 = (int) (point2.x * scalex);
		int y2 = (int) (point2.y * scaley);
		this.point1 = new Point(x1, y1);
		this.point2 = new Point(x2, y2);
		return this;

	}

	public void zoom(float preProportion, int preOffsetX, int preOffsetY, float proportion, int offsetX, int offsetY) {
		int x1 = Math.round((getx1() - preOffsetX) / preProportion * proportion) + offsetX;
		int x2 = Math.round((getx2() - preOffsetX) / preProportion * proportion) + offsetX;
		int y1 = Math.round((gety1() - preOffsetY) / preProportion * proportion) + offsetY;
		int y2 = Math.round((gety2() - preOffsetY) / preProportion * proportion) + offsetY;
		this.point1 = new Point(x1, y1);
		this.point2 = new Point(x2, y2);
	}

	/**
	 * create a new rect with point1 and point2
	 * 
	 * @return new Rect
	 */
	public Rect createRect() {
		Rect rect = new Rect(point1, point2);
		return rect;
	}

	@Override
	public boolean equals(Object rect) {
		if (rect == null)
			return false;
		if (rect instanceof Rect) {
			Rect tmp = (Rect) rect;
			return (point1.equals(tmp.point1)) && (point2.equals(tmp.point2));
		}
		return super.equals(rect);
	}

}
