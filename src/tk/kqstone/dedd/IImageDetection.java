package tk.kqstone.dedd;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

public interface IImageDetection {
	
	List<Rectangle> detectTeeth(BufferedImage image) throws Exception;
}
