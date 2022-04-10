package tk.kqstone.dedd;

import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public class ZoomableJPanel extends JPanel implements IZoomable{
	
	private ZoomInvoker zoomInvoker;
	private MouseWheelListener mouseWheelListener;
	
	public ZoomableJPanel() {
		super();
		mouseWheelListener = new MouseWheelListener(){
			final float scale = 1f;
			int clicks = 0;
			Point point = new Point(0, 0);

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println("Mouse Moved!!!!!!!!!!!");
				clicks += e.getWheelRotation();
				if (clicks < 0) {
					clicks = 0;
				} else if (clicks > 3) {
					clicks = 3;
				}
				if (clicks == 1 && e.getWheelRotation() == 1) {
					point = e.getPoint();
				}
				float proportion = 1 + (clicks) * scale;
				int offsetX = Math.round(point.x * (1 - proportion));
				int offsetY = Math.round(point.y * (1 - proportion));
//				if (zoomInvoker != null)
					zoomInvoker.excuteZoom(proportion, offsetX, offsetY);
			}
		};
		this.addMouseWheelListener(mouseWheelListener);
	}
	
	public void setZoomInvoker(ZoomInvoker zoomInvoker) {
		this.zoomInvoker = zoomInvoker;
	}
	
	public void disableFocusable() {
		if (mouseWheelListener != null)
			this.removeMouseWheelListener(mouseWheelListener);
	}
	

	@Override
	public void zoom(float proportion, int offsetX, int offsetY) {
		
	}

}
