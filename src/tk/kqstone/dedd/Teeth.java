package tk.kqstone.dedd;

import java.io.Serializable;
import java.util.List;

public class Teeth implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5112254936090509496L;

	public static final int FUNC_OPTIMIZE = 0;
	public static final int FUNC_ALIGN = 1;
	private List<Tooth> teeth;
	private int fun;

	public List<Tooth> getTeeth() {
		return teeth;
	}

	public void setTeeth(List<Tooth> teeth) {
		this.teeth = teeth;
	}

	public int getFun() {
		return fun;
	}

	public void setFun(int fun) {
		this.fun = fun;
	}

}
