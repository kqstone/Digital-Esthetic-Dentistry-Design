package tk.kqstone.dedd;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ToothData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2291853601001425119L;

	private static final String TOOTH_ID = "id";

	private static final String X1 = "x1";

	private static final String Y1 = "y1";

	private static final String X2 = "x2";

	private static final String Y2 = "y2";

	private static final String TOOTH_LENGTH = "length";

	private int site;
	private Point2D.Float p1;
	private Point2D.Float p2;
	private float length;

	public ToothData() {

	}

	public ToothData(int site, Point2D.Float p1, Point2D.Float p2, float length) {
		super();
		this.site = site;
		this.p1 = p1;
		this.p2 = p2;
		this.length = length;
	}

	public int getSite() {
		return site;
	}

	public void setSite(int site) {
		this.site = site;
	}

	public Point2D.Float getP1() {
		return p1;
	}

	public void setP1(Point2D.Float p1) {
		this.p1 = p1;
	}

	public Point2D.Float getP2() {
		return p2;
	}

	public void setP2(Point2D.Float p2) {
		this.p2 = p2;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public Map<String, String> getHashMap() {
		Map<String, String> map = new HashMap<>();
		map.put(TOOTH_ID, String.valueOf(site));
		map.put(X1, String.valueOf(p1.x));
		map.put(Y1, String.valueOf(p1.y));
		map.put(X2, String.valueOf(p2.x));
		map.put(Y2, String.valueOf(p2.y));
		map.put(TOOTH_LENGTH, String.valueOf(length));
		return map;
	}

	public static ToothData getToothData(Map<String, String> toothDataMap) {
		ToothData td = new ToothData();
		td.site = Integer.parseInt(toothDataMap.get(TOOTH_ID));
		td.length = Float.parseFloat(toothDataMap.get(TOOTH_LENGTH));
		float x1 = Float.parseFloat(toothDataMap.get(X1));
		float y1 = Float.parseFloat(toothDataMap.get(Y1));
		float x2 = Float.parseFloat(toothDataMap.get(X2));
		float y2 = Float.parseFloat(toothDataMap.get(Y2));
		td.p1 = new Point2D.Float(x1, y1);
		td.p2 = new Point2D.Float(x2, y2);
		return td;
	}

}
