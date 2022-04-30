package tk.kqstone.dedd;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import tk.kqstone.dedd.ui.IMethod;
import tk.kqstone.dedd.ui.IconButton;
import tk.kqstone.dedd.ui.IconButtonPane;

import java.awt.Container;
import java.awt.FlowLayout;

/**
 * ImageView that can double clicked to open photo and drag to rotate or crop
 * 
 * @author kqstone
 *
 */
public class EditableImageView extends ImageView implements IImageBinder {

	private static final int POINT_RADIUS = 4;

	private static final int CROP = 0;
	private static final int ROTATE = 1;
	private static final int UNIFY = 2;

	/**
	 * Default Mark Color when mouse drag
	 */
	private static final Color MARK_COLOR = Color.YELLOW;

	private MarkPanel markPanel;

	private IImageBinder imageBinder;	
	private boolean binded;

	private boolean unifiable;
	private boolean editable;
	private MouseAdapter editableMouseAdapter;
	private MouseAdapter doubleClickAdapter;
	private MouseAdapter unifiablemouseAdapter;
	private EditableKeyAdapter keyAdapter;
	
//	private IconButtonPane iconButtons;

	private boolean escPressed;

	public EditableImageView() {
		super();
		this.setLayout(new BorderLayout());
		markPanel = new MarkPanel();
		markPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.add(markPanel);
		binded = false;
		unifiable = false;
		editable = false;
		escPressed = false;
	}

	public void bind(IImageBinder imageBinder) {
		this.imageBinder = imageBinder;
	}
	
	public void unbind() {
		this.imageBinder = null;
	}
	
	public void setbinded(boolean binded) {
		this.binded = binded;
	}

	public void setEditable(boolean editable) {
		if (this.editable == editable)
			return;
		if (editable) {
			if (doubleClickAdapter == null) {
				doubleClickAdapter = new MouseDoubleClickAdapter();
			}
			this.addMouseListener(doubleClickAdapter);
			if (editableMouseAdapter == null) {
				editableMouseAdapter = new EditableMouseAdapter(this);				
			}
			this.addMouseListener(editableMouseAdapter);
			this.addMouseMotionListener(editableMouseAdapter);
			if (keyAdapter ==null) {
				keyAdapter = new EditableKeyAdapter();
			}
			this.addKeyListener(keyAdapter);
			markPanel.clear();
				
		} else {
			this.removeMouseListener(doubleClickAdapter);
			this.removeMouseListener(editableMouseAdapter);
			this.removeMouseMotionListener(editableMouseAdapter);
			this.removeKeyListener(keyAdapter);
		}
		this.editable = editable;
	}

	public void setUnifiable(boolean unifiable) {
		if (this.unifiable == unifiable)
			return;
		if (unifiable) {

			if (unifiablemouseAdapter == null) {
				unifiablemouseAdapter = new UnifiableMouseAdapter();
			}
			this.addMouseListener(unifiablemouseAdapter);
			this.addMouseMotionListener(unifiablemouseAdapter);
		} else {
			this.removeMouseListener(unifiablemouseAdapter);
			this.removeMouseMotionListener(unifiablemouseAdapter);
		}

		this.unifiable = unifiable;
	}

	public boolean isUnifiable() {
		return this.unifiable;
	}

	@Override
	public void setImage(BufferedImage image) {
		// TODO Auto-generated method stub
		super.setImage(image);
//		setEditable(true);
//		removeMouseListener(doubleClickAdapter);
	}

	class EditableMouseAdapter extends MouseAdapter {
		private ImageView panel;
		Point start;

