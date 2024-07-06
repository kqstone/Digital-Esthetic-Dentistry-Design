import java.io.Serializable;

import tk.kqstone.dedd.ui.IMethod;

public class Mouth implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7434449612209896497L;
	
	int x;
	int y;
	int w;
	int h;
	
	public Mouth(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
}
