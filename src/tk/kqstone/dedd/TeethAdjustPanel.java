package tk.kqstone.dedd;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Teeth adjust panel
 * 
 * @author kqstone
 *
 */
public class TeethAdjustPanel extends ZoomableJPanel {
	public static final int MODE_CONTOUR = 0;
	public static final int MODE_REAL = 1;
	private Map<Integer, BufferedImage> teethContourImage;
	private Map<Integer, BufferedImage> teethRealImage;
	private List<ToothPanel> teethPanel;
	private ImageView mask;
	private int showMode;
	private boolean showBorderAndText;
	private boolean dragable;

	private int bright;
	private int yellow;

	public TeethAdjustPanel() {
		super();
		this.setLayout(null);
		this.setOpaque(false);
		teethPanel = new ArrayList<>();
		showMode = MODE_CONTOUR;
		showBorderAndText = true;
		dragable = true;
		bright = 0;
		yellow = 0;
		MouseListener l = new MouseListener();
		this.addMouseListener(l);
		this.addMouseMotionListener(l);

	}

	public TeethAdjustPanel(Map<Integer, BufferedImage> contourImage, Map<Integer, BufferedImage> realImage) {
		this();
		this.teethContourImage = contourImage;
		this.teethRealImage = realImage;
	}

	public TeethAdjustPanel(boolean showBorderAndText) {
		this();
		this.showBorderAndText = showBorderAndText;
	}

	public TeethAdjustPanel(Map<Integer, BufferedImage> contourImage, Map<Integer, BufferedImage> realImage,
			boolean showBorderAndText) {
		this(contourImage, realImage);
		this.showBorderAndText = showBorderAndText;
	}

	class MouseListener extends MouseAdapter {
		private boolean isButton3Drag = false;
		private Point start;

		int brightnow;
		int yellownow;
		int brightChange;
		int yellowChange;

