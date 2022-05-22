package tk.kqstone.dedd;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.JPanel;

/**
 * Panel that can show border and image, which can be dragged to move and resize
 * 
 * @author kqstone
 *
 */
public class BasicBorderPanel extends JPanel {

	private SimpleDrawableBorderRect simpleDrawableBorderRect; // rect(relative to parent component) for
																// BasicBorderPanel
	private boolean dragable;

	/**
	 * initialize BasicBorderPanel
	 */
	public BasicBorderPanel() {
		super();
		this.setOpaque(false);
		simpleDrawableBorderRect = new SimpleDrawableBorderRect();
		setDragable(true);
	}

	public void setDragable(boolean dragable) {
		if (dragable == this.dragable)
			return;
		this.dragable = dragable;
		if (dragable) {
			DragAdapter adapter = new DragAdapter(this);
			this.addMouseListener(adapter);
			this.addMouseMotionListener(adapter);
		} else {
			MouseListener[] mls = this.getMouseListeners();
			for (MouseListener l : mls) {
				this.removeMouseListener(l);
			}
			MouseMotionListener[] mmls = this.getMouseMotionListeners();
			for (MouseMotionListener ml : mmls) {
				this.removeMouseMotionListener(ml);
			}
		}
	}

	/**
	 * get rect for this BasicBorderPanel
	 * 
	 * @return rect(relative to parent component)
	 */
	public SimpleDrawableBorderRect getSimpleDrawableBorderRect() {
		return this.simpleDrawableBorderRect;
	}

	public Rect2D getRect() {
		return this.simpleDrawableBorderRect.convertRect();
	}

	public void setRect(SimpleDrawableBorderRect rect) {
		this.simpleDrawableBorderRect = rect;
	}

	/**
	 * set bounds relative to parent component
	 */
	@Override
	public void setBounds(int x, int y, int width, int height) {
		int border = ((BorderRect) simpleDrawableBorderRect).getBorder();
		super.setBounds(x - border / 2, y - border / 2, width + border, height + border);
//		simpleDrawableBorderRect.setPoint(new Point2D.Float(x, y), new Point2D.Float(x + width, y + height));
	}

	/**
	 * set bounds relative to parent component with rect data
	 */
	public void setBounds() {
		int x1 = (int) Math.round(simpleDrawableBorderRect.getX1());
		int y1 = (int) Math.round(simpleDrawableBorderRect.getY1());
		int width = (int) Math.round(simpleDrawableBorderRect.getWidth());
		int height = (int) Math.round(simpleDrawableBorderRect.getHeight());
		setBounds(x1, y1, width, height);
	}

	/**
	 * inherited method from JPanel run after repaint();
	 */
	@Override
	protected void paintComponent(Graphics g) {
//		super.paintComponent(g);
		if (simpleDrawableBorderRect != null) {

//			Logger.getGlobal().info("Rect:"+rect.getx1()+","+rect.gety1());
			simpleDrawableBorderRect.drawBorderAndPits(g);
			simpleDrawableBorderRect.drawImage(g);

		}

	}

	/**
	 * set image for BorderPanel
	 * 
	 * @param image BufferedImage
	 */
	public void setImage(BufferedImage image) {
		((DrawableBorderRect) simpleDrawableBorderRect).setImage(image);
	}

	/**
	 * Mouse Drag Adapter
	 * 
	 * @author kqstone
	 *
	 */
	class DragAdapter extends MouseAdapter {
		Container content;
		BorderRect inRect; // rect relative to it self
		Point start; // start point when mouse pressed
		Point end; // end point when mouse released
		int flag = BorderRect.POINT_OUT_RECT; // mouse select flag

		// start point for rect relative to parent component
		Point2D p1;
		Point2D p2;
		Point2D p3;
		Point2D p4;
		
		boolean inDrag = false;

