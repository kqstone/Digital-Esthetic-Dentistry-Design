package tk.kqstone.dedd;

import java.io.Serializable;

public class Face implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5506214878743045630L;

	int x;
	int y;
	int w;
	int h;
	float conf;

	public Face(int x, int y, int w, int h, float conf) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.conf = conf;
	}

}
