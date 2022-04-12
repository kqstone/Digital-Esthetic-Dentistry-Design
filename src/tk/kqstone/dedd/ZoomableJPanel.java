package tk.kqstone.dedd;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;

public abstract class ZoomableJPanel extends JPanel implements IZoomable{
	
	private ZoomInvoker zoomInvoker;
	private MouseAdapter mouseListener;
	private float proportion = 1.0f;
	private final static float MAX_PROPORTION = 6.0f; 
	private int offsetX = 0;
	private int offsetY = 0;
	private final static float SCALE = 0.5f;
	
	private final static int INTERVAL = 5;
	private float oriProportion ;
	private int oriOffsetX ;
	private int oriOffsetY ;
	
	public ZoomableJPanel() {
		super();
		mouseListener = new MouseAdapter() {
			private boolean isButton3Drag = false;
			private Point start;
			private ExecutorService executor = Executors.newSingleThreadExecutor();

			@Override
			public void mouseDragged(MouseEvent e) {
				if (isButton3Drag) {
					
					Point now = e.getPoint();
					int xChange = start.x - now.x;
					int yChange = start.y - now.y;
					
					offsetX = oriOffsetX - xChange;
					offsetY = oriOffsetY - yChange;
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									SwingUtilities.invokeLater(new Runnable() {

										@Override
										public void run() {
											zoomInvoker.excuteZoom(proportion, offsetX, offsetY);
										}}); 
								}});
							
						}});
					thread.start();
					
				}

			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					isButton3Drag = true;
					start = e.getPoint();
					oriOffsetX = offsetX;
					oriOffsetY = offsetY;
				}

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (isButton3Drag) {
					start = null;
					isButton3Drag = false;
				}
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println("Mouse Moved!!!!!!!!!!!");
				oriProportion = proportion;
				oriOffsetX = offsetX;
				oriOffsetY = offsetY;
				int rotation = e.getWheelRotation();
				Point point = e.getPoint();
				float f = proportion + SCALE * rotation;
				if (f <= MAX_PROPORTION && f >= 1.0f) {

					if (f < 1.5f) {
						offsetX = 0;
						offsetY = 0;
					} else {
						offsetX = Math.round((offsetX - point.x) * f / proportion + point.x);
						offsetY = Math.round((offsetY - point.y) * f / proportion + point.y);
					}
					proportion = f;
				}
				if (proportion == oriProportion && offsetX == oriOffsetX && offsetY == oriOffsetY)
					return;
				System.out.println(oriProportion);

				Thread thread = new Thread(new Runnable() {
					float cp = proportion;
					float op = oriProportion;
					int cx = offsetX;
					int ox = oriOffsetX;
					int cy = offsetY;
					int oy = oriOffsetY;

					public void run() {

						int count = 0;

						while (count < INTERVAL) {
							count++;
							try {
								Thread.sleep(10);
								final float p = op + (cp - op) * count / INTERVAL;
								final int x = ox + (cx - ox) * count / INTERVAL;
								final int y = oy + (cy - oy) * count / INTERVAL;
								System.out.println(Thread.currentThread());
								System.out.println(cp);
								System.out.println(p);
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {

										zoomInvoker.excuteZoom(p, x, y);

									}
								});

							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				});
				executor.submit(thread);
				
			}

		};
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
		this.addMouseWheelListener(mouseListener);
		
	}
	
	public void setZoomInvoker(ZoomInvoker zoomInvoker) {
		this.zoomInvoker = zoomInvoker;
	}
	
	public void disableFocusable() {
		this.removeMouseWheelListener(mouseListener);
		this.removeMouseListener(mouseListener);
		this.removeMouseMotionListener(mouseListener);
	}
	

	@Override
	public abstract void zoom(float proportion, int offsetX, int offsetY);

}
