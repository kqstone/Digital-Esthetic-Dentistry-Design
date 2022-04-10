package tk.kqstone.dedd;

import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

public class ZoomableJPanel extends JPanel implements IZoomable{
	
	private ZoomInvoker zoomInvoker;
	private MouseWheelListener mouseWheelListener;
	private float proportion = 1f;
	private int offsetX = 0;
	private int offsetY = 0;
	float scale = 0.5f;
	
	public ZoomableJPanel() {
		super();
		mouseWheelListener = new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println("Mouse Moved!!!!!!!!!!!");
				int rotation = e.getWheelRotation();
				Point point = e.getPoint();
				float f = proportion + scale*rotation;
				if (f <=6.0f && f >=1.0f) {
					
					if (f < 1.5f) {
						offsetX = 0;
						offsetY = 0;
					} else {
						offsetX = Math.round((offsetX-point.x)*f/proportion+point.x);
						offsetY = Math.round((offsetY-point.y)*f/proportion+point.y);
					}
					proportion = f;
				}
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
