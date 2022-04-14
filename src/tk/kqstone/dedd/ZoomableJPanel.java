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
	protected float proportion = 1.0f;
	private final static float MAX_PROPORTION = 6.0f; 
	protected int offsetX = 0;
	protected int offsetY = 0;
	private final static float SCALE = 0.5f;
	
	private final static int INTERVAL = 5;
	private float oriProportion = 1.0f ;
	private int oriOffsetX = 0;
	private int oriOffsetY = 0;
	private final ExecutorService executor;
	
	public ZoomableJPanel() {
		super();
		executor = Executors.newSingleThreadExecutor();
		mouseListener = new MouseAdapter() {
			private boolean isButton3Drag = false;
			private Point start;
			

			@Override
			public void mouseDragged(MouseEvent e) {
				if (isButton3Drag) {
					final int x;
					final int y;
					Point now = e.getPoint();
					int xChange = start.x - now.x;
					int yChange = start.y - now.y;
					
					x = oriOffsetX - xChange;
					y = oriOffsetY - yChange;
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {

									zoomInvoker.excuteZoom(proportion, x, y);
								}
							});
							
						}
					});
					thread.start();
					
				}

			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON2) {
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
					oriOffsetX = offsetX;
					oriOffsetY = offsetY;
				}
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println("Mouse Moved!!!!!!!!!!!");
				float f;
				int x = oriOffsetX, y = oriOffsetY;
				int rotation = e.getWheelRotation();
				Point point = e.getPoint();
				f = oriProportion - SCALE * rotation;
				if (f > MAX_PROPORTION || f < 1.0f) {
					f = oriProportion;
					return;
				} else {
					if (f < 1.5f) {
						x = 0;
						y = 0;
					} else {
						x = Math.round((x - point.x) * f / oriProportion + point.x);
						y = Math.round((y - point.y) * f / oriProportion + point.y);
					}
				}
				System.out.println(oriProportion);
				zoomInvoker.excuteSmoothZoom(oriProportion, oriOffsetX, oriOffsetY, f, x, y);
//				smoothZoom(oriProportion, oriOffsetX, oriOffsetY, f, x, y);
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
	public void zoom(float proportion, int offsetX, int offsetY) {
		this.proportion = proportion;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.repaint();
	}
	
	@Override
	public void smoothZoom(final float oriProportion, final int oriOffsetX, final int oriOffsetY,
			final float proportion, final int offsetX, final int offsetY) {
		Thread thread = new Thread(new Runnable() {

			public void run() {

				int count = 0;

				while (count < INTERVAL) {
					count++;
					try {
						Thread.sleep(10);
						final float p = oriProportion + (proportion - oriProportion) * count / INTERVAL;
						final int x = oriOffsetX + (offsetX - oriOffsetX) * count / INTERVAL;
						final int y = oriOffsetY + (offsetY - oriOffsetY) * count / INTERVAL;
						System.out.println(Thread.currentThread());
						System.out.println(p);
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {

								zoom(p, x, y);

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
		this.oriProportion = proportion;
		this.oriOffsetX = offsetX;
		this.oriOffsetY = offsetY;
	}

}
