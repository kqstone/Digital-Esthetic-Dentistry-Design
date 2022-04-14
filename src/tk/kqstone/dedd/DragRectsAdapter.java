package tk.kqstone.dedd;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.List;

/**
 * MouseAdapter for Dragable Rects
 * 
 * @author kqstone
 *
 */
public class DragRectsAdapter extends MouseAdapter {
	public static int FUNCTION_NULL = 0;
	public static int FUNCTION_DRAG = 1;
	public static int FUNCTION_REMOVE = 2;
	BasicDrawablePanel content;
	private List<DrawableBorderRect> listRects;
	Point start;
	Point end;
	int index = 0;
	int flag;
	int function = FUNCTION_NULL;

	// initial position for rect
	Point2D p1;
	Point2D p2;
	Point2D p3;
	Point2D p4;

	DragRectsAdapter(BasicDrawablePanel content, List<DrawableBorderRect> listPanel) {
		this.content = content;
		this.listRects = listPanel;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1)
			return;
		function = FUNCTION_DRAG;
		flag = BorderRect.POINT_OUT_RECT;
		start = e.getPoint();
		int size = listRects.size();
		for (int i = 0; i < size; i++) {
			flag = listRects.get(i).getPointFlag(start);
			if (flag != BorderRect.POINT_OUT_RECT) {
				// 点中之前矩形，将index指向选中的矩形
				index = i;
				break;
			}
		}
		// 未点中任何之前的矩形，新建矩形
		if (flag == BorderRect.POINT_OUT_RECT) {
			DrawableBorderRect bpn = new DrawableBorderRect();
			listRects.add(bpn);
			content.rectAdded();
			index = size;
			return;
		} else {

			p1 = listRects.get(index).getPoint(BorderRect.POINT_NW);
			p2 = listRects.get(index).getPoint(BorderRect.POINT_SE);
			p3 = listRects.get(index).getPoint(BorderRect.POINT_NE);
			p4 = listRects.get(index).getPoint(BorderRect.POINT_SW);

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (function != FUNCTION_DRAG)
			return;
		end = e.getPoint();
		// if end point is coincide with start point, and out of rect, remove it
		if (start.equals(end)) {
			if (flag == BorderRect.POINT_OUT_RECT) {
				listRects.remove(index);
				content.rectRemoved(index);
			}
		}
		function = FUNCTION_NULL;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (function != FUNCTION_DRAG)
			return;
		Point tmp = e.getPoint();
		DrawableBorderRect bdp = listRects.get(index);
		float x1 = 0, y1 = 0, x2 = 0, y2 = 0;
		switch (flag) {
		case BorderRect.POINT_OUT_RECT:
			x1 = start.x;
			y1 = start.y;
			x2 = tmp.x;
			y2 = tmp.y;
			break;
		case BorderRect.POINT_NW:
			x1 = tmp.x;
			y1 = tmp.y;
			x2 = (float) p2.getX();
			y2 = (float) p2.getY();
			break;
		case BorderRect.POINT_NE:
			x1 = tmp.x;
			y1 = tmp.y;
			x2 = (float) p4.getX();
			y2 = (float) p4.getY();
			break;
		case BorderRect.POINT_SW:
			x1 = tmp.x;
			y1 = tmp.y;
			x2 = (float) p3.getX();
			y2 = (float) p3.getY();
			break;
		case BorderRect.POINT_SE:
			x1 = tmp.x;
			y1 = tmp.y;
			x2 = (float) p1.getX();
			y2 = (float) p1.getY();
			break;
		case BorderRect.POINT_ON_NB:
			x1 = (float) bdp.getX1();
			y1 = tmp.y;
			x2 = (float) p2.getX();
			y2 = (float) p2.getY();
			break;
		case BorderRect.POINT_ON_SB:
			x1 = (float) p1.getX();
			y1 = (float) p1.getY();
			x2 = (float) bdp.getX2();
			y2 = tmp.y;
			break;
		case BorderRect.POINT_ON_WB:
			x1 = (float) p2.getX();
			y1 = (float) p2.getY();
			x2 = tmp.x;
			y2 = (float) bdp.getY1();
			break;
		case BorderRect.POINT_ON_EB:
			x1 = (float) p1.getX();
			y1 = (float) p1.getY();
			x2 = tmp.x;
			y2 = (float) bdp.getY2();
			break;
		case BorderRect.POINT_IN_RECT:
			int offsetx = tmp.x - start.x;
			int offsety = tmp.y - start.y;
			x1 = (float) (p1.getX() + offsetx);
			y1 = (float) (p1.getY() + offsety);
			x2 = (float) (p2.getX() + offsetx);
			y2 = (float) (p2.getY() + offsety);
			break;
		}
		bdp.setPoint(new Point2D.Float(x1, y1), new Point2D.Float(x2, y2));
		content.rectMoved(index, (int) x1, (int) y1, (int) x2, (int) y2);
		content.repaint();

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON3)
			return;
		Point tmp = e.getPoint();
		int index = -1;
		int size = listRects.size();
		for (int i = 0; i < size; i++) {
			int flag = listRects.get(i).getPointFlag(tmp);
			if (flag == BorderRect.POINT_IN_RECT) {
				// 点中之前矩形，将index指向选中的矩形
				index = i;
				break;
			}
		}
		if (index != -1) {
			listRects.remove(index);
			content.rectRemoved(index);
			content.repaint();
		}
	}

}