package tk.kqstone.dedd;

import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JLabel;

import tk.kqstone.dedd.Rect2D.Float;

public class ToothPanel extends BasicBorderPanel implements BindImpl {
	private static final int LABEL_WIDTH = 80;
	private static final int LABEL_HEIGHT = 40;

	private JLabel toothLength;
	private JLabel toothRatio;
	private double scale;
	private int site;

	private Rect2D initRect;

	private ToothPanel bindedToothPanel;

	private ToothPanel bindedToothPanelV;

	private ToothPanel bindedToothPanelHL;

	private ToothPanel bindedToothPanelHR;

	private boolean labelVisable;
	
	private boolean bindVertical = false;
	
	private float proportion;
	private int offsetX;
	private int offsetY;

	public ToothPanel() {
		super();
//		this.setLayout(null);
		toothLength = new JLabel();
		toothRatio = new JLabel();
		this.add(toothLength);
		this.add(toothRatio);
		labelVisable = true;
		proportion = 1.0f;
		offsetX = 0;
		offsetY = 0;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public double getScale() {
		return scale;
	}
	
	public void setProportion(float proportion) {
		this.proportion = proportion;
	}
	
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public void setLabelVisable(boolean labelVisable) {
		if (labelVisable == this.labelVisable)
			return;
		this.labelVisable = labelVisable;
		toothLength.setVisible(labelVisable);
		toothRatio.setVisible(labelVisable);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setTextFieldLocation(this.getWidth(), this.getHeight());
		setText();

	}

	private void setTextFieldLocation(int width, int height) {
		int x = (width - LABEL_WIDTH) / 2;
		int y = (height - 2 * LABEL_HEIGHT) / 2;

		toothLength.setBounds(x, y, LABEL_WIDTH, LABEL_HEIGHT);
		toothRatio.setBounds(x, y + LABEL_HEIGHT, LABEL_WIDTH, LABEL_HEIGHT);

	}

	private void setText() {
		float pxLength = (float) getSimpleDrawableBorderRect().getHeight();
		float length = pxLength/proportion;

		float ratio = (float) (getSimpleDrawableBorderRect().getWidth() / pxLength);
		if (scale != 0d) {
			length = (float) (length * scale);
		}
		toothLength.setText(Constant.TOOTH_LENGTH + ": " + Utils.formatString(length));
		toothRatio.setText(Constant.TOOTH_RATIO + ": " + Utils.formatString(ratio));
	}

	public int getSite() {
		return site;
	}

	public void setSite(int site) {
		this.site = site;
	}

	@Override
	public void bind(BindImpl bindedImpl) {
		this.bindedToothPanel = (ToothPanel) bindedImpl;
	}

	@Override
	public void bindV(BindImpl bindImpl) {
		this.bindedToothPanelV = (ToothPanel) bindImpl;
	}

	@Override
	public void bindHL(BindImpl bindImpl) {
		this.bindedToothPanelHL = (ToothPanel) bindImpl;

	}

	@Override
	public void bindHR(BindImpl bindImpl) {
		this.bindedToothPanelHR = (ToothPanel) bindImpl;

	}

	@Override
	public void update() {

		Rect2D initBindRect = bindedToothPanel.initRect;
		double heightRatio = initBindRect.getHeight() / initRect.getHeight();
		double widthRatio = initBindRect.getWidth() / initRect.getWidth();
		float y1now = (float) (initBindRect.getY1()
				+ (this.getSimpleDrawableBorderRect().getY1() - this.initRect.getY1()) * heightRatio);
		float y2now = (float) (initBindRect.getY2()
				+ (this.getSimpleDrawableBorderRect().getY2() - this.initRect.getY2()) * heightRatio);
		float x1now = (float) (initBindRect.getX1()
				+ (this.getSimpleDrawableBorderRect().getX1() - this.initRect.getX1()) * widthRatio);
		float x2now = (float) (initBindRect.getX2()
				+ (this.getSimpleDrawableBorderRect().getX2() - this.initRect.getX2()) * widthRatio);
//		int heightnow = (int) (initBindRect.getHeight()*heightChange);
		Rect2D rect = new Rect2D.Float(x1now, y1now, x2now, y2now);
		bindedToothPanel.getSimpleDrawableBorderRect().setRect(rect);
		bindedToothPanel.setBounds2();
	}

	@Override
	public void updateV() {
		float y1 = (float) getSimpleDrawableBorderRect().getY1();
		float y2 = (float) getSimpleDrawableBorderRect().getY2();
		float x1 = (float) bindedToothPanelV.getSimpleDrawableBorderRect().getX1();
		float x2 = (float) bindedToothPanelV.getSimpleDrawableBorderRect().getX2();
		bindedToothPanelV.getSimpleDrawableBorderRect().setRect(new Rect2D.Float(x1, y1, x2, y2));
		bindedToothPanelV.setBounds2();
	}

	@Override
	public void updateHR() {
		float y1 = (float) bindedToothPanelHR.getSimpleDrawableBorderRect().getY1();
		float y2 = (float) bindedToothPanelHR.getSimpleDrawableBorderRect().getY2();
		float x1 = (float) getSimpleDrawableBorderRect().getX2();
		float x2 = (float) bindedToothPanelHR.getSimpleDrawableBorderRect().getX2();
		bindedToothPanelHR.getSimpleDrawableBorderRect().setRect(new Rect2D.Float(x1, y1, x2, y2));
		bindedToothPanelHR.setBounds2();
	}

	@Override
	public void updateHL() {
		float y1 = (float) bindedToothPanelHL.getSimpleDrawableBorderRect().getY1();
		float y2 = (float) bindedToothPanelHL.getSimpleDrawableBorderRect().getY2();
		float x1 = (float) bindedToothPanelHL.getSimpleDrawableBorderRect().getX1();
		float x2 = (float) getSimpleDrawableBorderRect().getX1();
		bindedToothPanelHL.getSimpleDrawableBorderRect().setRect(new Rect2D.Float(x1, y1, x2, y2));
		bindedToothPanelHL.setBounds2();
	}

	@Override
	public void setBounds() {
		super.setBounds();
		if (bindedToothPanel != null)
			update();
		if (bindedToothPanelV != null && EnvVar.sPressed_VK_B)
			updateV();
		if (bindedToothPanelHR != null)
			updateHR();
		if (bindedToothPanelHL != null)
			updateHL();
	}

	public void setBoundsWithBinded(boolean updateBind) {
		if (updateBind) {
			setBounds();
		} else {
			super.setBounds();
		}
	}

	public void setBounds2() {
		super.setBounds();
		if (bindedToothPanel != null)
			update();
	}

	public void setInitRect(Rect2D initRect) {
		this.initRect = initRect;
	}

	public Rect2D getInitRect() {
		return initRect;
	}
	
	public void setBindVertical(boolean b) {
		this.bindVertical = b;
	}
	
}
