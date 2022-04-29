package tk.kqstone.dedd;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.io.Serializable;

public abstract class Rect2D implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7423037584446128627L;
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
	/**
	 * point on North Border
	 */
	public static final int POINT_ON_NB = 7;
	/**
	 * point on Sourth Border
	 */
	public static final int POINT_ON_SB = 8;
	/**
	 * point on West Border
	 */
	public static final int POINT_ON_WB = 9;
	/**
	 * point on East Border
	 */
	public static final int POINT_ON_EB = 10;

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

	public static class Float extends Rect2D {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4125724573048564013L;
		Point2D.Float point1;
		Point2D.Float point4;

		public Float() {
		}

		public Float(Point2D point1, Point2D point2) {
			this();
			this.setPoint(point1, point2);
		}

		public Float(double x1, double y1, double x2, double y2) {
			this();
			this.setPoint(x1, y1, x2, y2);
		}

		public Float(Point2D centerPoint, double width, double height) {
			point1 = new Point2D.Float((float) (centerPoint.getX() - width / 2),
					(float) (centerPoint.getY() - height / 2));
			point4 = new Point2D.Float((float) (centerPoint.getX() + width / 2),
					(float) (centerPoint.getY() + height / 2));
		}

		public Float(Point2D centerPoint, double width) {
			this(centerPoint, width, width);
		}

		@Override
		public Point2D getPoint1() {
			return point1;
		}

		@Override
		public Point2D getPoint2() {
			return new Point2D.Float(point4.x, point1.y);
		}

		@Override
		public Point2D getPoint3() {
			return new Point2D.Float(point1.x, point4.y);
		}

		@Override
		public Point2D getPoint4() {
			return point4;
		}

		@Override
		public void setPoint(Point2D point1, Point2D point2) {
			setPoint(point1.getX(), point1.getY(), point2.getX(), point2.getY());
		}

		@Override
		public void setPoint(double x1, double y1, double x2, double y2) {
			if (x1 > x2) {
				double x = x1;
				x = x1;
				x1 = x2;
				x2 = x;
			}

			if (y1 > y2) {
				double y = y1;
				y = y1;
				y1 = y2;
				y2 = y;
			}
			point1 = new Point2D.Float((float) x1, (float) y1);
			point4 = new Point2D.Float((float) x2, (float) y2);
		}

		@Override
		public void zoom(float preProportion, int preOffsetX, int preOffsetY, float proportion, int offsetX,
				int offsetY) {
			float x1 = (float) ((getX1() - preOffsetX) / preProportion * proportion + offsetX);
			float x2 = (float) ((getX2() - preOffsetX) / preProportion * proportion + offsetX);
			float y1 = (float) ((getY1() - preOffsetY) / preProportion * proportion + offsetY);
			float y2 = (float) ((getY2() - preOffsetY) / preProportion * proportion + offsetY);
			this.point1 = new Point2D.Float(x1, y1);
			this.point4 = new Point2D.Float(x2, y2);
		}

		@Override
		public void setRect(Rect2D rect) {
			point1 = (Point2D.Float) rect.getPoint1();
			point4 = (Point2D.Float) rect.getPoint4();

		}

	}

	public Rect2D() {

	}

	public abstract Point2D getPoint1();

	public abstract Point2D getPoint2();

	public abstract Point2D getPoint3();

	public abstract Point2D getPoint4();

	public double getX1() {
		return getPoint1().getX();
	}

	public double getX2() {
		return getPoint4().getX();
	}

	public double getY1() {
		return getPoint1().getY();
	}

	public double getY2() {
		return getPoint4().getY();
	}

	public abstract void setPoint(Point2D point1, Point2D point2);

	public abstract void setPoint(double x1, double y1, double x2, double y2);

	public abstract void setRect(Rect2D rect);

	public double getWidth() {
		return getX2() - getX1();
	}

	public double getHeight() {
		return getY2() - getY1();
	}

	public Point2D getPoint(int pointFlag) {
		switch (pointFlag) {
		case Rect2D.POINT_NW:
			return getPoint1();
		case Rect2D.POINT_NE:
			return getPoint2();
		case Rect2D.POINT_SW:
			return getPoint3();
		case Rect2D.POINT_SE:
			return getPoint4();
		default:
			return null;
		}

	}

	public int getPointFlag(Point2D point) {
		if (point.equals(getPoint1()))
			return Rect2D.POINT_NW;
		if (point.equals(getPoint2()))
			return Rect2D.POINT_NE;
		if (point.equals(getPoint3()))
			return Rect2D.POINT_SW;
		if (point.equals(getPoint4()))
			return Rect2D.POINT_SE;
		if (point.getY() > this.getY1() && point.getY() < this.getY2()) {
			if (point.getX() == this.getX1()) {
				return Rect2D.POINT_ON_WB;
			} else if (point.getX() == this.getX2()) {
				return Rect2D.POINT_ON_EB;
			}
		}
		if (point.getX() > this.getX1() && point.getX() < this.getX2()) {
			if (point.getY() == this.getY1()) {
				return Rect2D.POINT_ON_NB;
			} else if (point.getY() == this.getY2()) {
				return Rect2D.POINT_ON_SB;
			}
		}
		if (contain(point))
			return Rect.POINT_IN_RECT;
		return Rect.POINT_OUT_RECT;
	}

	public boolean contain(Point2D point) {
		double x = point.getX();
		double y = point.getY();
		if (x > getX1() && x < getX2() && y > getY1() && y < getY2())
			return true;
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Rect2D) {
			Rect2D r2d = (Rect2D) obj;
			if (Math.abs(r2d.getX1() - this.getX1()) < 0.1d && Math.abs(r2d.getY1() - this.getY1()) < 0.1d
					&& Math.abs(r2d.getX2() - this.getX2()) < 0.1d && Math.abs(r2d.getY2() - this.getY2()) < 0.1d)
				return true;
		}
		return super.equals(obj);
	}

	public abstract void zoom(float preProportion, int preOffsetX, int preOffsetY, float proportion, int offsetX,
			int offsetY);
}
