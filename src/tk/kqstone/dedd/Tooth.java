package tk.kqstone.dedd;

import java.io.Serializable;

public class Tooth implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int site;
	private Rect2D toothRect;
	private float realLength;
	private double scale;

	public Tooth() {
	}

	public Tooth(int site, Rect2D toothRect, float realLength) {
		this.site = site;
		this.toothRect = toothRect;
		this.realLength = realLength;
		this.scale = realLength / toothRect.getHeight();
	}

	public Tooth(int site, Rect2D toothRect, double scale) {
		this.site = site;
		this.toothRect = toothRect;
		this.realLength = (float) ((scale != 0d) ? (toothRect.getHeight() * scale) : toothRect.getHeight());
		this.scale = scale;
	}

//	public Tooth(int site, Rect toothRect, float scale) {
//		this.site = site;
//		this.scale = scale;
//		this.toothRect = toothRect;
//	}

	boolean setData(int site, Rect2D toothRect, float realLength) {
		if (this.site == site) {
			this.realLength = realLength;
			this.toothRect = toothRect;
			this.scale = realLength / toothRect.getHeight();
			return true;
		} else {
			return false;
		}

	}

//	boolean setData(int site, Rect toothRect, float scale) {
//		if (this.site == site) {
//			this.scale = scale;
//			this.toothRect = toothRect;
//			return true;
//		} else {
//			return false;
//		}
//
//	}

	public int site() {
		return site;
	}

	public float realLength() {
		return realLength;
	}

	public Rect2D rect() {
		return toothRect;
	}

	public float ratio() {
		return (float) (toothRect.getWidth() / toothRect.getHeight());
	}

	public double scale() {
		return scale;
	}

	@Override
	public boolean equals(Object tooth) {
		if (tooth == null)
			return false;
		if (tooth instanceof Tooth) {
			Tooth tmp = (Tooth) tooth;
			return (site == tmp.site()) && (toothRect.equals(tmp.rect())) && (realLength == tmp.realLength());
		}
		return super.equals(tooth);
	}

}
