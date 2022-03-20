package tk.kqstone.dedd.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;

public class TitleBar extends JPanel {
	public static final int HEIGHT = 30;
	public static final int BORDER_WIDTH = 3;
	public static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
	public static final Color FOREGROUND_COLOR = Color.WHITE;

	private Button iconLabel;
	private JLabel titleLabel;
	private Button minimizeButton;
	private Button closeButton;

	private JFrame jFrame;

	private static ImageIcon close_normal_icon;
	private static ImageIcon close_mouseon_icon;
	private static ImageIcon close_pressed_icon;
	private static ImageIcon minimize_normal_icon;
	private static ImageIcon minimize_mouseon_icon;
	private static ImageIcon minimize_pressed_icon;

	static {
		close_normal_icon = getIcon("/img/close_normal.png");
		close_mouseon_icon = getIcon("/img/close_mouseon.png");
		close_pressed_icon = getIcon("/img/close_pressed.png");
		minimize_normal_icon = getIcon("/img/minimize_normal.png");
		minimize_mouseon_icon = getIcon("/img/minimize_mouseon.png");
		minimize_pressed_icon = getIcon("/img/minimize_pressed.png");
	}

	static ImageIcon getIcon(String url) {
		return new ImageIcon(TitleBar.class.getResource(url));
	}

	static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
		return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}

	public TitleBar() {
		super();
		this.setPreferredSize(new Dimension(0, HEIGHT));
		this.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH));
		this.setBackground(BACKGROUND_COLOR);
		setLayout(new BorderLayout());

		titleLabel = new JLabel();
		titleLabel.setForeground(FOREGROUND_COLOR);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel = new Button();
		iconLabel.setOpaque(false);
		iconLabel.setPreferredSize(new Dimension(HEIGHT - BORDER_WIDTH * 2, HEIGHT - BORDER_WIDTH * 2));
		minimizeButton = new Button(minimize_normal_icon, minimize_mouseon_icon, minimize_pressed_icon);
		minimizeButton.setPreferredSize(new Dimension(HEIGHT - BORDER_WIDTH * 2, HEIGHT - BORDER_WIDTH * 2));
		closeButton = new Button(close_normal_icon, close_mouseon_icon, close_pressed_icon);
		closeButton.setPreferredSize(new Dimension(HEIGHT - BORDER_WIDTH * 2, HEIGHT - BORDER_WIDTH * 2));

		add(titleLabel, BorderLayout.CENTER);
		add(iconLabel, BorderLayout.WEST);

		JPanel buttonArea = new JPanel();
		buttonArea.setOpaque(false);

		buttonArea.setLayout(new BoxLayout(buttonArea, BoxLayout.LINE_AXIS));
		buttonArea.add(minimizeButton);
		buttonArea.add(Box.createHorizontalStrut(20));
		buttonArea.add(closeButton);
		buttonArea.add(Box.createHorizontalStrut(5));
		add(buttonArea, BorderLayout.EAST);
		minimizeButton.getWidth();
		minimizeButton.addClickListener(new MouseClickListener() {

			@Override
			public void click() {
				if (jFrame != null) {
					jFrame.setExtendedState(Frame.ICONIFIED | jFrame.getExtendedState());
				}
			}
		});
		closeButton.addClickListener(new MouseClickListener() {

			@Override
			public void click() {
				if (jFrame != null) {
					jFrame.dispose();
				}
			}
		});
	}

	public TitleBar(JFrame jFrame) {
		this();
		this.jFrame = jFrame;
		if (jFrame.getIconImage() != null)
			this.iconLabel.setIcon(new ImageIcon(jFrame.getIconImage()));
		if (jFrame.getTitle() != null)
			this.titleLabel.setText(jFrame.getTitle());
	}

	public void setIcon(ImageIcon icon) {
		iconLabel.setIcon(icon);
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	class Button extends JPanel {
		ImageIcon normalIcon;
		ImageIcon mouseonIcon;
		ImageIcon pressedIcon;

		private ImageIcon icon;

		private MouseClickListener listener;

		public void addClickListener(MouseClickListener listener) {
			this.listener = listener;
		}

		Button() {
			super();
			this.setOpaque(false);
			MouseListener l = new MouseListener();
			this.addMouseListener(l);
			this.addMouseMotionListener(l);
		}

		Button(ImageIcon normalIcon) {
			super();
			this.setOpaque(false);
			this.normalIcon = normalIcon;
			this.setIcon(normalIcon);
			MouseListener l = new MouseListener();
			this.addMouseListener(l);
			this.addMouseMotionListener(l);
		}

		Button(ImageIcon normalIcon, ImageIcon mouseonIcon, ImageIcon pressedIcon) {
			this(normalIcon);
			this.mouseonIcon = mouseonIcon;
			this.pressedIcon = pressedIcon;
		}

		public void setIcon(Icon icon) {
			if (icon instanceof ImageIcon) {
				if (this.icon == null || !this.icon.equals(icon)) {
					this.icon = (ImageIcon) icon;
					repaint();
				}
			}

		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (icon == null)
				return;
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawImage(icon.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
		}

		class MouseListener extends MouseAdapter {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (listener != null) {
					listener.click();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (pressedIcon != null)
					Button.this.setIcon(pressedIcon);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				Button.this.setIcon(normalIcon);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (mouseonIcon != null)
					Button.this.setIcon(mouseonIcon);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				Button.this.setIcon(normalIcon);
			}

		}
	}

	interface MouseClickListener {
		void click();
	}

}
