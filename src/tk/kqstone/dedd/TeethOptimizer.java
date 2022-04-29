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
	private static final int[] TOOTH_SITES = { 11, 12, 13, 21, 22, 23 };
	List<Tooth> oriTeeth;
	List<Tooth> dstTeeth;

	public TeethOptimizer() {
		oriTeeth = new ArrayList<>();
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
	public void optimize() throws Exception {};
	
	@Override
	public void align() throws Exception {};

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

}
