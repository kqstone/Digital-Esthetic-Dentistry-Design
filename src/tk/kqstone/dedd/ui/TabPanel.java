package tk.kqstone.dedd.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class TabPanel extends Container {
	public static final int DEFAULT_TAB_WIDTH = 90;
	public static final int DEFAULT_TAB_HEIGHT = 25;
	public static int DEFAULT_TAB_RADIUS = 4;
	public static final Color DEFAULT_COLOR = new Color(220, 220, 220);

	private List<TabLabel> tabs;
	private int focusIndex;
	private TabMouseAdapter tabMouseAdapter;

	private Container tabCtn;
	private Container iconButtonCtn;
	private ContentPanel content;
	private TabStateChangeListener listener;

	private int width;
	private int height;
	private int radius;
	private Color mainColor;

	public TabPanel(int width, int height, int radius, Color mainColor) {
		super();
		this.width = width;
		this.height = height;
		this.radius = radius;
		this.mainColor = mainColor;

		BorderLayout rootLayout = new BorderLayout();
		this.setLayout(rootLayout);
		content = new ContentPanel(radius, mainColor);
//		BorderLayout ctnLayout = new BorderLayout();
		content.setLayout(new BorderLayout());
		tabCtn = new Container();
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		layout.setHgap(-DEFAULT_TAB_RADIUS);
		layout.setVgap(0);
		tabCtn.setLayout(layout);
		Component horizontalStrut = Box.createHorizontalStrut(radius * 2);
		tabCtn.add(horizontalStrut);
		
		iconButtonCtn = new Container();
		FlowLayout layout2 = new FlowLayout();
		layout2.setAlignment(FlowLayout.RIGHT);
		layout2.setHgap(5);
		layout2.setVgap(0);
		iconButtonCtn.setLayout(layout2);
		
		Container ctn = new Container();
		ctn.setLayout(new BorderLayout());
		ctn.add(tabCtn,BorderLayout.CENTER);
		ctn.add(iconButtonCtn,BorderLayout.EAST);
		this.add(ctn, BorderLayout.NORTH);
		this.add(content, BorderLayout.CENTER);

		tabs = new ArrayList<>();
		tabMouseAdapter = new TabMouseAdapter();
	}

	public TabPanel() {
		this(DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT, DEFAULT_TAB_RADIUS, DEFAULT_COLOR);
		focusIndex = 0;
	}

	public TabPanel addTab(String label) {

		TabLabel tab = new TabLabel(label, width, height, radius, mainColor);
		tab.addMouseListener(tabMouseAdapter);
		tabs.add(tab);
		tabCtn.add(tab);
		if (tabs.size() == 1) {
			tab.changeState(TabLabel.STATE_FOCUS);

		}
		return this;
	}

	public void setFocus(int index) {
		int size = tabs.size();
		if (size == 0 || index >= size)
			return;
		if (index == focusIndex)
			return;
		focusIndex = index;

		for (int i = 0; i < size; i++) {
			if (i == index) {
				TabLabel tab = tabs.get(i);
				tab.changeState(TabLabel.STATE_FOCUS);
			} else {
				tabs.get(i).changeState(TabLabel.STATE_NORMAL);
			}
		}
	}

	public void addTabStateChangeListener(TabStateChangeListener listener) {
		this.listener = listener;
	}

	public void addContent(Component component) {
		this.content.add(component, BorderLayout.CENTER);
		component.revalidate();
		component.repaint();
	}

	public void removeContent(Component component) {
		this.content.remove(component);
	}
	
	public TabPanel addIconButton(IconButton iconButton) {
		this.iconButtonCtn.add(iconButton);
		return this;
	}

	class TabMouseAdapter extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			TabLabel tab = (TabLabel) e.getSource();

			for (TabLabel tmptap : tabs) {
				if (tmptap.equals(tab)) {
					if (tmptap.getState() != TabLabel.STATE_FOCUS) {
						tmptap.changeState(TabLabel.STATE_FOCUS);
						focusIndex = tabs.indexOf(tmptap);
//						tabCtn.setComponentZOrder(tmptap, 0);
					}
				} else {
					if (tmptap.getState() == TabLabel.STATE_FOCUS) {
						tmptap.changeState(TabLabel.STATE_NORMAL);
					}
//					tabCtn.setComponentZOrder(tmptap, 1);
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			TabLabel tab = (TabLabel) e.getSource();
			tab.mouseOver(true);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			TabLabel tab = (TabLabel) e.getSource();
			tab.mouseOver(false);
		}

	}

	public class TabLabel extends JLabel {

		public final static int STATE_FOCUS = 1;
		public final static int STATE_NORMAL = 0;
		public final static int NORMAL_MOUSEOVER = 2;
		public final static int FOCUS_MOUSEOVER = 3;
		private int state;
		private boolean isMouseOver;

		private int width;
		private int height;
		private int radius;
		private Color mainColor;

		TabLabel(String label) {
			super(label);
			state = STATE_NORMAL;
			isMouseOver = false;
			this.setVerticalAlignment(JLabel.CENTER);
			this.setHorizontalAlignment(JLabel.CENTER);
		}

		TabLabel(String label, int width, int height, int radius, Color mainColor) {
			this(label);
			this.width = width;
			this.height = height;
			this.radius = radius;
			this.mainColor = mainColor;
			this.setPreferredSize(new Dimension(width, height));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Shape area = ShapeUtilits.genTabArea(width, height, radius);
			Color birghtColor = mainColor.brighter();
			Color fillColor = null;
			Color strokeColor = null;
			switch (state) {
			case STATE_FOCUS:
				fillColor = isMouseOver ? mainColor : mainColor;
				strokeColor = isMouseOver ? mainColor : mainColor;
				break;
			case STATE_NORMAL:
				fillColor = isMouseOver ? birghtColor : birghtColor;
				strokeColor = isMouseOver ? mainColor : mainColor;
				break;
			}
			if (fillColor != null) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(fillColor);
				g2d.fill(area);
				g2d.setColor(strokeColor);
				g2d.draw(area);
			}
			super.paintComponent(g);

		}

		protected void changeState(int state) {
			if (state == this.state)
				return;
			this.state = state;
			this.repaint();
			if (listener == null)
				return;
			TabEvent e = new TabEvent(this);
			if (this.state == STATE_FOCUS) {
				listener.onFocused(e);
			} else {
				listener.unFocused(e);

			}
		}

		protected void mouseOver(boolean mouseOver) {
			isMouseOver = mouseOver;
			this.repaint(0, 0, this.getWidth(), this.getHeight());
		}

		protected int getState() {
			return state;
		}

	}

	public class ContentPanel extends JPanel {
		private int radius;
		private Color bgColor;

		public ContentPanel(int radius, Color bgColor) {
			this.radius = radius;
			this.bgColor = bgColor;
		}

		@Override
		protected void paintBorder(Graphics g) {
			int width = this.getWidth();
			int height = this.getHeight();
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(bgColor);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			System.out.println("Draw TabPanel Border");
			g2d.fillRoundRect(0, 0, width, height, radius * 2, radius * 2);
			g2d.drawRoundRect(0, 0, width, height, radius * 2, radius * 2);
		}

	}
	
	public static class IconButton extends JButton {

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

}
