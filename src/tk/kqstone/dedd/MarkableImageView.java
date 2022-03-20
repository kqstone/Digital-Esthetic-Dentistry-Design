package tk.kqstone.dedd;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class MarkableImageView extends ImageView {
	private static final Color DEFAULT_COLOR = Color.DARK_GRAY;

	private MarkPanel markPanel;
	private MarkAdapter adapter;
	private List<SectScale> sectScales;
	protected List<LineSeg> lineSegs = new ArrayList<>();

	public MarkableImageView() {
		super();
		this.setLayout(new BorderLayout());
	}

	public void initMark() {
		if (markPanel == null) {
			markPanel = new MarkPanel();
//			Rectangle rect = this.getDrawRect();
//			markPanel.setBounds(rect);
			markPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
			this.add(markPanel);
		}
		if (adapter == null) {
			adapter = new MarkAdapter(markPanel);
			this.addMouseListener(adapter);
			this.addMouseMotionListener(adapter);
		}
	}

	public void setSectScale(List<SectScale> setScales) {
		this.sectScales = setScales;
	}

	class MarkPanel extends JPanel {
		MarkPanel() {
			super();
			this.setOpaque(false);

		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(DEFAULT_COLOR);
			g2d.setStroke(new BasicStroke(3.0f));
			g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
			for (LineSeg lineSeg : lineSegs) {
				if (lineSeg.p1 != null && lineSeg.p2 != null) {
					g2d.drawLine(lineSeg.p1.x, lineSeg.p1.y, lineSeg.p2.x, lineSeg.p2.y);
					double scale = 1d;
					for (SectScale sectScale : sectScales) {
						if (lineSeg.p1.x >= sectScale.x1 && lineSeg.p1.x <= sectScale.x2) {
							scale = sectScale.scale;
							break;
						}
					}
					double length = lineSeg.p1.distance(lineSeg.p2) * scale;
					g2d.drawString(Utils.formatString(length), (lineSeg.p1.x + lineSeg.p2.x) / 2 + 10,
							(lineSeg.p1.y + lineSeg.p2.y) / 2);
				}
			}

		}

	}

	class MarkAdapter extends MouseAdapter {
		static final int FLAG_NONE = 0;
		static final int FLAG_NEW = 1;
		static final int FLAG_EDIT1 = 2;
		static final int FLAG_EDIT2 = 3;
		static final int FLAG_DEL = 4;
		static final int DEFAULT_DISTANCE = 5;
		private Point start;
		private int flag;
		private int index;
		private JComponent comp;

		MarkAdapter(JComponent comp) {
			super();
			this.comp = comp;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON3)
				return;
			for (int i = 0; i < lineSegs.size(); i++) {
				LineSeg lineSeg = lineSegs.get(i);
				double distance = Line2D.ptSegDist(lineSeg.p1.x, lineSeg.p1.y, lineSeg.p2.x, lineSeg.p2.y,
						e.getPoint().getX(), e.getPoint().y);
				if (distance < DEFAULT_DISTANCE) {
					index = i;
					flag = FLAG_DEL;
				}
			}
			if (flag == FLAG_DEL) {
				lineSegs.remove(index);
				comp.repaint();
			}
			flag = FLAG_NONE;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON1) {
				flag = FLAG_NONE;
				return;
			}
			start = e.getPoint();
			index = lineSegs.size();
			flag = FLAG_NEW;
			for (int i = 0; i < lineSegs.size(); i++) {
				LineSeg lineSeg = lineSegs.get(i);
				if (start.distance(lineSeg.p1) < DEFAULT_DISTANCE) {
					flag = FLAG_EDIT1;
					index = i;
					break;
				} else if (start.distance(lineSeg.p2) < DEFAULT_DISTANCE) {
					flag = FLAG_EDIT2;
					index = i;
					break;
				}
			}

			if (flag == FLAG_NEW) {
				LineSeg lineSeg = new LineSeg();
				lineSeg.p1 = start;
				lineSegs.add(lineSeg);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			Point tmp = e.getPoint();
			if (flag == FLAG_NEW) {
				if (tmp.equals(start)) {
					lineSegs.remove(index);
				} else {
					lineSegs.get(index).p2 = tmp;
				}
				comp.repaint();
			} else if (flag == FLAG_EDIT2) {
				if (tmp.equals(lineSegs.get(index).p1)) {
					lineSegs.remove(index);
				} else {
					lineSegs.get(index).p2 = tmp;
				}
				comp.repaint();
			} else if (flag == FLAG_EDIT1) {
				if (tmp.equals(lineSegs.get(index).p2)) {
					lineSegs.remove(index);
				} else {
					lineSegs.get(index).p1 = tmp;
				}
				comp.repaint();
			}
			flag = FLAG_NONE;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			Point tmp = e.getPoint();
			if (flag == FLAG_NEW || flag == FLAG_EDIT2) {
				lineSegs.get(index).p2 = tmp;
				comp.repaint();
			} else if (flag == FLAG_EDIT1) {
				lineSegs.get(index).p1 = tmp;
				comp.repaint();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			comp.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}

	}

	class LineSeg {
		Point p1;
		Point p2;
	}

}
