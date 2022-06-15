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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import tk.kqstone.dedd.ui.IMethod;
import tk.kqstone.dedd.ui.IconButton;
import tk.kqstone.dedd.ui.IconButtonPane;

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
	
	private IconButtonPane imageEditButtonPane;
	private IconButtonPane teethAdjustButtonPane;

	protected List<Tooth> markedTeeth;

	private int status;
	
	private ZoomInvoker zoomInvoker;
	
	private MarkDataUpload markDataUpload;

	public WorkPanel() {
		super();
		this.setLayout(null);
		this.viewId = BASE_VIEW;
		markDataUpload = new MarkDataUpload();
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
			adjustPanel.setShowMode(TeethAdjustPanel.MODE_REAL);
			adjustPanel.setShowContour(false);
			adjustPanel.setDragable(false);
		}

		adjustPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
		this.add(adjustPanel, 0);

		initIconButtonPane();
		
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

	private void initIconButtonPane() {
		imageEditButtonPane = new IconButtonPane();
		this.add(imageEditButtonPane, 0);
		imageEditButtonPane.setBounds(0, this.getHeight() - 50, this.getWidth(), this.getHeight());
		final IconButton rotateL90DButton = new IconButton(Constant.ROTATE_LEFT_90D,
				new ImageIcon(this.getClass().getResource(Resources.URL_ROTATELEFT)));
		final IconButton rotateR90DButton = new IconButton(Constant.ROTATE_RIGHT_90D,
				new ImageIcon(this.getClass().getResource(Resources.URL_ROTATERIGHT)));
		imageEditButtonPane.addButton(rotateL90DButton);
		imageEditButtonPane.addButton(rotateR90DButton);
		imageEditButtonPane.addActions(new IMethod() {

			@Override
			public void run(Object obj) throws Exception {
				if (obj.equals(rotateL90DButton)) {
					BufferedImage image = ImageUtils.rotate90D(getImage(), "left");
					imageView.setImage(image);
				} else if (obj.equals(rotateR90DButton)) {
					BufferedImage image = ImageUtils.rotate90D(getImage(), "right");
					imageView.setImage(image);
				}
			}
		});

		teethAdjustButtonPane = new IconButtonPane();
		this.add(teethAdjustButtonPane, 0);
		teethAdjustButtonPane.setBounds(0, this.getHeight() - 50, this.getWidth(), this.getHeight());
		final IconButton contourButton = new IconButton(Constant.SHOW_CONTOUR,
				new ImageIcon(this.getClass().getResource(Resources.URL_MODE_CONTOUR)));
		final IconButton realButton = new IconButton(Constant.SHOW_REAL,
				new ImageIcon(this.getClass().getResource(Resources.URL_MODE_REAL)));
		final IconButton noneButton = new IconButton(Constant.SHOW_NONE,
				new ImageIcon(this.getClass().getResource(Resources.URL_MODE_NONE)));
		teethAdjustButtonPane.addButton(contourButton);
		teethAdjustButtonPane.addButton(realButton);
		teethAdjustButtonPane.addButton(noneButton);
		teethAdjustButtonPane.addActions(new IMethod() {

			@Override
			public void run(Object obj) throws Exception {
				if (obj.equals(contourButton)) {
					adjustPanel.changeMode(TeethAdjustPanel.MODE_CONTOUR);
				} else if (obj.equals(realButton)) {
					adjustPanel.changeMode(TeethAdjustPanel.MODE_REAL);
				} else if (obj.equals(noneButton)) {
					adjustPanel.changeMode(TeethAdjustPanel.MODE_NONE);
				}
			}
		});
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
		if (! imageEditButtonPane.isVisible())
			imageEditButtonPane.setVisible(true);
		if (teethAdjustButtonPane.isVisible())
			teethAdjustButtonPane.setVisible(false);
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
		if (imageEditButtonPane.isVisible())
			imageEditButtonPane.setVisible(false);
		if (teethAdjustButtonPane.isVisible())
			teethAdjustButtonPane.setVisible(false);
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
		if (imageEditButtonPane.isVisible())
			imageEditButtonPane.setVisible(false);
		if (teethAdjustButtonPane.isVisible())
			teethAdjustButtonPane.setVisible(false);
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
		if (imageEditButtonPane.isVisible())
			imageEditButtonPane.setVisible(false);
		if (teethAdjustButtonPane.isVisible())
			teethAdjustButtonPane.setVisible(false);
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
		if (imageEditButtonPane.isVisible())
			imageEditButtonPane.setVisible(false);
		if (!teethAdjustButtonPane.isVisible())
			teethAdjustButtonPane.setVisible(true);
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
	
	public BufferedImage genFullPreImage() {
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
	
	private Map<String, Rectangle2D> getMarkTeethRectData4Yolo() {
		Rectangle drawRect = this.getImageView().getDrawRect();
		List<Tooth> teeth = this.getMarkedTeeth();
		Map<String, Rectangle2D> result = new HashMap<>();
		for(Tooth tooth:teeth) {
			String name = "T" + (tooth.site() % 10);
			Rect2D rect = tooth.rect();
			double x = (rect.getX1() - drawRect.x + rect.getWidth() / 2) / drawRect.width;
			double y = (rect.getY1() - drawRect.y + rect.getHeight() / 2)  / drawRect.height;
			double width = rect.getWidth() / drawRect.width;
			double height = rect.getHeight() / drawRect.height;
			Rectangle2D rect2d = new Rectangle2D.Double(x,y,width,height);
			result.put(name, rect2d);
		}
		return result;
	}
	
	public void uploadData() {
		markDataUpload.setImage(this.genPreImage());
		markDataUpload.setMarkdata(getMarkTeethRectData4Yolo());
	}

}
