package tk.kqstone.dedd.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SuspendTip extends JDialog {

	private static final int DEFAULT_WIDTH = 200;
	private static final int DEFAULT_HEIGHT = 120;
	private final JLabel label;
	private final GifPane gifPane;
	private Thread thread;
	private Runnable runnable;

	private IMethod iMethod;

	public SuspendTip(Frame owner) {

		super(owner);
		this.setUndecorated(true);
		this.setBackground(new Color(0, 0, 0, 0));
		this.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		Shape roundRect = new RoundRectangle2D.Float(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, 20, 20);
		this.setLocation((owner.getWidth() - DEFAULT_WIDTH) / 2, (owner.getHeight() - DEFAULT_HEIGHT) / 2);
		getContentPane().setLayout(new BorderLayout());
		RoundRectPanel root = new RoundRectPanel();
		root.roundRect = roundRect;
		root.backgroundColor = new Color(255, 255, 255, 200);
		getContentPane().add(root);
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

		Image loadingImage = Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("/img/loading.gif"));
		gifPane = new GifPane(loadingImage);
		root.add(gifPane);

		label = new JLabel("", JLabel.CENTER);
		label.setOpaque(false);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setFont(new Font("等线", Font.BOLD, 15));
		root.add(label);

		root.add(Box.createVerticalStrut(10));

		runnable = new CRunnable();

	}

	public SuspendTip(Frame owner, String tip) {
		this(owner);
		label.setText(tip);
	}

	public void addMethod(IMethod iMethod) {
		this.iMethod = iMethod;
	}

	public void run() {
		thread = new Thread(runnable);
		thread.start();
	}

	public boolean isFinished() {
		if (thread.getState().equals(Thread.State.TERMINATED))
			return true;
		return false;
	}

	class CRunnable implements Runnable {

		@Override
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					SuspendTip.this.setVisible(true);
					gifPane.play();
				}

			});
			try {
				iMethod.run();
			} finally {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						gifPane.stop();
						SuspendTip.this.dispose();
					}

				});
			}

		}

	}

	class RoundRectPanel extends JPanel {
		protected Shape roundRect;
		protected Color backgroundColor;

		public RoundRectPanel() {
			super();
			this.setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(backgroundColor);
			g2d.fill(roundRect);

		}

	}

}
