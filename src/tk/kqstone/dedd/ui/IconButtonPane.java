package tk.kqstone.dedd.ui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IconButtonPane extends Container {
	private List<IconButton> buttons;

	public IconButtonPane() {
		super();
		setLayout(new FlowLayout(FlowLayout.CENTER));
		buttons = new ArrayList<>();
	}

	public void addButton(IconButton iconButton) {
		this.buttons.add(iconButton);
		this.add(iconButton);
	}

	public void addButtons(List<IconButton> buttons) {
		for (IconButton b : buttons) {
			this.addButton(b);
		}
	}
	
	public void addActions(final IMethod action) {
		MouseListener l = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent event) {
				try {
					action.run(event.getSource());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		};
		for (IconButton b:buttons) {
			b.addMouseListener(l);
		}
	}
}
