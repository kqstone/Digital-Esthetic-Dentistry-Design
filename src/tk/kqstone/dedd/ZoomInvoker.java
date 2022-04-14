package tk.kqstone.dedd;

import java.util.ArrayList;
import java.util.List;

public class ZoomInvoker {

	private List<IZoomable> listZoomable;

	public ZoomInvoker() {
		listZoomable = new ArrayList<>();
	}

	public void excuteZoom(float proportion, int offsetX, int offsetY) {
		for (IZoomable zoomable : listZoomable) {
			if (zoomable != null) {
				zoomable.zoom(proportion, offsetX, offsetY);
			}
		}
	}
	
	public void excuteSmoothZoom(float oriProportion, int oriOffsetX, int oriOffsetY, float proportion, int offsetX,
			int offsetY) {
		for (IZoomable zoomable : listZoomable) {
			if (zoomable != null) {
				zoomable.smoothZoom(oriProportion, oriOffsetX, oriOffsetY, proportion, offsetX, offsetY);
			}
		}
	}

	public void addReceiver(IZoomable zoomable) {
		listZoomable.add(zoomable);
	}

}
