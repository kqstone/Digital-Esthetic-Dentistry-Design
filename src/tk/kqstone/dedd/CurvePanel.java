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
	private List<Point> points; //实际拖拽点
	private List<Point> showPoints; //显示拖拽点，随缩放和位移变化
	private List<Point[]> controlPoints;
	private List<Point[]> showControlPoints;
	private GeneralPath path; //实际路径
	private GeneralPath showPath; //显示路径，随缩放和位移变化
	private GeneralPath outputPath;

	public CurvePanel() {
		this.setOpaque(false);
		points = new ArrayList<>();
		showPoints = new ArrayList<>();
		showPath = new GeneralPath();
		MouseAdapter adapter = new MouseActionAdapter();
		this.addMouseListener(adapter);
		this.addMouseMotionListener(adapter);
		proportion = 1.0f;
		offsetX = 0;
		offsetY = 0;
	}

	public GeneralPath getPath() {
		genPath();
		return path;
	}

	public GeneralPath getLowerClosedPath() {
		if (points.size() < 2)
			return null;
		genCtlPoints();
		genPath();
		outputPath = (GeneralPath) path.clone();
//		int x1 = points.get(0).x;
//		int x2 = points.get(points.size() - 1).x;
//		int y = this.getHeight();
//		outputPath.lineTo(x2, y);
//		outputPath.lineTo(x1, y);
//		outputPath.closePath();
		return outputPath;
	}

	@Override
	public void zoom(float proportion, int offsetX, int offsetY) {
		
		showPoints = zoomPoints(points, proportion, offsetX, offsetY);
		genShowCtlPoints();
		genShowPath();
		super.zoom(proportion, offsetX, offsetY);
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
		for (Point p : showPoints) {
//			int x = p.x * proportion +offsetX;
//			int y = p.y * proportion + offsetY;
			g.fillOval(p.x - PT_RADIUS, p.y - PT_RADIUS, PT_RADIUS * 2, PT_RADIUS * 2);
		}

//		g.setColor(Color.RED);
//		for (Point[] ps : controlPoints) {
//			g.fillOval(ps[0].x - PT_RADIUS, ps[0].y - PT_RADIUS, PT_RADIUS * 2, PT_RADIUS * 2);
//			g.fillOval(ps[1].x - PT_RADIUS, ps[1].y - PT_RADIUS, PT_RADIUS * 2, PT_RADIUS * 2);
//		}

		g2d.draw(showPath);
//		g2d.scale(proportion, proportion);
//		g2d.translate(offsetX, offsetY);
	}

	@Deprecated
	private static List<Point[]> genCtlPoints(List<Point> points) {
		List<Point[]> ctlPoints = new ArrayList<>();
		if (points.size() < 3)
			return ctlPoints;
		Point c0 = null;
		Point c1;
		Point c2;
		List<Point> tmpPoints = new ArrayList<>();
		tmpPoints.addAll(points);		
//		tmpPoints.add(points.get(0));
//		tmpPoints.add(points.get(1));
		
		for (int i = 0; i < tmpPoints.size() ; i++) {
			Point p0, p1, p2;
			if (i == 0) {
				p0 = tmpPoints.get(tmpPoints.size()-1);
				p2 = tmpPoints.get(i + 1);
			} else if (i == tmpPoints.size()-1) {
				p2 = tmpPoints.get(0);
				p0 = tmpPoints.get(i-1);
			} else {
				p0 = tmpPoints.get(i-1);
				p2 = tmpPoints.get(i + 1);
			}			
			p1 = tmpPoints.get(i);
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
//			if (i == 0) {
//				c0 = p0;
//			}
			Point[] tmp = { c1, c2 };
			ctlPoints.add(tmp);
//			c0 = c2;
		}
//		Point[] tmp = { c0, tmpPoints.get(points.size() - 1) };
//		ctlPoints.add(tmp);
		return ctlPoints;
	}

	private void genCtlPoints() {
		this.controlPoints = genCtlPoints(points);
	}
	
	private void genShowCtlPoints() {
		this.showControlPoints = genCtlPoints(showPoints);
	}
	
	private static GeneralPath genPath(List<Point> points, List<Point[]> controlPoints) {
		GeneralPath path = new GeneralPath();
		int size = points.size();
		if (size >= 2) {
			path.moveTo(points.get(0).x, points.get(0).y);
			if (size == 2) {
				path.lineTo(points.get(1).x, points.get(1).y);
			} else {
				for (int i = 1; i <= points.size() ; i++) {
					Point c0,c1;
					if(i==points.size()) {
						c0 = controlPoints.get(controlPoints.size()-1)[1];
						c1 =  controlPoints.get(0)[0];
					}else {
						c0 = controlPoints.get(i-1)[1];
						c1 =  controlPoints.get(i)[0];
					}
					Point p1;
					if (i == points.size()) {
						p1 = points.get(0);
					} else {
						p1 = points.get(i);
					}
					path.curveTo(c0.x, c0.y, c1.x, c1.y, p1.x, p1.y);
				}
			}
		}
		return path;
	}


	private void genPath() {
		path = genPath(points, controlPoints);

	}
	
	private void genShowPath() {		
		showPath = genPath(showPoints, showControlPoints);
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
				if (showPoints.size() == 0) {
					paintMode = true;
					showPoints.add(point);
					points.add(restoreZoomPoint(point, proportion, offsetX, offsetY));
				} else {
					if (paintMode) {
						for (int i = 0; i < points.size() - 1; i++) {
							if (showPoints.get(i).distance(point) < PT_RADIUS) {
								return;
							}
						}
						
					} else {
						return;
					}
					genShowCtlPoints();
				}
				genShowPath();
				repaint();
				pointIndex = showPoints.size();
				break;
			case MouseEvent.BUTTON3:
				if (paintMode) {
					paintMode = false;

				} else {
					
					for(int i=0; i<showPoints.size(); i++) {
						if (showPoints.get(i).distance(point) < PT_RADIUS) {
							showPoints.remove(i);
							points.remove(i);
							genShowCtlPoints();
							genShowPath();
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
				if (showPoints.get(i).distance(point) < PT_RADIUS) {
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
			showPoints.get(pointIndex).setLocation(point);
			points.get(pointIndex).setLocation(restoreZoomPoint(point, proportion, offsetX, offsetY));
			genShowCtlPoints();
			genShowPath();
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (!paintMode)
				return;
			Point point = e.getPoint();
			
			if (pointIndex >= points.size()) {
					showPoints.add(point);
					points.add(restoreZoomPoint(point, proportion, offsetX, offsetY));
				
				
			}
			showPoints.get(pointIndex).setLocation(point);
			points.get(pointIndex).setLocation(restoreZoomPoint(point, proportion, offsetX, offsetY));
			genShowCtlPoints();
			genShowPath();
			repaint();
		}

	}

	public void setPoints(List<Point> points) {
		this.points = points;
		this.showPoints = zoomPoints(points, proportion, offsetX, offsetY);
		genShowCtlPoints();
		genCtlPoints();
		genShowPath();
		repaint();
	}

	public List<Point> getPoints() {
		if (this.points == null || points.size() == 0)
			return null;
		return this.points;

	}
	
	private static Point zoomPoint(Point point ,float proportion, int offsetX, int offsetY) {
		int x = Math.round(point.x * proportion + offsetX);
		int y = Math.round(point.y * proportion + offsetY);
		return new Point(x,y);		
	}
	
	private static Point restoreZoomPoint(Point point ,float proportion, int offsetX, int offsetY) {
		int x = Math.round((point.x - offsetX) / proportion);
		int y = Math.round((point.y - offsetY) / proportion);
		return new Point(x,y);		
	}
	
	private static List<Point> zoomPoints(List<Point> points ,float proportion, int offsetX, int offsetY) {
		List<Point> outputPoint = new ArrayList<>();
		for (Point p : points) {
			Point tmpPoint = zoomPoint(p, proportion, offsetX, offsetY);
			outputPoint.add(tmpPoint);
		}
		return outputPoint;		
	}

}