		EditableMouseAdapter(ImageView panel) {
			this.panel = panel;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (panel.getImage() == null)
				return;
			requestFocus(true);
			escPressed = false;
			start = e.getPoint();
			if (e.getButton() == MouseEvent.BUTTON1) {
				markPanel.point1 = e.getPoint();
				markPanel.flag = ROTATE;
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				markPanel.point1 = e.getPoint();
				markPanel.flag = CROP;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (panel.getImage() == null || escPressed) {
				return;
			}
			Point end = e.getPoint();
			if (start.equals(end)) {
				return;
			}
			if (markPanel.flag == EditableImageView.CROP) {
				BufferedImage image = panel.getImage();
				double scale = panel.getScale();
				int scalType = panel.getScaleType();
				Rect rect = new Rect(start, end);
				int x1 = Math.round((start.x-panel.offsetX) / panel.proportion);
				int y1 = Math.round((start.y-panel.offsetY) / panel.proportion);
				int x2 = Math.round((end.x-panel.offsetX) / panel.proportion);
				int y2 = Math.round((end.y-panel.offsetY) / panel.proportion);
				x1 = (int) (x1 / scale);
				y1 = (int) (y1 / scale);
				x2 = (int) (x2 / scale);
				y2 = (int) (y2 / scale);
				if (scalType == ImageView.SCALE_ADAPT_WIDTH) {
					int offset = (int) ((panel.getHeight() / scale - image.getHeight()) / 2);
					y1 = y1 - offset;
					y2 = y2 - offset;
				} else if (scalType == ImageView.SCALE_ADAPT_HEIGHT) {
					int offset = (int) ((panel.getWidth() / scale - image.getWidth()) / 2);
					x1 = x1 - offset;
					x2 = x2 - offset;
				}
				
				

				Point p1 = new Point(x1, y1);
				Point p2 = new Point(x2, y2);

				
				BufferedImage img = ImageUtils.crop(image, new Rect(p1, p2));
				panel.setImage(img);
				panel.zoom(1.0f, 0, 0);
			} else if (markPanel.flag == EditableImageView.ROTATE) {
				int width = end.x - start.x;
				int height = end.y - start.y;
				double tanA = height / (double) width;
				double radius = -Math.atan(tanA);
				rotate(radius);
				if (imageBinder != null)
					imageBinder.rotate(radius);
			}
			markPanel.point1 = null;
			markPanel.point2 = null;
			markPanel.repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (panel.getImage() == null || escPressed)
				return;
			markPanel.point2 = e.getPoint();
			markPanel.repaint();

		}

	}
	
	class UnifiableMouseAdapter extends MouseAdapter {
		Point start;
		private int index = 0;


		@Override
		public void mousePressed(MouseEvent e) {
			requestFocus(true);
			start = e.getPoint();
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (markPanel.point3 != null && start.distance(markPanel.point3) <= 10) {
					index = 1;
					markPanel.flag = UNIFY;
				} else if (markPanel.point4 != null && start.distance(markPanel.point4) <= 10) {
					index = 2;
					markPanel.flag = UNIFY;
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (markPanel.flag == UNIFY) {
				index = 0;

				unifyBinded();
				return;
			}
			markPanel.repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (markPanel.flag == UNIFY) {
				if (index == 0)
					return;
				Point p = (index == 1) ? markPanel.point3 : markPanel.point4;
				p.setLocation(e.getPoint());
				markPanel.repaint();

				return;
			}

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON1)
				return;
			if (markPanel.point3 == null) {
				markPanel.point3 = e.getPoint();
			} else if (markPanel.point4 == null) {
				markPanel.point4 = e.getPoint();
				unifyBinded();
			}

			markPanel.repaint();
		}

	}

	@Override
	public void rotate(double radius) {
		BufferedImage img = ImageUtils.rotate(this.getImage(), radius);
		this.setImage(img);
		Point2D.Float cPoint = new Point2D.Float((float) (this.getWidth() * 0.5f * super.proportion + offsetX),
				(float) (this.getHeight() * 0.5f * proportion + offsetY));
		Point p1 = this.markPanel.point3;
		Point p2 = this.markPanel.point4;
		if (p1 == null && p2 == null)
			return;
		if (p1 != null) {
			Point2D.Float A = new Point2D.Float(p1.x - cPoint.x, p1.y - cPoint.y);
			PolarPoint PA = PolarPoint.toPolarPoint(A);
			PA.thelta += radius;
			Point2D.Double CA = PolarPoint.toCartierPoint(PA);
			p1.setLocation(CA.x + cPoint.x, CA.y + cPoint.y);
		}
		if (p2 != null) {
			Point2D.Float B = new Point2D.Float(p2.x - cPoint.x, p2.y - cPoint.y);
			PolarPoint PB = PolarPoint.toPolarPoint(B);
			PB.thelta += radius;
			Point2D.Double CB = PolarPoint.toCartierPoint(PB);
			p2.setLocation(CB.x + cPoint.x, CB.y + cPoint.y);
		}
		this.getMarkPanel().repaint();
	}

