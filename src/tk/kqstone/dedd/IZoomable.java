package tk.kqstone.dedd;

import java.util.List;

public interface IZoomable {
	
	
	void zoom(float proportion, int offsetX, int offsetY);

	void smoothZoom(float oriProportion, int oriOffsetX, int oriOffsetY, float proportion, int offsetX, int offsetY);

}