		@Override
		public void mouseDragged(MouseEvent e) {
			if (isButton3Drag) {
				Point now = e.getPoint();
				int xChange = start.x - now.x;
				int yChange = start.y - now.y;
				brightChange = brightnow + yChange / 20;

				yellowChange = yellownow + xChange / 50;
				changeBrightAndYellow(brightChange, yellowChange);

//				int yChange =  start.y-now.y;
//				if (yChange%10 == 0) {	
//					int brightChange = 0;
//					if (yChange >0 ) {
//						brightChange = 10;
//					} else if(yChange<0) {
//						brightChange = -10;
//					}
//					start = now;
//					changeBrightness(brightChange);
//				}
			}

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				isButton3Drag = true;
				start = e.getPoint();
				brightnow = bright;
				yellownow = yellow;
			}

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (isButton3Drag) {
				start = null;
				isButton3Drag = false;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON2) {
				int mode = (showMode == MODE_REAL) ? MODE_CONTOUR : MODE_REAL;
				changeMode(mode);
			}
		}

	}

	public void initialize(List<Tooth> teeth) {
		for (ToothPanel tp : teethPanel) {
			this.remove(tp);
		}
		teethPanel.clear();
//		this.removeAll();
		for (Tooth tooth : teeth) {
			addTooth(tooth);
		}
	}

	public void addTooth(Tooth tooth) {
		int site = tooth.site();
		for (ToothPanel tp : teethPanel) {
			if (site == tp.getSite()) {
				teethPanel.remove(tp);
				this.remove(tp);
				break;
			}
		}
		
		Rect2D rect = tooth.rect();
		float x1 = (float) rect.getX1() * proportion + offsetX;
		float x2 = (float) rect.getX2() * proportion + offsetX;
		float y1 = (float) rect.getY1() * proportion + offsetY;
		float y2 = (float) rect.getY2() * proportion + offsetY;
		ToothPanel tp = new ToothPanel();
		tp.setSite(site);
		tp.getSimpleDrawableBorderRect().setPoint(new Point2D.Float(x1, y1), new Point2D.Float(x2, y2));
		tp.setScale(tooth.scale());
		tp.setProportion(proportion);
		tp.setOffsetX(offsetX);
		tp.setOffsetY(offsetY);

		BufferedImage image;
		boolean showBorder;
		boolean showText;

		if (showMode == MODE_REAL) {
			image = teethRealImage.get(site);
			try {
				image = ImageUtils.adjustBrightAndYellow(image, bright, yellow);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showBorder = false;
			showText = false;
		} else {
			image = teethContourImage.get(site);
			showBorder = true;
			showText = true;
		}
		tp.setLabelVisable(showBorderAndText ? showText : false);
		tp.getSimpleDrawableBorderRect().setDrawBorder(showBorderAndText ? showBorder : false);
		tp.setImage(image);
		for (ToothPanel tp1 : teethPanel) {
			int tmpsite = tp1.getSite();
			if (site != tmpsite && site % 10 == tmpsite % 10) {
				tp1.bindV(tp);
				tp.bindV(tp1);
			}
			int formatTmpSite = formatToothSite(tmpsite);
			int formatSite = formatToothSite(site);
			if (formatSite - formatTmpSite == 1) {
				tp.bindHL(tp1);
				tp1.bindHR(tp);
			} else if (formatSite - formatTmpSite == -1) {
				tp.bindHR(tp1);
				tp1.bindHL(tp);
			}
		}
		teethPanel.add(tp);
		this.add(tp);
		tp.setBoundsWithBinded(false);
		tp.setInitRect(tp.getRect());
		tp.setDragable(dragable);
	}

	private int formatToothSite(int site) {
		int r = 0;
		switch (site) {
		case 13:
			r = 6;
			break;
		case 12:
			r = 7;
			break;
		case 11:
			r = 8;
			break;
		case 21:
			r = 9;
			break;
		case 22:
			r = 10;
			break;
		case 23:
			r = 11;
			break;
		}
		return r;

	}

	public void addTeeth(List<Tooth> teeth) {
		for (Tooth tooth : teeth) {
			addTooth(tooth);
		}
	}

	public void setMask(BufferedImage image) {
		if (this.mask == null) {
			this.mask = new ImageView();
			this.add(mask);
			this.setComponentZOrder(mask, 0);
			mask.setBounds(0, 0, this.getWidth(), this.getHeight());
			mask.disableFocusable();
		}
		mask.setImage(image);
	}

	@Deprecated
	private void changeBrightness(int brightChange) {
		if (brightChange == 0 || this.showMode != MODE_REAL)
			return;
		this.bright += brightChange;
		for (ToothPanel tooth : teethPanel) {
			int site = tooth.getSite();
			BufferedImage image = teethRealImage.get(site);
			try {
				image = ImageUtils.lumAdjustment(image, bright);
				tooth.setImage(image);
				tooth.repaint();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void changeBrightAndYellow(int bright, int yellow) {
		if (this.showMode != MODE_REAL)
			return;
		if (bright == this.bright && yellow == this.yellow)
			return;
		for (ToothPanel tooth : teethPanel) {
			int site = tooth.getSite();
			BufferedImage image = teethRealImage.get(site);
			try {
				image = ImageUtils.adjustBrightAndYellow(image, bright, yellow);
				tooth.setImage(image);
				tooth.repaint();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		this.yellow = yellow;
		this.bright = bright;
	}

	public void changeMode(int mode) {
		if (mode == showMode)
			return;
		showMode = mode;
		for (ToothPanel tooth : teethPanel) {
			int site = tooth.getSite();
			BufferedImage image;
			boolean showBorder;
			boolean showText;
			if (mode == MODE_REAL) {
				image = teethRealImage.get(site);
				try {
					image = ImageUtils.adjustBrightAndYellow(image, bright, yellow);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showBorder = false;
				showText = false;
			} else {
				image = teethContourImage.get(site);
				showBorder = true;
				showText = true;
			}
			tooth.setImage(image);
			tooth.setLabelVisable(showBorderAndText ? showText : false);
			tooth.getSimpleDrawableBorderRect().setDrawBorder(showBorderAndText ? showBorder : false);
			tooth.repaint();
		}
	}

	public int getMode() {
		return this.showMode;
	}

	public void setDragable(boolean dragable) {
		if (dragable == this.dragable)
			return;
		this.dragable = dragable;
		for (ToothPanel tooth : teethPanel) {
			tooth.setDragable(dragable);
		}
	}

	@Override
	public void zoom(float proportion, int offsetX, int offsetY) {
		for (ToothPanel tp : teethPanel) {
			Rect2D rect = tp.getSimpleDrawableBorderRect();
			rect.zoom(this.proportion, this.offsetX, this.offsetY, proportion, offsetX, offsetY);
			tp.setBoundsWithBinded(false);
			Rect2D initRect = tp.getInitRect();
			initRect.zoom(this.proportion, this.offsetX, this.offsetY, proportion, offsetX, offsetY);
			tp.setProportion(proportion);
			tp.setOffsetX(offsetX);
			tp.setOffsetY(offsetY);
		}
		if (mask != null)
			mask.zoom(proportion, offsetX, offsetY);
		super.zoom(proportion, offsetX, offsetY);
	}

	public void bind(TeethAdjustPanel adjustPanel) {
		List<ToothPanel> bindedTeethPanel = adjustPanel.teethPanel;
		for (ToothPanel tp : teethPanel) {
			for (ToothPanel bindedTp : bindedTeethPanel) {
				if (tp.getSite() == bindedTp.getSite()) {
					tp.bind(bindedTp);
				}
			}
		}
	}

	public List<Tooth> getCurrentTeeth() {
		List<Tooth> tmp = new ArrayList<>();
		for (ToothPanel tp : teethPanel) {
			Rect2D rect = tp.getSimpleDrawableBorderRect();
			float x1 = ((float) rect.getX1() - offsetX) / proportion;
			float x2 = ((float) rect.getX2() - offsetX) / proportion;
			float y1 = ((float) rect.getY1() - offsetY) / proportion;
			float y2 = ((float) rect.getY2() - offsetY) / proportion;
			double scale = tp.getScale();
			Tooth t = new Tooth(tp.getSite(), new Rect2D.Float(x1, y1, x2, y2), scale);
			tmp.add(t);
		}
		return tmp;
	}

	public List<SectScale> getSectScales() {
		List<SectScale> tmp = new ArrayList<>();
		for (ToothPanel tp : teethPanel) {
			Rect2D rect = tp.getSimpleDrawableBorderRect();
			int x1 = (int) ((rect.getX1() - offsetX) / proportion);
			int x2 = (int) ((rect.getX2() - offsetX) / proportion);
			double scale = tp.getScale();
			SectScale ss = new SectScale();
			ss.x1 = x1;
			ss.x2 = x2;
			ss.scale = scale;
			tmp.add(ss);
		}
		return tmp;
	}

	private BufferedImage creatImageFromPath(String path) {
		InputStream input = this.getClass().getResourceAsStream(path);
		BufferedImage image = null;
		try {
			image = ImageIO.read(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}

	public void setTeethBrightAndYellow(int adjustedTeethBright, int adjustedTeethYellow) {
		this.bright = adjustedTeethBright;
		this.yellow = adjustedTeethYellow;

	}

	public int getAdjustTeethBright() {
		return this.bright;
	}

	public int getAdjustTeethYellow() {
		return this.yellow;
	}
	
	public void optimize() {
		IOptimizer op = new TeethOptimizer();
		op.setSrc(getCurrentTeeth());
		op.analysis();
		op.optimize();
		List<Tooth> tmp = (List<Tooth>) op.getDst();
		for (Tooth t : tmp) {
			for (ToothPanel tp : teethPanel) {
				if (t.site() == tp.getSite()) {
					Rect2D rect = t.rect();
					float x1 = (float) rect.getX1() * proportion + offsetX;
					float x2 = (float) rect.getX2() * proportion + offsetX;
					float y1 = (float) rect.getY1() * proportion + offsetY;
					float y2 = (float) rect.getY2() * proportion + offsetY;
					rect = new Rect2D.Float(x1, y1, x2, y2);
					tp.getSimpleDrawableBorderRect().setRect(rect);
					tp.setBounds2();
					tp.setProportion(proportion);
					tp.setOffsetX(offsetX);
					tp.setOffsetY(offsetY);
					tp.setInitRect(rect);
				}
			}
		}
	}
	
	public void align() {
		IOptimizer op = new TeethOptimizer();
		op.setSrc(getCurrentTeeth());
		op.analysis();
		op.align();
		List<Tooth> tmp = (List<Tooth>) op.getDst();
		for (Tooth t : tmp) {
			for (ToothPanel tp : teethPanel) {
				if (t.site() == tp.getSite()) {
					Rect2D rect = t.rect();
					float x1 = (float) rect.getX1() * proportion + offsetX;
					float x2 = (float) rect.getX2() * proportion + offsetX;
					float y1 = (float) rect.getY1() * proportion + offsetY;
					float y2 = (float) rect.getY2() * proportion + offsetY;
					rect = new Rect2D.Float(x1, y1, x2, y2);
					tp.getSimpleDrawableBorderRect().setRect(rect);
					tp.setBounds2();
					tp.setProportion(proportion);
					tp.setOffsetX(offsetX);
					tp.setOffsetY(offsetY);
					tp.setInitRect(rect);
				}
			}
		}
	}

}
