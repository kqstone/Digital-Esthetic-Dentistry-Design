package tk.kqstone.dedd;

import java.util.List;

/**
 * Data Facctory Interface
 * 
 * @author kqstone
 *
 */
public interface IOptimizer {

	void setSrc(Object source);

	Object getSrc();

	Object getDst();

	void analysis();

	void optimize();

	void align();

}
