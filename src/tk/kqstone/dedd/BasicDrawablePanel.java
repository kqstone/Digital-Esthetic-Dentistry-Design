package tk.kqstone.dedd;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Panel that can draw, move and resize Rectangles
 * 
 * @author kqstone
 *
 */
public abstract class BasicDrawablePanel extends ZoomableJPanel {
	private List<DrawableBorderRect> listDrawableBorderRect;
	private int panelWidth;
	private int panelHeight;

	public BasicDrawablePanel() {
		super();
		this.setOpaque(false);
		listDrawableBorderRect = new ArrayList<>();
		DragRectsAdapter adapter = new DragRectsAdapter(this, listDrawableBorderRect);
		this.addMouseListener(adapter);
		this.addMouseMotionListener(adapter);
	}

	/**
	 * get all DrawableBorderRect
	 * 
	 * @return List<DrawableBorderRect>
	 */
	public List<DrawableBorderRect> getBorderPanelList() {
		return listDrawableBorderRect;
	}

	/**
	 * 
	 * paint rects inherited from JPanel
	 * 
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (DrawableBorderRect bp : listDrawableBorderRect) {
			bp.drawBorderAndPits(g);
		}
	}

	protected abstract void rectAdded();

	protected abstract void rectRemoved(int index);

	protected abstract void rectMoved(int index, int x1, int y1, int x2, int y2);

//	/**
//	 * set bounds
//	 */
//	@Override
//	public void setBounds(int x, int y, int width, int height) {
//		if ((width / (double) panelWidth - 1 < 0.1 && width / (double) panelWidth - 1 > -0.1)
//				|| (height / (double) panelHeight - 1 < 0.1 && height / (double) panelHeight - 1 > -0.1)) {
//			return;
//		}
//		if (listDrawableBorderRect.size() > 0) {
//			for (DrawableBorderRect bdp : listDrawableBorderRect) {
//				bdp.resize(width / (double) panelWidth, height / (double) panelHeight);
//			}
//		}
//		super.setBounds(x, y, width, height);
//		panelWidth = width;
//		panelHeight = height;
//
//	}
//
//	public void resize(double scalex, double scaley) {
//		if (listDrawableBorderRect.size() > 0) {
//			for (DrawableBorderRect bdp : listDrawableBorderRect) {
//				bdp.resize(scalex, scaley);
//			}
//			this.repaint();
//		}
//
//	}

}
