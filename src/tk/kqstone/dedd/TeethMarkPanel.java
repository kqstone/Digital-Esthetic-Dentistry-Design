package tk.kqstone.dedd;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class TeethMarkPanel extends BasicDrawablePanel {
	private static final int TEXT_FIELD_WIDTH = 50;
	private static final int TEXT_FIELD_HEIGHT = 40;

	private List<TextField> listTextTooth;
	private List<TextField> listTextLength;
	private List<DrawableBorderRect> listPanelTooth;

	private boolean showTextLength = true;

	public TeethMarkPanel() {
		super();
		this.setLayout(null);
		listTextTooth = new ArrayList<>();
		listTextLength = new ArrayList<>();
		listPanelTooth = super.getBorderPanelList();
	}

	@Deprecated
	public void initFromData() {
		List<Tooth> teeth = TeethData.getInstance().getTeeth();
		initialize(teeth);
	}

	public void initialize(List<Tooth> teeth) {
		if (teeth.isEmpty())
			return;
		for (int i = 0; i < teeth.size(); i++) {
			Tooth t = teeth.get(i);
			Rect2D rect = t.rect();
			float x1 = (float) rect.getX1() * proportion + offsetX;
			float x2 = (float) rect.getX2() * proportion + offsetX;
			float y1 = (float) rect.getY1() * proportion + offsetY;
			float y2 = (float) rect.getY2() * proportion + offsetY;
			Rect2D.Float tmpRect = new Rect2D.Float(x1, y1, x2, y2);
			DrawableBorderRect dbr = new DrawableBorderRect();
			dbr.setRect(tmpRect);
			listPanelTooth.add(dbr);

			TextField tt = new TextTooth();
			tt.setText(String.valueOf(t.site()));
			listTextTooth.add(tt);
			TextField tl = new TextLength();
			tl.setText(String.valueOf(t.realLength()));
			listTextLength.add(tl);
			this.add(tt);
			this.add(tl);
			this.setTextFieldLocation(i, tmpRect);
		}
		this.repaint();
	}

	@Deprecated
	public void collectdata() {
		TeethData teethData = TeethData.getInstance();
		List<Tooth> teeth = this.getTeeth();
		for (Tooth t : teeth) {
			teethData.add(t);
		}
	}

	public List<Tooth> getTeeth() {
		List<Tooth> teeth = new ArrayList<>();
		for (int i = 0; i < listPanelTooth.size(); i++) {
			int site = ((TextTooth) listTextTooth.get(i)).getSite();
			float realLength = ((TextLength) listTextLength.get(i)).getLength();
			float x1 = ((float) listPanelTooth.get(i).getX1() - offsetX) / proportion;
			float x2 = ((float) listPanelTooth.get(i).getX2() - offsetX) / proportion;
			float y1 = ((float) listPanelTooth.get(i).getY1() - offsetY) / proportion;
			float y2 = ((float) listPanelTooth.get(i).getY2() - offsetY) / proportion;
			Rect2D rect = new Rect2D.Float(x1, y1, x2, y2);
			teeth.add(new Tooth(site, rect, realLength));
		}
		return teeth;
	}

	public void setTextLengthVisable(boolean visable) {
		this.showTextLength = visable;
	}

	@Override
	public void zoom(float proportion, int offsetX, int offsetY) {
		for (int i = 0; i < listPanelTooth.size(); i++) {
			DrawableBorderRect dbr = listPanelTooth.get(i);
			float x1 = ((float) dbr.getX1() - this.offsetX) / this.proportion * proportion + offsetX;
			float x2 = ((float) dbr.getX2() - this.offsetX) / this.proportion * proportion + offsetX;
			float y1 = ((float) dbr.getY1() - this.offsetY) / this.proportion * proportion + offsetY;
			float y2 = ((float) dbr.getY2() - this.offsetY) / this.proportion * proportion + offsetY;
			Rect2D tmpRect = new Rect2D.Float(x1, y1, x2, y2);
			dbr.setRect(tmpRect);
			this.setTextFieldLocation(i, tmpRect);
		}
		super.zoom(proportion, offsetX, offsetY);
	}

	@Deprecated
	private void setTextFieldLocation(int index, int x, int y, int width, int height) {
		int x1 = x + (width - TEXT_FIELD_WIDTH) / 2;
		int y1;
		if (width > TEXT_FIELD_WIDTH && height > 2 * TEXT_FIELD_HEIGHT) {
			y1 = y + (height - 2 * TEXT_FIELD_HEIGHT) / 2;

		} else {
			y1 = y + height;
		}
		listTextTooth.get(index).setBounds(x1, y1, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
		listTextLength.get(index).setBounds(x1, y1 + TEXT_FIELD_HEIGHT, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
		listTextLength.get(index).setVisible(showTextLength);

	}

	private void setTextFieldLocation(int index, Rect rect) {
		this.setTextFieldLocation(index, rect.getx1(), rect.gety1(), rect.getWidth(), rect.getHeight());

	}

	private void setTextFieldLocation(int index, Rect2D rect) {
		this.setTextFieldLocation(index, (int) rect.getX1(), (int) rect.getY1(), (int) rect.getWidth(),
				(int) rect.getHeight());

	}

	class TextField extends JTextField {
		public TextField() {
			super();
			this.setOpaque(false);

		}

		public TextField(String title) {
			this();

			this.setBorder(new TitledBorder(title));
		}

	}

	class TextLength extends TextField {

		public TextLength() {
			super(Constant.TOOTH_LENGTH);

		}

		float getLength() {
			if (!this.getText().isBlank()) {
				return Float.parseFloat(this.getText());
			}
			return 0;

		}
	}

	class TextTooth extends TextField {
		public TextTooth() {
			super(Constant.TOOTH_SITE);

		}

		int getSite() {
			if (!this.getText().isBlank()) {
				return Integer.parseInt(this.getText());
			}
			return 0;
		}

	}

	@Override
	protected void rectAdded() {
		TextField textTooth = new TextTooth();
		listTextTooth.add(textTooth);
		TextField textLength = new TextLength();
		listTextLength.add(textLength);
		this.add(textTooth);
		this.add(textLength);
	}

	@Override
	protected void rectRemoved(int index) {
		TextField textTooth = listTextTooth.get(index);
		TextField textLength = listTextLength.get(index);
		this.remove(textTooth);
		this.remove(textLength);
		listTextTooth.remove(index);
		listTextLength.remove(index);
	}

	@Override
	protected void rectMoved(int index, int x1, int y1, int x2, int y2) {
		setTextFieldLocation(index, new Rect(x1, y1, x2, y2));
	}

}
