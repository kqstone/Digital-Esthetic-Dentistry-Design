package tk.kqstone.dedd.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GifPane extends JPanel {

	private boolean isAnimated;

	private Image image;
	private Thread thread;

	public GifPane() {
		super();
		this.setOpaque(false);
		isAnimated = false;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (!isAnimated)
						break;
					try {
						Thread.sleep(100);
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								GifPane.this.repaint();

							}
						});

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});
	}

	public GifPane(Image gifImage) {
		this();
		this.image = gifImage;
	}

	public void play() {
		if (!isAnimated) {
			isAnimated = true;
			thread.start();
		}

	}

	public void stop() {
		isAnimated = false;
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponents(g);
		if (image != null) {
			int width = this.getWidth();
			int height = this.getHeight();
			int size = width > height ? height : width;
			g.drawImage(image, (width - size) / 2, (height - size) / 2, size, size, null);
		}

	}
}