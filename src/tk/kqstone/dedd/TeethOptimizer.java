package tk.kqstone.dedd;

import java.util.ArrayList;
import java.util.List;

import tk.kqstone.dedd.Rect2D.Float;

/**
 * Analysis and Adjust Teeth Data
 * 
 * @author kqstone
 *
 */
public class TeethOptimizer implements IOptimizer {
	public static final float PERFECT_WLRATIO_T1 = 0.78f;
	public static final float PERFECT_LRATIO_T2TOT1 = 0.8f;
	public static final float PERFECT_LRATIO_T3TOT1 = 0.85f;
	private static final int[] TOOTH_SITES = { 11, 12, 13, 21, 22, 23 };
	private List<Tooth> oriTeeth;
	private List<Tooth> dstTeeth;

	public TeethOptimizer() {
		dstTeeth = new ArrayList<>();
	}

	public TeethOptimizer(List<Tooth> source) {
		this();
		oriTeeth = source;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSrc(Object source) {
		if (source instanceof List) {
			this.oriTeeth = (List<Tooth>) source;
		}
	}

	@Override
	public Object getSrc() {
		// TODO Auto-generated method stub
		return oriTeeth;
	}

	@Override
	public Object getDst() {
		return dstTeeth;
	}

	@Override
	public void optimize() {
		optimizeT1();
		optimizeT2();
		optimizeT3();

	}
	
	@Override
	public void align() {
		alignT1();
		alignT2();
		alignT3();

	}

	@Override
	public void analysis() {
		List<Tooth> tmp = new ArrayList<>();
		for (Tooth t : oriTeeth) {
			for (int i = 0; i < TOOTH_SITES.length; i++) {
				if (t.site() == TOOTH_SITES[i]) {
					tmp.add(t);
				}
			}
		}
		oriTeeth = tmp;
	}

	public void optimizeT1() {
		Tooth t11 = null;
		Tooth t21 = null;
		Tooth dstt11 = null;
		Tooth dstt21 = null;
		boolean hasT11 = hasTooth(11);
		boolean hasT21 = hasTooth(21);
		if (hasT11) {
			t11 = getTooth(11);
		}
		if (hasT21) {
			t21 = getTooth(21);
		}
		if (hasT11 && hasT21) {
			float x1 = (float) t11.rect().getX1();
			float x4 = (float) t21.rect().getX2();
			float width = (x4 - x1) / 2;
			float height = width / PERFECT_WLRATIO_T1;
			Rect2D rect1 = new Rect2D.Float(x1, t11.rect().getY2(), x1 + width, t11.rect().getY2() - height);
			Rect2D rect2 = new Rect2D.Float(x4, t11.rect().getY2(), x4 - width, t11.rect().getY2() - height);
			dstt11 = new Tooth(11, rect1, t11.scale());
			dstt21 = new Tooth(21, rect2, t21.scale());

		} else if (hasT11) {
			float x1 = (float) t11.rect().getX1();
			float width = (float) t11.rect().getWidth();
			float height = width / PERFECT_WLRATIO_T1;
			Rect2D rect1 = new Rect2D.Float(x1, t11.rect().getY2(), x1 + width, t11.rect().getY2() - height);
			dstt11 = new Tooth(11, rect1, t11.scale());
		} else if (hasT21) {
			float x1 = (float) t21.rect().getX1();
			float width = (float) t21.rect().getWidth();
			float height = width / PERFECT_WLRATIO_T1;
			Rect2D rect1 = new Rect2D.Float(x1, t21.rect().getY2(), x1 + width, t21.rect().getY2() - height);
			dstt21 = new Tooth(21, rect1, t21.scale());
		}
		if (dstt11 != null) {
			dstTeeth.add(dstt11);
		}
		if (dstt21 != null) {
			dstTeeth.add(dstt21);
		}
	}
	
	public void alignT1() {
		Tooth t11 = null;
		Tooth t21 = null;
		Tooth dstt11 = null;
		Tooth dstt21 = null;
		boolean hasT11 = hasTooth(11);
		boolean hasT21 = hasTooth(21);
		if (hasT11) {
			t11 = getTooth(11);
		}
		if (hasT21) {
			t21 = getTooth(21);
		}
		if (hasT11 && hasT21) {
			float x1 = (float) t11.rect().getX1();
			float x4 = (float) t21.rect().getX2();
			float y1 = (float) t11.rect().getY1();
			float y2 = (float) t21.rect().getY1();
			float y3 = (float) t11.rect().getY2();
			float y4 = (float) t21.rect().getY2();
			float width = (x4 - x1) / 2;
			Rect2D rect1 = new Rect2D.Float(x1, (y1 + y2) / 2, x1 + width, (y3 + y4) / 2);
			Rect2D rect2 = new Rect2D.Float(x4, (y1 + y2) / 2, x4 - width, (y3 + y4) / 2);
			dstt11 = new Tooth(11, rect1, t11.scale());
			dstt21 = new Tooth(21, rect2, t21.scale());

		}
		if (dstt11 != null) {
			dstTeeth.add(dstt11);
		}
		if (dstt21 != null) {
			dstTeeth.add(dstt21);
		}
	}

	public void optimizeT2() {
		Tooth t12 = null;
		Tooth t22 = null;
		Tooth dstt12 = null;
		Tooth dstt22 = null;
		boolean hasT12 = hasTooth(12);
		boolean hasT22 = hasTooth(22);
		if (hasT12) {
			t12 = getTooth(12);
			float x1 = (float) t12.rect().getX1();
			float x2 = (float) t12.rect().getX2();
			float y1 = (float) t12.rect().getY1();
			float y2 = (float) t12.rect().getY2();
			if (hasTooth(11)) {
				y1 = (float) (getTooth(11).rect().getY1()
						+ getTooth(11).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);
				y2 = (float) (getTooth(11).rect().getY2()
						- getTooth(11).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);
				x2 = (float) getTooth(11).rect().getX1();
			} else if (hasTooth(21)) {
				y1 = (float) (getTooth(21).rect().getY1()
						+ getTooth(21).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);
				y2 = (float) (getTooth(21).rect().getY2()
						- getTooth(21).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);
			}
			Rect2D rect = new Rect2D.Float(x1, y1, x2, y2);
			dstt12 = new Tooth(12, rect, t12.scale());
		}
		if (hasT22) {
			t22 = getTooth(22);

			float x1 = (float) t22.rect().getX1();
			float x2 = (float) t22.rect().getX2();
			float y1 = (float) t22.rect().getY1();
			float y2 = (float) t22.rect().getY2();
			if (hasTooth(21)) {
				y1 = (float) (getTooth(21).rect().getY1()
						+ getTooth(21).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);
				y2 = (float) (getTooth(21).rect().getY2()
						- getTooth(21).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);
				x1 = (float) getTooth(21).rect().getX2();
			} else if (hasTooth(11)) {
				y1 = (float) (getTooth(11).rect().getY1()
						+ getTooth(11).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);
				y2 = (float) (getTooth(11).rect().getY2()
						- getTooth(11).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);

			}
			Rect2D rect = new Rect2D.Float(x1, y1, x2, y2);
			dstt22 = new Tooth(22, rect, t22.scale());
		}
		if (dstt12 != null) {
			dstTeeth.add(dstt12);
		}
		if (dstt22 != null) {
			dstTeeth.add(dstt22);
		}
	}
	
	public void alignT2() {
		Tooth t12 = null;
		Tooth t22 = null;
		Tooth dstt12 = null;
		Tooth dstt22 = null;
		float x1 = 0, x2 = 0, y1 = 0, y2 = 0, x3 = 0, y3 = 0, x4 = 0, y4 = 0;
		boolean hasT12 = hasTooth(12);
		boolean hasT22 = hasTooth(22);
		if (hasT12) {
			t12 = getTooth(12);
			x1 = (float) t12.rect().getX1();
			x2 = (float) t12.rect().getX2();
			y1 = (float) t12.rect().getY1();
			y2 = (float) t12.rect().getY2();
		}
		if (hasT22) {
			t22 = getTooth(22);
			x3 = (float) t22.rect().getX1();
			x4 = (float) t22.rect().getX2();
			y3 = (float) t22.rect().getY1();
			y4 = (float) t22.rect().getY2();
		}
		if (hasT12 && hasT22) {
			float ty1 = (y1 + y3) / 2;
			float ty2 = (y2 + y4) / 2;
			y1 = ty1;
			y3 = ty1;
			y2 = ty2;
			y4 = ty2;
		}
		if (hasTooth(11)) {
			x2 = (float) getTooth(11).rect().getX1();
		}
		if (hasTooth(21)) {
			x3 = (float) getTooth(21).rect().getX2();

		}
		if (hasT12) {
			Rect2D rect = new Rect2D.Float(x1, y1, x2, y2);
			dstt12 = new Tooth(12, rect, t12.scale());
			dstTeeth.add(dstt12);
		}
		if (hasT22) {
			Rect2D rect = new Rect2D.Float(x3, y3, x4, y4);
			dstt22 = new Tooth(22, rect, t22.scale());
			dstTeeth.add(dstt22);
		}
	}

	public void optimizeT3() {
		Tooth t13 = null;
		Tooth t23 = null;
		Tooth dstt13 = null;
		Tooth dstt23 = null;
		boolean hasT13 = hasTooth(13);
		boolean hasT23 = hasTooth(23);
		if (hasT13) {
			t13 = getTooth(13);
			float x1 = (float) t13.rect().getX1();
			float x2 = (float) t13.rect().getX2();
			float y1 = (float) t13.rect().getY1();
			float y2 = (float) t13.rect().getY2();
			if (hasTooth(12)) {
				y1 = (float) (getTooth(12).rect().getY1()
						- getTooth(12).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);
				y2 = (float) (getTooth(12).rect().getY2()
						- getTooth(12).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 4);
				x2 = (float) getTooth(12).rect().getX1();
			} else if (hasTooth(22)) {
				y1 = (float) (getTooth(22).rect().getY1()
						- getTooth(22).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);
				y2 = (float) (getTooth(22).rect().getY2()
						- getTooth(22).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 4);
			} else if (hasTooth(11)) {
				y1 = (float) (getTooth(11).rect().getY1());
				y2 = (float) (getTooth(11).rect().getY2()
						- getTooth(11).rect().getHeight() * (1 - PERFECT_LRATIO_T3TOT1));
			} else if (hasTooth(21)) {
				y1 = (float) (getTooth(21).rect().getY1());
				y2 = (float) (getTooth(21).rect().getY2()
						- getTooth(21).rect().getHeight() * (1 - PERFECT_LRATIO_T3TOT1));
			}
			Rect2D rect = new Rect2D.Float(x1, y1, x2, y2);
			dstt13 = new Tooth(13, rect, t13.scale());
		}
		if (hasT23) {
			t23 = getTooth(23);
			float x1 = (float) t23.rect().getX1();
			float x2 = (float) t23.rect().getX2();
			float y1 = (float) t23.rect().getY1();
			float y2 = (float) t23.rect().getY2();
			if (hasTooth(22)) {
				y1 = (float) (getTooth(22).rect().getY1()
						- getTooth(22).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);
				y2 = (float) (getTooth(22).rect().getY2()
						- getTooth(22).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 4);
				x1 = (float) getTooth(22).rect().getX2();
			} else if (hasTooth(12)) {
				y1 = (float) (getTooth(12).rect().getY1()
						- getTooth(12).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 2);
				y2 = (float) (getTooth(12).rect().getY2()
						- getTooth(12).rect().getHeight() * (1 - PERFECT_LRATIO_T2TOT1) / 4);
			} else if (hasTooth(11)) {
				y1 = (float) (getTooth(11).rect().getY1());
				y2 = (float) (getTooth(11).rect().getY2()
						- getTooth(11).rect().getHeight() * (1 - PERFECT_LRATIO_T3TOT1));
			} else if (hasTooth(21)) {
				y1 = (float) (getTooth(21).rect().getY1());
				y2 = (float) (getTooth(21).rect().getY2()
						- getTooth(21).rect().getHeight() * (1 - PERFECT_LRATIO_T3TOT1));
			}
			Rect2D rect = new Rect2D.Float(x1, y1, x2, y2);
			dstt23 = new Tooth(23, rect, t23.scale());
		}
		if (dstt13 != null) {
			dstTeeth.add(dstt13);
		}
		if (dstt23 != null) {
			dstTeeth.add(dstt23);
		}
	}

	
	public void alignT3() {
		Tooth t13 = null;
		Tooth t23 = null;
		Tooth dstt13 = null;
		Tooth dstt23 = null;
		float x1 = 0, x2 = 0, y1 = 0, y2 = 0, x3 = 0, y3 = 0, x4 = 0, y4 = 0;
		boolean hasT13 = hasTooth(13);
		boolean hasT23 = hasTooth(23);
		if (hasT13) {
			t13 = getTooth(13);
			x1 = (float) t13.rect().getX1();
			x2 = (float) t13.rect().getX2();
			y1 = (float) t13.rect().getY1();
			y2 = (float) t13.rect().getY2();
		}
		if (hasT23) {
			t23 = getTooth(23);
			x3 = (float) t23.rect().getX1();
			x4 = (float) t23.rect().getX2();
			y3 = (float) t23.rect().getY1();
			y4 = (float) t23.rect().getY2();
		}
		if (hasT13 && hasT23) {
			float ty1 = (y1 + y3) / 2;
			float ty2 = (y2 + y4) / 2;
			y1 = ty1;
			y3 = ty1;
			y2 = ty2;
			y4 = ty2;
		}
		if (hasTooth(12)) {
			x2 = (float) getTooth(12).rect().getX1();
		}
		if (hasTooth(22)) {
			x3 = (float) getTooth(22).rect().getX2();
		}
		if (hasT13) {
			Rect2D rect = new Rect2D.Float(x1, y1, x2, y2);
			dstt13 = new Tooth(13, rect, t13.scale());
			dstTeeth.add(dstt13);
		}
		if (hasT23) {
			Rect2D rect = new Rect2D.Float(x3, y3, x4, y4);
			dstt23 = new Tooth(23, rect, t23.scale());
			dstTeeth.add(dstt23);
		}
	}

	private boolean hasTooth(int site) {
		for (Tooth t : oriTeeth) {
			if (t.site() == site) {
				return true;
			}
		}
		return false;
	}

	private Tooth getTooth(int site) {
		for (Tooth t : dstTeeth) {
			if (t.site() == site) {
				return t;
			}
		}
		for (Tooth t : oriTeeth) {
			if (t.site() == site) {
				return t;
			}
		}
		return null;
	}

}
