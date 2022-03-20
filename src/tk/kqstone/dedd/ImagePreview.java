package tk.kqstone.dedd;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

public class ImagePreview extends ImageView implements PropertyChangeListener {
	static final int WIDTH = 250;

	public ImagePreview() {
		super();
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.setPreferredSize(new Dimension(WIDTH, WIDTH));
		this.setText("预览");
	}

	@Override
	public void propertyChange(PropertyChangeEvent pe) {
		if (pe.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
			File f = (File) pe.getNewValue();
			if (f == null || f.isDirectory()) {
				this.setImage(null);
				return;
			} else {
				try {
					BufferedImage image = ImageIO.read(f);
					this.setImage(ImageUtils.thumbnail(image, (double) WIDTH / image.getWidth()));
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}

}
