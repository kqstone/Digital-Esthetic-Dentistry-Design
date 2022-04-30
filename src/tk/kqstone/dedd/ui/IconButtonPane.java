package tk.kqstone.dedd.ui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class IconButtonPane extends Container {
	private List<IconButton> buttons;

	public IconButtonPane() {
		super();
		setLayout(new FlowLayout(FlowLayout.CENTER));
		buttons = new ArrayList<>();
	}

	public void addButton(IconButton button, final IMethod action) {
		buttons.add(button);
		this.add(button);
		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent event) {
				try {
					action.run();
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
		});
	}
}
