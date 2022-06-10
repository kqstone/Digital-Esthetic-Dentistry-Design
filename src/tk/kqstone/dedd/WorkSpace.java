package tk.kqstone.dedd;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;

public class WorkSpace extends Container implements IMenmento{
	private WorkPanel basePanel;
	private WorkPanel frontPanel;

	private List<Tooth> baseMarkedTeeth;
	private List<Tooth> frontMarkedTeeth;


	private TeethMarkDataMemento teethMarkDataMemento = TeethMarkDataMemento.getInstance();
	
	class PanelContainer extends JPanel {
		public PanelContainer() {
			super();
			setLayout(new BorderLayout());
			setOpaque(false);
		}

		public PanelContainer(String title, int titlePosition) {
			this();
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), title, titlePosition,
					TitledBorder.TOP));
		}
	}

	public WorkSpace() {
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		JPanel leftPanel = new PanelContainer("正面视图", TitledBorder.RIGHT);
		JPanel rightPanel = new PanelContainer("口内视图", TitledBorder.LEFT);
		this.add(leftPanel);
//		this.add(Box.createHorizontalStrut(2));
		this.add(rightPanel);
		basePanel = new WorkPanel(WorkPanel.BASE_VIEW);
		frontPanel = new WorkPanel(WorkPanel.FRONT_VIEW);

		leftPanel.add(frontPanel);
		rightPanel.add(basePanel);
		
	}

	public WorkPanel getBasePanel() {
		return basePanel;
	}

	public WorkPanel getFrontPanel() {
		return frontPanel;
	}

	public BufferedImage getBaseImage() {
		return basePanel.getImage();
	}

	public BufferedImage getFrontImage() {
		return frontPanel.getImage();
	}

	public BufferedImage getImage(int viewId) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			return basePanel.getImage();

		case WorkPanel.FRONT_VIEW:
			return frontPanel.getImage();

		}
		return null;
	}

	public void setImage(int viewId, BufferedImage image) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			basePanel.setImage(image);
			break;

		case WorkPanel.FRONT_VIEW:
			frontPanel.setImage(image);
			break;
		}
	}

	public List<Tooth> getMarkedTeeth(int viewId) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			return basePanel.getMarkedTeeth();

		case WorkPanel.FRONT_VIEW:
			return frontPanel.getMarkedTeeth();

		}
		return null;
	}

	public void setMarkedTeeth(int viewId, List<Tooth> markedTeeth) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			basePanel.initMarkPanel(markedTeeth);
			baseMarkedTeeth = markedTeeth;
			break;

		case WorkPanel.FRONT_VIEW:
			frontPanel.initMarkPanel(markedTeeth);
			frontMarkedTeeth = markedTeeth;
			break;

		}
	}

	public List<Tooth> getAdjustedTeeth(int viewId) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			return basePanel.getAdjustedTeeth();

		case WorkPanel.FRONT_VIEW:
			return frontPanel.getAdjustedTeeth();

		}
		return null;
	}

	public void setAdjustedTeeth(int viewId, List<Tooth> adjustedTeeth) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			basePanel.initAdjustPanel(adjustedTeeth);
			break;

		case WorkPanel.FRONT_VIEW:
			frontPanel.initAdjustPanel(adjustedTeeth);
			break;

		}
	}

	public void addImageChangeLisener(int viewId, ImageChangeListener listener) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			basePanel.addImageChangeListener(listener);
			break;
		case WorkPanel.FRONT_VIEW:
			frontPanel.addImageChangeListener(listener);
			break;
		}
	}

	public void init() {
		basePanel.init();
		frontPanel.init();
		basePanel.getMarkPanel().setMenmento(this);
		frontPanel.getMarkPanel().setMenmento(this);
	}

	public void edit() {
		basePanel.edit();
		frontPanel.edit();
		basePanel.getImageView().unbind();
		frontPanel.getImageView().unbind();
	}

	public void unify() {
		basePanel.unify();
		frontPanel.unify();
		basePanel.getImageView().bind(frontPanel.getImageView());
		frontPanel.getImageView().bind(basePanel.getImageView());
		basePanel.getImageView().setbinded(true);
	}

	public void marklip() {
		basePanel.marklip();
		frontPanel.marklip();
	}

	public void mark() {
		basePanel.mark();
		frontPanel.mark();
	}

	public void adjust() {
		List<Tooth> baseTeeth = basePanel.getMarkedTeeth();
		List<Integer> basesites = new ArrayList<Integer>();
		List<Tooth> tmpbaseTeeth = new ArrayList<>(baseTeeth);
		if (baseMarkedTeeth != null && !baseMarkedTeeth.isEmpty()) {
			for (Tooth ctooth : baseTeeth) {
				for (Tooth otooth : baseMarkedTeeth) {
					if (ctooth.equals(otooth)) {
						tmpbaseTeeth.remove(ctooth);
						break;
					}
				}
			}
		}
		for (Tooth t : tmpbaseTeeth) {
			basesites.add(t.site());
		}

		List<Tooth> frontTeeth = frontPanel.getMarkedTeeth();
		List<Integer> frontsites = new ArrayList<Integer>();
		List<Tooth> tmpfrontTeeth = new ArrayList<>(frontTeeth);
		if (frontMarkedTeeth != null && !frontMarkedTeeth.isEmpty()) {
			for (Tooth ctooth : frontTeeth) {
				for (Tooth otooth : frontMarkedTeeth) {
					if (ctooth.equals(otooth)) {
						tmpfrontTeeth.remove(ctooth);
						break;
					}
				}
			}
		}
		for (Tooth t : tmpfrontTeeth) {
			frontsites.add(t.site());
		}

		baseMarkedTeeth = new ArrayList<>(baseTeeth);
		frontMarkedTeeth = new ArrayList<>(frontTeeth);

		tmpbaseTeeth.clear();
		tmpfrontTeeth.clear();
		List<Integer> r = new ArrayList<>(basesites);
		r.addAll(frontsites);
		for (int i : r) {
			for (Tooth t : frontTeeth) {
				if (i == t.site()) {
					tmpfrontTeeth.add(t);
					break;
				}
			}
			for (Tooth t : baseTeeth) {
				if (i == t.site()) {
					tmpbaseTeeth.add(t);
					break;
				}
			}
		}

		basePanel.adjust(tmpbaseTeeth);
		frontPanel.adjust(tmpfrontTeeth);
		basePanel.getAdjustPanel().bind(frontPanel.getAdjustPanel());

	}

	public void setAdjustedTeethBrightAndYellow(int viewId, int adjustedTeethBright, int adjustedTeethYellow) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			basePanel.getAdjustPanel().setTeethBrightAndYellow(adjustedTeethBright, adjustedTeethYellow);
			break;

		case WorkPanel.FRONT_VIEW:
			frontPanel.getAdjustPanel().setTeethBrightAndYellow(adjustedTeethBright, adjustedTeethYellow);
			break;

		}

	}

	public int getAdjustedTeethBright(int viewId) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			return basePanel.getAdjustPanel().getAdjustTeethBright();

		case WorkPanel.FRONT_VIEW:
			return frontPanel.getAdjustPanel().getAdjustTeethBright();

		}
		return 0;
	}

	public int getAdjustedTeethYellow(int viewId) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			return basePanel.getAdjustPanel().getAdjustTeethYellow();

		case WorkPanel.FRONT_VIEW:
			return frontPanel.getAdjustPanel().getAdjustTeethYellow();

		}
		return 0;
	}

	public void setLipPoints(int viewId, List<Point> lipPoints) {
		if (lipPoints == null)
			return;
		switch (viewId) {
		case WorkPanel.FRONT_VIEW:
			if (frontPanel.getMarkLipPanel() != null) {
				frontPanel.getMarkLipPanel().setPoints(lipPoints);
			}
			break;
		}
	}

	public List<Point> getLipPoints(int viewId) {
		switch (viewId) {
		case WorkPanel.FRONT_VIEW:
			if (frontPanel.getMarkLipPanel() != null) {
				return frontPanel.getMarkLipPanel().getPoints();
			}
		}
		return null;
	}

	public void addLipMaskToAdjustPanel() {
		frontPanel.addMaskToAdjustPanel();
	}

	public Rectangle getContentRect(int viewId) {
		switch (viewId) {
		case WorkPanel.FRONT_VIEW:
			return frontPanel.getImageView().getDrawRect();
		case WorkPanel.BASE_VIEW:
			return basePanel.getImageView().getDrawRect();
		}
		return null;
	}

	@Override
	public void storeCurrent() {
		TeethMarkData tmd = new TeethMarkData();
		basePanel.getMarkPanel().collectTeethMarkData(tmd);
		frontPanel.getMarkPanel().collectTeethMarkData(tmd);
		teethMarkDataMemento.add(tmd);
	}

	@Override
	public void undo() {
		try {
			TeethMarkData tmd = teethMarkDataMemento.getPrevious();
			basePanel.getMarkPanel().setDataFromTeethMarkData(tmd);
			frontPanel.getMarkPanel().setDataFromTeethMarkData(tmd);
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void redo() {
		try {
			TeethMarkData tmd = teethMarkDataMemento.getNext();
			basePanel.getMarkPanel().setDataFromTeethMarkData(tmd);
			frontPanel.getMarkPanel().setDataFromTeethMarkData(tmd);
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void autoAdjust() throws Exception {
		TeethAdjustPanel baseAdjustPanel = basePanel.getAdjustPanel();
		TeethAdjustPanel frontAdjustPanel = frontPanel.getAdjustPanel();
		if (baseAdjustPanel != null)
			baseAdjustPanel.optimize();
		if (frontAdjustPanel != null)
			frontAdjustPanel.align();
	}
	
	public void reGenAdjustTeeth() {
		TeethAdjustPanel baseAdjustPanel = basePanel.getAdjustPanel();
		TeethAdjustPanel frontAdjustPanel = frontPanel.getAdjustPanel();		
			baseAdjustPanel.clearTeeth();		
		
			frontAdjustPanel.clearTeeth();
			basePanel.adjust(baseMarkedTeeth);
			frontPanel.adjust(frontMarkedTeeth);
			basePanel.getAdjustPanel().bind(frontPanel.getAdjustPanel());
	}

	public void detectTeeth() throws Exception {
		TeethMarkPanel baseMarkPanel = basePanel.getMarkPanel();
		BufferedImage image = basePanel.genFullPreImage();
		baseMarkPanel.detectTeeth(image);
		
	}

}
