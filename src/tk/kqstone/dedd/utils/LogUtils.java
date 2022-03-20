package tk.kqstone.dedd.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class LogUtils {

	public static final boolean DBG = true;

	public static void log(String msg) {
		if (!DBG)
			return;
		try {
			Logger logger = Logger.getGlobal();
			logger.info(msg);
		} catch (NoClassDefFoundError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void log(Level level, String msg) {
		if (!DBG)
			return;
		try {
			Logger logger = Logger.getGlobal();
			logger.log(level, msg);
		} catch (NoClassDefFoundError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
