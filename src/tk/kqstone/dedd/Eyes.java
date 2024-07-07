package tk.kqstone.dedd;

import java.io.Serializable;

public class Eyes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6556531414355960424L;

	int x1;
	int y1;
	int x2;
	int y2;

	public Eyes(int x1, int y1, int x2, int y2) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

}
