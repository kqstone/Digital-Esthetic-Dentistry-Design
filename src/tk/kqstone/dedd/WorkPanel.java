package tk.kqstone.dedd;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class WorkPanel extends Container {

	public static final int BASE_VIEW = 0;
	public static final int FRONT_VIEW = 1;

	public static final int STATUS_UNIFYIMAGE = 0;
	public static final int STATUS_MARKTEETH = 1;
	public static final int STATUS_ADJUSTTEETH = 2;
	public static final int STATUS_MARKLIP = 3;
	public static final int STATUS_EDITIMAGE = 4;

	private int viewId;

	private EditableImageView imageView;
	private CurvePanel markLipPanel;
	private TeethMarkPanel markPanel;
	private TeethAdjustPanel adjustPanel;

	protected List<Tooth> markedTeeth;

	private int status;
	
	private ZoomInvoker zoomInvoker;

	public WorkPanel() {
		super();
		this.setLayout(null);
		this.viewId = BASE_VIEW;
	}

	public WorkPanel(int viewId) {
		this();
		this.viewId = viewId;
	}

	public int getViewId() {
		return viewId;
	}

	public TeethMarkPanel getMarkPanel() {
		return markPanel;
	}

	public EditableImageView getImageView() {
		return imageView;
	}

	public TeethAdjustPanel getAdjustPanel() {
		return adjustPanel;
	}

	public CurvePanel getMarkLipPanel() {
		return markLipPanel;
	}

	public void init() {
		imageView = new EditableImageView();
		imageView.setText("双击选择照片文件！");

		this.add(imageView);
		imageView.setBounds(0, 0, this.getWidth(), this.getHeight());
		if (viewId == WorkPanel.FRONT_VIEW) {
			markLipPanel = new CurvePanel();
			markLipPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
			this.add(markLipPanel, 0);
		}
		markPanel = new TeethMarkPanel();
		markPanel.setViewId(viewId);
		markPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.add(markPanel, 0);
		if (viewId == WorkPanel.BASE_VIEW) {
			adjustPanel = new TeethAdjustPanel(TeethImage.getTeethContour(), TeethImage.getTeethReal());
			adjustPanel.setDragable(true);
		} else {
			adjustPanel = new TeethAdjustPanel(TeethImage.getTeethContour(), TeethImage.getTeethRealFront(), false);
			adjustPanel.setDragable(false);
		}

		adjustPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.add(adjustPanel, 0);

		status = WorkPanel.STATUS_EDITIMAGE;
		
		zoomInvoker = new ZoomInvoker();
		zoomInvoker.addReceiver(imageView);
		zoomInvoker.addReceiver(markLipPanel);
		zoomInvoker.addReceiver(markPanel);
		zoomInvoker.addReceiver(adjustPanel);
		imageView.setZoomInvoker(zoomInvoker);
		if (markLipPanel != null) {
			markLipPanel.setZoomInvoker(zoomInvoker);
		}
		markPanel.setZoomInvoker(zoomInvoker);
		adjustPanel.setZoomInvoker(zoomInvoker);

	}

	public void setImage(BufferedImage image) {
		this.imageView.setImage(image);
	}

	public void addImageChangeListener(ImageChangeListener listener) {
		this.imageView.addImageChangeListener(listener);
	}

	public void initMarkPanel(List<Tooth> teeth) {
		markPanel.initialize(teeth);
		markedTeeth = teeth;
	}

	public void initAdjustPanel(List<Tooth> adjustTeeth) {
		adjustPanel.initialize(adjustTeeth);
	}

	public void edit() {
		if (imageView == null)
			return;
		if (markLipPanel != null && markLipPanel.isVisible()) {
			markLipPanel.setVisible(false);
		}
		imageView.setEditable(true);
		imageView.setUnifiable(false);
		if (markPanel.isVisible())
			markPanel.setVisible(false);
		if (adjustPanel.isVisible())
			adjustPanel.setVisible(false);
		status = WorkPanel.STATUS_UNIFYIMAGE;
	}

	public void unify() {
		if (imageView == null)
			return;
		if (markLipPanel != null && markLipPanel.isVisible()) {
			markLipPanel.setVisible(false);
		}
		imageView.setEditable(false);
		if (imageView.isUnifiable() != true)
			imageView.setUnifiable(true);
		if (markPanel.isVisible())
			markPanel.setVisible(false);
		if (adjustPanel.isVisible())
			adjustPanel.setVisible(false);
		status = WorkPanel.STATUS_UNIFYIMAGE;
	}

	public void marklip() {
		if (markLipPanel != null && !markLipPanel.isVisible()) {
			markLipPanel.setVisible(true);
		}
		if (markPanel.isVisible())
			markPanel.setVisible(false);
		if (adjustPanel.isVisible())
			adjustPanel.setVisible(false);

		((EditableImageView) imageView).setUnifiable(false);
		imageView.setEditable(false);
		status = WorkPanel.STATUS_MARKLIP;
	}

	public void mark() {
		if (markLipPanel != null && markLipPanel.isVisible()) {
			markLipPanel.setVisible(false);
		}
		if (!markPanel.isVisible())
			markPanel.setVisible(true);
		if (adjustPanel.isVisible())
			adjustPanel.setVisible(false);

		((EditableImageView) imageView).setUnifiable(false);
		imageView.setEditable(false);
		status = WorkPanel.STATUS_MARKTEETH;
	}

	@Deprecated
	public void adjust() {
//		zoom(1d,0,0);

		List<Tooth> currentTeeth = markPanel.getTeeth();
		IOptimizer op = new TeethOptimizer();
		op.setSrc(currentTeeth);
		op.analysis();
		currentTeeth = (List<Tooth>) op.getSrc();
		if (markedTeeth == null || markedTeeth.isEmpty()) {
			adjustPanel.addTeeth(currentTeeth);
		} else {
			List<Tooth> tmpTeeth = new ArrayList<>(currentTeeth);
			for (Tooth ctooth : currentTeeth) {
				for (Tooth otooth : markedTeeth) {
					if (ctooth.equals(otooth)) {
						tmpTeeth.remove(ctooth);
						break;
					}
				}
			}
			adjustPanel.addTeeth(tmpTeeth);
		}
		markedTeeth = currentTeeth;

		if (markLipPanel != null && markLipPanel.isVisible()) {
			markLipPanel.setVisible(false);
		}
		if (markPanel.isVisible())
			markPanel.setVisible(false);
		if (!adjustPanel.isVisible())
			adjustPanel.setVisible(true);
		((EditableImageView) imageView).setUnifiable(false);
		status = WorkPanel.STATUS_ADJUSTTEETH;
	}

	public void adjust(List<Tooth> teeth) {

		IOptimizer op = new TeethOptimizer();
		op.setSrc(teeth);
		op.analysis();
		teeth = (List<Tooth>) op.getSrc();

		adjustPanel.addTeeth(teeth);

		if (markLipPanel != null && markLipPanel.isVisible()) {
			markLipPanel.setVisible(false);
		}
		if (markPanel.isVisible())
			markPanel.setVisible(false);
		if (!adjustPanel.isVisible())
			adjustPanel.setVisible(true);
		((EditableImageView) imageView).setUnifiable(false);
		imageView.setEditable(false);
		status = WorkPanel.STATUS_ADJUSTTEETH;
//		addMaskToAdjustPanel();
	}

	public List<Tooth> getMarkedTeeth() {
		return this.markPanel.getTeeth();
	}

	public List<Tooth> getAdjustedTeeth() {
		return this.adjustPanel.getCurrentTeeth();
	}

	public BufferedImage getImage() {
		return imageView.getImage();
	}

	public BufferedImage genImage(int mode) {
		zoom(1.0f, 0, 0);
		int currentStatus = this.status;
		if (currentStatus != WorkPanel.STATUS_ADJUSTTEETH)
			adjust();
		BufferedImage image;
		Rectangle rect = imageView.getDrawRect();
		if (rect.width == 0 || rect.height == 0) {
			image = genErrorImage();
		} else {
			int currentMode = adjustPanel.getMode();
			boolean modeChanged = currentMode != mode;
			if (modeChanged)
				adjustPanel.changeMode(mode);
			image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			this.print(g);
			g.dispose();
			image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
			if (modeChanged)
				adjustPanel.changeMode(currentMode);
			switch (currentStatus) {
			case STATUS_UNIFYIMAGE:
				unify();
				break;
			case STATUS_MARKTEETH:
				mark();
				break;
			case STATUS_MARKLIP:
				marklip();
				break;
			default:
				break;
			}
		}

		return image;
	}

	public BufferedImage genPreImage() {
		zoom(1.0f, 0, 0);
		BufferedImage image;
		Rectangle rect = imageView.getDrawRect();
		if (rect.width == 0 || rect.height == 0) {
			image = genErrorImage();
		} else {
			image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics g = image.getGraphics();
			imageView.print(g);
			g.dispose();
			image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
		}
		return image;
	}

	private BufferedImage genErrorImage() {

		BufferedImage image = new BufferedImage(this.getWidth(), 150, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(new Font("等线", Font.BOLD, 18));
		g.setColor(Color.RED);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(Constant.IMPORT_IMAGE_FIRST, (image.getWidth() - fm.stringWidth(Constant.IMPORT_IMAGE_FIRST)) / 2,
				(image.getHeight() + fm.getHeight()) / 2);
		g.dispose();
		return image;
	}

	public void zoom(float proportion, int offsetX, int offsetY) {
		imageView.zoom(proportion, offsetX, offsetY);
		if (markLipPanel != null)
			markLipPanel.zoom(proportion, offsetX, offsetY);
		markPanel.zoom(proportion, offsetX, offsetY);
		adjustPanel.zoom(proportion, offsetX, offsetY);
	}

	public void addMaskToAdjustPanel() {
		if (this.markLipPanel == null)
			return;
		Shape path = this.markLipPanel.getLowerClosedPath();
		if (path == null)
			return;
		BufferedImage image = imageView.genImageFromView(path);
		this.adjustPanel.setMask(image);
	}

}
