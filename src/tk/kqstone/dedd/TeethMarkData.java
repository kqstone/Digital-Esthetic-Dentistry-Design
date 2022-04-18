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
	
	public static final int VIEW_BASE = 0;
	public static final int VIEW_FRONT = 1;
	
	private List<MarkDatum> baseMarkData;
	private List<MarkDatum> frontMarkData;

	public TeethMarkData() {
		baseMarkData = new ArrayList<>();
		frontMarkData = new ArrayList<>();
	}

	public List<MarkDatum> getBaseData() {
		return baseMarkData;
	}

	public List<MarkDatum> getFrontData() {
		return frontMarkData;
	}

	public void setBaseData(List<MarkDatum> data) {
		this.baseMarkData = data;
	}

	public void setFrontData(List<MarkDatum> data) {
		this.frontMarkData = data;
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
