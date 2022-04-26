package tk.kqstone.dedd.ui;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class IconButton extends JButton {

	private static final int SIZE = 25;

	public IconButton() {
		super();
	}

	public IconButton(Icon icon) {
		this();
		this.setPreferredSize(new Dimension(SIZE, SIZE));
		this.setIcon(new ImageIcon(
				((ImageIcon) icon).getImage().getScaledInstance(SIZE - 5, SIZE - 5, Image.SCALE_SMOOTH)));
	}

	public IconButton(String tip, Icon icon) {
		this(icon);
		this.setToolTipText(tip);
	}
}