	private void unifyBinded() {
		if (imageBinder == null)
			return;
		Point bindedP1 = imageBinder.getMarkPanel().point3 == null ? null : imageBinder.getMarkPanel().point3;
		Point bindedP2 = imageBinder.getMarkPanel().point4 == null ? null : imageBinder.getMarkPanel().point4;
		Point p1 = this.markPanel.point3;
		Point p2 = this.markPanel.point4;
		if (bindedP1 == null || bindedP2 == null || p1 == null || p2 == null)
			return;
		double bindedRadius = Math.atan2((bindedP2.y - bindedP1.y), (double) (bindedP2.x - bindedP1.x));
		double radius = bindedRadius - Math.atan2((p2.y - p1.y), (double) (p2.x - p1.x));
		if (radius == 0d)
			return;
		if (!binded) {
			imageBinder.rotate(-radius);
		} else {
			this.rotate(radius);
		}
		
	}

	class MouseDoubleClickAdapter extends MouseAdapter {

		private int clickNum = 0;

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON1)
				return;
			if (clickNum == 0) {
				clickNum = 1;
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						clickNum = 0;
						this.cancel();

					}
				}, 300);
			} else if (clickNum == 1) {
				System.out.print("Mouse Double Clicked");
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setSelectedFile(new File(GlobalVariables.sWorkingDir + " "));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("图片文件", "jpg", "png", "bmp");
				chooser.setFileFilter(filter);
				ImagePreview ip = new ImagePreview();
				chooser.setAccessory(ip);
				chooser.addPropertyChangeListener(ip);
				int result = chooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					GlobalVariables.sWorkingDir = file.getParent() + File.separator;
					if (file != null) {
						try {
							BufferedImage image = ImageIO.read(file);
							setImage(image);
							if (markPanel != null)
								markPanel.clear();

						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}

			}
		}
	}

	class EditableKeyAdapter extends KeyAdapter {

		int preMarkPanelFlag;

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				escPressed = true;
				if (markPanel.point2 != null || markPanel.point1 != null) {
					markPanel.point2 = null;
					markPanel.point1 = null;
					markPanel.repaint();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				preMarkPanelFlag = markPanel.flag;
				markPanel.flag = UNIFY;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (markPanel.flag == UNIFY) {
				markPanel.flag = preMarkPanelFlag;
			}
		}

	}

	class MarkPanel extends JPanel {

		private static final int POINTER_SIZE = 16;
		int flag;
		Point point1;
		Point point2;

		Point point3 = null;
		Point point4 = null;

		MarkPanel() {
			super();
			this.setOpaque(false);
//			flag = UNIFY;
		}

		protected void clear() {
			point3 = null;
			point4 = null;
			this.repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(MARK_COLOR);
			Stroke stroke = new BasicStroke(2.0f);
			g2d.setStroke(stroke);
			Point[] points = { point3, point4 };
			for (Point p : points) {
				if (p != null) {
					g.drawLine(p.x - POINTER_SIZE / 2, p.y, p.x + POINTER_SIZE / 2, p.y);
					g.drawLine(p.x, p.y - POINTER_SIZE / 2, p.x, p.y + POINTER_SIZE / 2);
				}
			}

			if (point1 == null || point2 == null)
				return;

			if (flag == EditableImageView.CROP) {
				Rect rect = new Rect(point1, point2);
				g.drawRect(rect.getx1(), rect.gety1(), rect.getWidth(), rect.getHeight());
			} else if (flag == EditableImageView.ROTATE) {
				g.drawLine(point1.x, point1.y, point2.x, point2.y);

			}

		}

	}

	@Override
	public MarkPanel getMarkPanel() {
		if (markPanel != null)
			return markPanel;
		return null;
	}

	@Override
	public void zoom(float proportion, int offsetX, int offsetY) {
		if (markPanel != null) {
			Point p3 = this.getMarkPanel().point3;
			Point p4 = this.getMarkPanel().point4;
			if (p3 != null)
				p3.setLocation((p3.x - this.offsetX) * proportion / this.proportion + offsetX,
						(p3.y - this.offsetY) * proportion / this.proportion + offsetY);
			if (p4 != null)
				p4.setLocation((p4.x - this.offsetX) * proportion / this.proportion + offsetX,
						(p4.y - this.offsetY) * proportion / this.proportion + offsetY);
			this.markPanel.repaint();
		}
		super.zoom(proportion, offsetX, offsetY);
	}

}
