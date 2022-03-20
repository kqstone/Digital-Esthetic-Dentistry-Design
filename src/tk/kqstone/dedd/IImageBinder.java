package tk.kqstone.dedd;

import java.awt.Point;
import java.awt.image.BufferedImage;

import tk.kqstone.dedd.EditableImageView.MarkPanel;

public interface IImageBinder {

	MarkPanel getMarkPanel();

	BufferedImage getImage();

	void setImage(BufferedImage img);

	void rotate(double radius);

}
