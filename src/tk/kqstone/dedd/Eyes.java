package tk.kqstone.dedd;

import java.io.Serializable;

public class Eyes implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6556531414355960424L;
	
	int left;
	int right;
	
	public Eyes(int left, int right) {
		super();
		this.left = left;
		this.right = right;
	}

}
