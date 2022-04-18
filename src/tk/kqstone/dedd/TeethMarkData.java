package tk.kqstone.dedd;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeethMarkData implements Serializable {

	/**
	 * TeethMarkPanel的数据，序列化便于撤销及重做
	 */
	
	private static final long serialVersionUID = -6191677393431861594L;
	
	private List<MarkDatum> baseMarkData;
	private List<MarkDatum> frontMarkData;

	public TeethMarkData() {
		baseMarkData = new ArrayList<>();
		frontMarkData = new ArrayList<>();
	}

	public List<MarkDatum> getData(int viewId) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			return this.baseMarkData;
		case WorkPanel.FRONT_VIEW:
			return this.frontMarkData;
		default:
			return null;
		}
	}

	public void setData(List<MarkDatum> data, int viewId) {
		switch (viewId) {
		case WorkPanel.BASE_VIEW:
			this.baseMarkData = data;
			break;
		case WorkPanel.FRONT_VIEW:
			this.frontMarkData = data;
			break;
		default:
			break;
		}	
	}

	protected static class MarkDatum implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8490995337217506671L;
		
		Rect2D.Float rect;
		int site;
		float length;
	}
}
