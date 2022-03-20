package tk.kqstone.dedd;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeethData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int ORIGIN_TOOTH = 0;
	public static final int ADJUSTED_TOOTH = 1;
	private static TeethData instance = new TeethData();
	private List<Tooth> teeth;
	private List<Tooth> adjustedTeeth;

	public static TeethData getInstance() {
		return instance;
	}

	private TeethData() {
		teeth = new ArrayList<>();
		adjustedTeeth = new ArrayList<>();
	}

	public static void setData(TeethData data) {
		instance = data;
	}

	public List<Tooth> getTeeth() {
		return teeth;
	}

	public List<Tooth> getAdjustedTeeth() {
		return adjustedTeeth;
	}

	public void setTeeth(List<Tooth> teeth) {
		this.teeth = teeth;
	}

	public void setAdjustedTeeth(List<Tooth> adjustedTeeth) {
		this.adjustedTeeth = adjustedTeeth;
	}

	public void clear() {
		teeth.clear();
		adjustedTeeth.clear();
	}

	public int[] getContainedTeeth() {
		int size = teeth.size();
		int[] sites = new int[size];
		for (int i = 0; i < size; i++) {
			sites[i] = teeth.get(i).site();
		}
		return sites;
	}

//	public void add(int site, Rect toothRect, float scale) {
//		for (int i=0; i<teeth.size(); i++) {
//			if(site == teeth.get(i).site()) {
//				teeth.get(i).setData(site, toothRect, scale);
//				return;
//			}
//		}
//		Tooth tooth = new Tooth(site,toothRect, scale);
//		teeth.add(tooth);
//	}

	public void add(int site, Rect2D toothRect, float realLength) {
		for (int i = 0; i < teeth.size(); i++) {
			if (site == teeth.get(i).site()) {
				teeth.get(i).setData(site, toothRect, realLength);
				return;
			}
		}
		Tooth tooth = new Tooth(site, toothRect, realLength);
		teeth.add(tooth);
	}

	public void add(Tooth tooth) {
		teeth.add(tooth);
	}

	public Rect2D getToothRect(int site, int flag) {
		Tooth t = this.getTooth(site, flag);
		if (t != null) {
			return t.rect();
		}
		return null;
	}

	public float getRealLength(int site, int flag) {
		Tooth t = this.getTooth(site, flag);
		if (t != null) {
			return t.realLength();
		}
		return 0;
	}

	public double getScale(int site) {
		Tooth t = this.getTooth(site, ORIGIN_TOOTH);
		if (t != null) {
			return t.scale();
		}
		return 0;
	}

	public Tooth getTooth(int site, int flag) {
		List<Tooth> tmp = null;
		switch (flag) {
		case ORIGIN_TOOTH:
			tmp = teeth;
			break;
		case ADJUSTED_TOOTH:
			tmp = adjustedTeeth;
			break;
		}
		if (tmp == null)
			return null;
		for (Tooth t : tmp) {
			if (t.site() == site) {
				return t;
			}
		}
		return null;
	}

	public boolean hasTooth(int site, int flag) {
		List<Tooth> tmp = null;
		switch (flag) {
		case ORIGIN_TOOTH:
			tmp = teeth;
			break;
		case ADJUSTED_TOOTH:
			tmp = adjustedTeeth;
			break;
		}
		if (tmp == null)
			return false;
		for (Tooth t : tmp) {
			if (t.site() == site) {
				return true;
			}
		}
		return false;
	}

	public boolean hasTooth(int site) {
		return hasTooth(site, ORIGIN_TOOTH);
	}

	public void optimize() {
		IOptimizer anl = new TeethOptimizer();
//		anl.setData(this);
		anl.analysis();
		anl.optimize();
//		adjustedTeeth = ((TeethOptimizer)anl).getDstTeeth();
	}

}
