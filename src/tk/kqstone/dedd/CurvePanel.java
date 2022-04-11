package tk.kqstone.dedd;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class CurvePanel extends ZoomableJPanel {
	private static final int PT_RADIUS = 5;
	private static final Color DEFAULT_COLOR = Color.GREEN;
	private List<Point> points;
	private List<Point[]> controlPoints;
	private GeneralPath path;
	private GeneralPath outputPath;

	private float proportion;
	private int offsetX;
	private int offsetY;

	public CurvePanel() {
		this.setOpaque(false);
		points = new ArrayList<>();
		controlPoints = new ArrayList<>();
		path = new GeneralPath();
		MouseAdapter adapter = new MouseActionAdapter();
		this.addMouseListener(adapter);
		this.addMouseMotionListener(adapter);
		proportion = 1.0f;
		offsetX = 0;
		offsetY = 0;
	}

	public GeneralPath getPath() {
		zoom(1.0f, 0, 0);
		return path;
	}

	public GeneralPath getLowerClosedPath() {
		if (points.size() < 2)
			return null;
		zoom(1.0f, 0, 0);
		outputPath = (GeneralPath) path.clone();
		int x1 = points.get(0).x;
		int x2 = points.get(points.size() - 1).x;
		int y = this.getHeight();
		outputPath.lineTo(x2, y);
		outputPath.lineTo(x1, y);
		outputPath.closePath();
		return outputPath;
	}

	@Override
	public synchronized void zoom(float proportion, int offsetX, int offsetY) {

		for (Point p : points) {
			int x = Math.round((p.x - this.offsetX) / this.proportion * proportion + offsetX);
			int y = Math.round((p.y - this.offsetY) / this.proportion * proportion + offsetY);
			p.setLocation(x, y);
		}
		genCtlPoints();
		genPath();
		this.proportion = proportion;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
//		final AffineTransform identify = new AffineTransform();
//		g2d.setTransform(identify);
//		g2d.scale(proportion, proportion);
//		g2d.translate(-offsetX, -offsetY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(DEFAULT_COLOR);
		for (Point p : points) {
//			int x = p.x * proportion +offsetX;
//			int y = p.y * proportion + offsetY;
			g.fillOval(p.x - PT_RADIUS, p.y - PT_RADIUS, PT_RADIUS * 2, PT_RADIUS * 2);
		}

//		g.setColor(Color.RED);
//		for (Point[] ps : controlPoints) {
//			g.fillOval(ps[0].x - PT_RADIUS, ps[0].y - PT_RADIUS, PT_RADIUS * 2, PT_RADIUS * 2);
//			g.fillOval(ps[1].x - PT_RADIUS, ps[1].y - PT_RADIUS, PT_RADIUS * 2, PT_RADIUS * 2);
//		}

		g2d.draw(path);
//		g2d.scale(proportion, proportion);
//		g2d.translate(offsetX, offsetY);
	}

	@Deprecated
	private List<Point[]> genCtlPoints(List<Point> points) {
		List<Point[]> ctlPoints = new ArrayList<>();
		if (points.size() < 3)
			return ctlPoints;
		Point c0 = null;
		Point c1;
		Point c2;
		for (int i = 0; i < points.size() - 2; i++) {
			Point p0 = points.get(i);
			Point p1 = points.get(i + 1);
			Point p2 = points.get(i + 2);
			double d1 = p1.distance(p0);
			double d2 = p1.distance(p2);
			double t = d1 / (d1 + d2);
			int mx = (p0.x + p1.x) / 2;
			int my = (p0.y + p1.y) / 2;
			int nx = (p2.x + p1.x) / 2;
			int ny = (p2.y + p1.y) / 2;
			int sx = (int) ((1 - t) * mx + t * nx);
			int sy = (int) ((1 - t) * my + t * ny);

			int offsetX = p1.x - sx;
			int offsetY = p1.y - sy;
			c1 = new Point(mx + offsetX, my + offsetY);
			c2 = new Point(nx + offsetX, ny + offsetY);
			if (i == 0) {
				c0 = p0;
			}
			Point[] tmp = { c0, c1 };
			ctlPoints.add(tmp);
			c0 = c2;
		}
		Point[] tmp = { c0, points.get(points.size() - 1) };
		ctlPoints.add(tmp);
		return ctlPoints;
	}

	private void genCtlPoints() {
		this.controlPoints = genCtlPoints(points);
	}

	private void genPath() {
		path.reset();
		int size = points.size();
		if (size < 2)
			return;
		path.moveTo(points.get(0).x, points.get(0).y);
		if (size == 2) {
			path.lineTo(points.get(1).x, points.get(1).y);
		} else {
			for (int i = 0; i < points.size() - 1; i++) {
				Point c0 = controlPoints.get(i)[0];
				Point c1 = controlPoints.get(i)[1];
				Point p1 = points.get(i + 1);
				path.curveTo(c0.x, c0.y, c1.x, c1.y, p1.x, p1.y);
			}
		}
	}

	class MouseActionAdapter extends MouseAdapter {

		int pointIndex = -1;
		boolean paintMode = false;

		@Override
		public void mouseClicked(MouseEvent e) {
			int button = e.getButton();
			Point point = e.getPoint();
			switch (button) {
			case MouseEvent.BUTTON1:
				if (points.size() == 0) {
					paintMode = true;
					points.add(point);
				} else {
					for (int i = 0; i < points.size() - 1; i++) {
						if (points.get(i).distance(point) < PT_RADIUS) {
							return;
						}
					}
					if (points.get(points.size() - 1).distance(point) < PT_RADIUS) {
						paintMode = true;
					}
					genCtlPoints();
				}
				genPath();
				repaint();
				pointIndex = points.size();
				break;
			case MouseEvent.BUTTON3:
				if (paintMode) {
					paintMode = false;

				} else {

					for (Point p : points) {
						if (p.distance(point) < PT_RADIUS) {
							points.remove(p);
							genCtlPoints();
							genPath();
							repaint();
							break;
						}
					}
				}
				break;
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (paintMode)
				return;
			int button = e.getButton();
			if (button != MouseEvent.BUTTON1)
				return;
			Point point = e.getPoint();
			for (int i = 0; i < points.size(); i++) {
				if (points.get(i).distance(point) < PT_RADIUS) {
					pointIndex = i;
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (paintMode)
				return;
			if (pointIndex != -1)
				pointIndex = -1;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (paintMode)
				return;
			if (pointIndex == -1)
				return;
			Point point = e.getPoint();
			points.get(pointIndex).setLocation(point);
			genCtlPoints();
			genPath();
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (!paintMode)
				return;
			Point point = e.getPoint();
			if (pointIndex >= points.size()) {
				points.add(point);
			}
			points.get(pointIndex).setLocation(point);
			genCtlPoints();
			genPath();
			repaint();
		}

	}

	public void setPoints(List<Point> points) {
		this.points = points;
		genCtlPoints();
		genPath();
		repaint();
	}

	public List<Point> getPoints() {
		if (this.points == null || points.size() == 0)
			return null;
		zoom(1.0f, 0, 0);
		return this.points;

	}

}