		DragAdapter(Container content) {
			this.content = content;
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			Point p = e.getPoint();
			int tmpFlag = simpleDrawableBorderRect.getInRect().getPointFlag(p);
			if (tmpFlag == flag)
				return;
			flag = tmpFlag;
			System.out.println("MouseMoved:" + flag);
			switch (flag) {
			case BorderRect.POINT_OUT_RECT:
				content.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				break;
			case BorderRect.POINT_NW:
				content.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
				break;
			case BorderRect.POINT_NE:
				content.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
				break;
			case BorderRect.POINT_SW:
				content.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
				break;
			case BorderRect.POINT_SE:
				content.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				break;
			case BorderRect.POINT_ON_NB:
				content.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				break;
			case BorderRect.POINT_ON_SB:
				content.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				break;
			case BorderRect.POINT_ON_WB:
				content.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				break;
			case BorderRect.POINT_ON_EB:
				content.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				break;
			case BorderRect.POINT_IN_RECT:
				content.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				break;
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getButton() != MouseEvent.BUTTON1)
				return;
			inDrag = true;
			start = e.getLocationOnScreen();
			inRect = simpleDrawableBorderRect.getInRect();
			flag = inRect.getPointFlag(e.getPoint());

			p1 = simpleDrawableBorderRect.getPoint(Rect.POINT_NW);
			p2 = simpleDrawableBorderRect.getPoint(Rect.POINT_SE);
			p3 = simpleDrawableBorderRect.getPoint(Rect.POINT_NE);
			p4 = simpleDrawableBorderRect.getPoint(Rect.POINT_SW);

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (inDrag) {
				end = e.getPoint();
				inDrag = false;
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!inDrag)
				return;
			Point tmp = e.getLocationOnScreen();
			int offsetx = tmp.x - start.x;
			int offsety = tmp.y - start.y;
			switch (flag) {
			case BorderRect.POINT_NW:
				simpleDrawableBorderRect
						.setPoint(new Point2D.Float((float) p1.getX() + offsetx, (float) p1.getY() + offsety), p2);
				break;
			case BorderRect.POINT_NE:
				simpleDrawableBorderRect
						.setPoint(new Point2D.Float((float) p3.getX() + offsetx, (float) p3.getY() + offsety), p4);
				break;
			case BorderRect.POINT_SW:
				simpleDrawableBorderRect
						.setPoint(new Point2D.Float((float) p4.getX() + offsetx, (float) p4.getY() + offsety), p3);
				break;
			case BorderRect.POINT_SE:
				simpleDrawableBorderRect
						.setPoint(new Point2D.Float((float) p2.getX() + offsetx, (float) p2.getY() + offsety), p1);
				break;
			case BorderRect.POINT_ON_NB:
				simpleDrawableBorderRect.setPoint(new Point2D.Float((float) p1.getX(), (float) p1.getY() + offsety),
						p2);
				break;
			case BorderRect.POINT_ON_SB:
				simpleDrawableBorderRect.setPoint(p1,
						new Point2D.Float((float) p2.getX(), (float) p2.getY() + offsety));
				break;
			case BorderRect.POINT_ON_WB:
				simpleDrawableBorderRect.setPoint(p2,
						new Point2D.Float((float) p1.getX() + offsetx, (float) p1.getY()));
				break;
			case BorderRect.POINT_ON_EB:
				simpleDrawableBorderRect.setPoint(p1,
						new Point2D.Float((float) p2.getX() + offsetx, (float) p2.getY()));
				break;
			case BorderRect.POINT_IN_RECT:
				simpleDrawableBorderRect.setPoint(
						new Point2D.Float((float) p1.getX() + offsetx, (float) p1.getY() + offsety),
						new Point2D.Float((float) p2.getX() + offsetx, (float) p2.getY() + offsety));
				break;
			case BorderRect.POINT_OUT_RECT:
				break;
			default:
				break;
			}
			((BasicBorderPanel) content).setBounds();

		}
	}

}
