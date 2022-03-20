package tk.kqstone.dedd;

import java.awt.geom.Point2D;

public class PolarPoint {
	public double thelta;
	public double r;

	public PolarPoint() {
	}

	public PolarPoint(double thelta, double r) {
		this.thelta = thelta;
		this.r = r;
	}

	public Point2D.Double toCartierPoint() {
		double x = r * Math.cos(thelta);
		double y = r * Math.sin(thelta);
		return new Point2D.Double(x, y);
	}

	public static Point2D.Double toCartierPoint(PolarPoint polarPoint) {
		double x = polarPoint.r * Math.cos(polarPoint.thelta);
		double y = polarPoint.r * Math.sin(polarPoint.thelta);
		return new Point2D.Double(x, y);
	}

	public static PolarPoint toPolarPoint(Point2D point2D) {
		double thelta = Math.atan2(point2D.getY(), point2D.getX());
//		if (point2D.getX() < 0)
//			thelta = -thelta;
		double r = point2D.distance(0, 0);
		return new PolarPoint(thelta, r);
	}

}
