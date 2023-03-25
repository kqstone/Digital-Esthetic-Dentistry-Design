package tk.kqstone.dedd;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

public interface ITeethDetection {
	
	List<Rectangle> detectTeeth(BufferedImage image) throws Exception;
}
