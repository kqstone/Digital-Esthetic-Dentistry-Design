package tk.kqstone.dedd;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import tk.kqstone.dedd.TeethMarkData.MarkDatum;
import tk.kqstone.djl.djlyolov5.ServerImageDetection;

public class TeethMarkPanel extends BasicDrawablePanel {
	private static final int TEXT_FIELD_WIDTH = 50;
	private static final int TEXT_FIELD_HEIGHT = 40;

	private int viewId;
	private List<NumberField> listTextTooth;
	private List<NumberField> listTextLength;
	private List<DrawableBorderRect> listPanelTooth;
	
	private IMenmento menmento;

	public TeethMarkPanel() {
		super();
		this.setLayout(null);
		listTextTooth = new ArrayList<>();
		listTextLength = new ArrayList<>();
		listPanelTooth = super.getBorderPanelList();
	}

	protected void detectTeeth(BufferedImage image) throws Exception {
		IImageDetection imgDtection = new NetImageDetection();		
		List<Rectangle> rects = imgDtection.detectTeeth(image);
		System.out.println(rects);
		for (Rectangle rect:rects) {
			float x1 = (int)rect.x * proportion + offsetX;
			float y1 = (int) rect.y * proportion + offsetY;
			float x2 = (int) (rect.x+rect.width) * proportion + offsetX;
			float y2 = (int) (rect.y+rect.height) * proportion + offsetY;
			Rect2D.Float tmpRect = new Rect2D.Float(x1, y1, x2, y2);
			DrawableBorderRect dbr = new DrawableBorderRect();
			dbr.setRect(tmpRect);
			listPanelTooth.add(dbr);

			NumberField tt = new TextTooth();
			listTextTooth.add(tt);
			NumberField tl = new TextLength();
			listTextLength.add(tl);
			this.add(tt);
			this.add(tl);
//			this.setTextFieldLocation(i, tmpRect);
		}
		this.repaint();
		
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

			NumberField tt = new TextTooth();
			tt.setText(String.valueOf(t.site()));
			listTextTooth.add(tt);
			NumberField tl = new TextLength();
			tl.setText(String.valueOf(t.realLength()));
			listTextLength.add(tl);
			this.add(tt);
			this.add(tl);
			this.setTextFieldLocation(i, tmpRect);
		}
		this.repaint();
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

	public void setViewId(int viewId) {
		this.viewId = viewId;
	}

	public void setMenmento(IMenmento menmento) {
		this.menmento = menmento;
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
		listTextLength.get(index).setVisible(showLength());

	}

	private void setTextFieldLocation(int index, Rect rect) {
		this.setTextFieldLocation(index, rect.getx1(), rect.gety1(), rect.getWidth(), rect.getHeight());

	}

	private void setTextFieldLocation(int index, Rect2D rect) {
		this.setTextFieldLocation(index, (int) rect.getX1(), (int) rect.getY1(), (int) rect.getWidth(),
				(int) rect.getHeight());

	}
	
	private boolean showLength() {
		return this.viewId == WorkPanel.BASE_VIEW ? true:false;
	}
	
	public void collectTeethMarkData(TeethMarkData teethMarkData) {
		
		List<MarkDatum> markData = new ArrayList<>();
		for (int i = 0; i < listPanelTooth.size(); i++) {
			MarkDatum md = new MarkDatum();
			float x1 = ((float) listPanelTooth.get(i).getX1() - offsetX) / proportion;
			float x2 = ((float) listPanelTooth.get(i).getX2() - offsetX) / proportion;
			float y1 = ((float) listPanelTooth.get(i).getY1() - offsetY) / proportion;
			float y2 = ((float) listPanelTooth.get(i).getY2() - offsetY) / proportion;
			md.rect = new Rect2D.Float(x1, y1, x2, y2);
			md.site = ((TextTooth) listTextTooth.get(i)).getSite();
			md.length =  ((TextLength) listTextLength.get(i)).getLength();
			markData.add(md);
		}
		teethMarkData.setData(markData, viewId);
	}
	
	public void setDataFromTeethMarkData(TeethMarkData teethMarkData) {
		this.removeAll();
		this.listPanelTooth.clear();
		this.listTextLength.clear();
		this.listTextTooth.clear();
		
		List<MarkDatum> markData = teethMarkData.getData(viewId);
		for (int i = 0; i < markData.size(); i++) {
			Rect2D rect = markData.get(i).rect;
			float x1 = (float) rect.getX1() * proportion + offsetX;
			float x2 = (float) rect.getX2() * proportion + offsetX;
			float y1 = (float) rect.getY1() * proportion + offsetY;
			float y2 = (float) rect.getY2() * proportion + offsetY;
			Rect2D.Float tmpRect = new Rect2D.Float(x1, y1, x2, y2);
			DrawableBorderRect dbr = new DrawableBorderRect();
			dbr.setRect(tmpRect);
			listPanelTooth.add(dbr);
			NumberField tt = new TextTooth();
			listTextTooth.add(tt);
			int site = markData.get(i).site;
			if (site != 0) {
				tt.setText(String.valueOf(site));
			}
			this.add(tt);
			
			NumberField tl = new TextLength();
			listTextLength.add(tl);
			float length = markData.get(i).length;
			if (length != 0.0f)
				tl.setText(String.valueOf(length));
			this.add(tl);

			this.setTextFieldLocation(i, tmpRect);
		}
		this.repaint();
	}

	class NumberField extends JTextField {
		private boolean dotEnable = false;
		
		public NumberField() {
			super();
			this.setOpaque(false);
			this.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					int keyChar = e.getKeyChar();
					if (!((keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9)
							|| (dotEnable && keyChar == KeyEvent.VK_PERIOD))) {
						e.consume();
					}
				}
			});

			Document document = this.getDocument();
			document.addDocumentListener(new DocumentListener() {

				@Override
				public void insertUpdate(DocumentEvent e) {
					if (NumberField.this.isFocusOwner())
						menmento.storeCurrent();

				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub

				}
			});

		}

		public NumberField(String title) {
			this();

			this.setBorder(new TitledBorder(title));
		}
		
		public NumberField(String title, boolean dotEnable) {
			this();

			this.setBorder(new TitledBorder(title));
			this.dotEnable = dotEnable;
		}

	}

	class TextLength extends NumberField {

		public TextLength() {
			super(Constant.TOOTH_LENGTH, true);

		}

		float getLength() {
			if (!this.getText().isBlank()) {
				return Float.parseFloat(this.getText());
			}
			return 0;

		}
	}

	class TextTooth extends NumberField {
		public TextTooth() {
			super(Constant.TOOTH_SITE, false);

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
		NumberField textTooth = new TextTooth();
		listTextTooth.add(textTooth);
		NumberField textLength = new TextLength();
		listTextLength.add(textLength);
		this.add(textTooth);
		this.add(textLength);
		
	}

	@Override
	protected void rectRemoved(int index) {
		NumberField textTooth = listTextTooth.get(index);
		NumberField textLength = listTextLength.get(index);
		this.remove(textTooth);
		this.remove(textLength);
		listTextTooth.remove(index);
		listTextLength.remove(index);
	}

	@Override
	protected void rectMoved(int index, int x1, int y1, int x2, int y2) {
		setTextFieldLocation(index, new Rect(x1, y1, x2, y2));
//		teethMarkData.setBaseTeeth(getTeeth());
//		teethMarkDataMemento.add();
	}
	

	@Override
	protected void finishAction() {
		this.menmento.storeCurrent();
//		collectTeethMarkData();
	}
	

}
