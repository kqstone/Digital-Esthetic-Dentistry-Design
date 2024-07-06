package tk.kqstone.dedd;

import java.io.IOException;
import java.io.Serializable;
import java.awt.image.BufferedImage;

public interface IDetector {
	
	String detect(BufferedImage image) throws IOException;

}
