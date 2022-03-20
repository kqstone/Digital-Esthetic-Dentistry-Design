package tk.kqstone.dedd;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
//import java.awt.geom.Point2D.Float;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import tk.kqstone.dedd.utils.LogUtils;

public class ProjData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4839299522844429103L;

	private BasicInfo basicInfo;

	private BufferedImage baseImage;
	private BufferedImage frontImage;

	private List<ToothData> baseMarkedTeeth;
	private List<ToothData> frontMarkedTeeth;
	private List<ToothData> baseAdjustedTeeth;
	private List<ToothData> frontAdjustedTeeth;

	private int baseAdjustedTeethBright;
	private int baseAdjustedTeethYellow;
	private int frontAdjustedTeethBright;
	private int frontAdjustedTeethYellow;

	private List<Point2D.Float> lipPoints;

	public BasicInfo getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(BasicInfo basicInfo) {
		this.basicInfo = basicInfo;
	}

	public BufferedImage getBaseImage() {
		return baseImage;
	}

	public void setBaseImage(BufferedImage baseImage) {
		if (baseImage == null)
			return;
		this.baseImage = baseImage;
	}

	public BufferedImage getFrontImage() {
		return frontImage;
	}

	public void setFrontImage(BufferedImage frontImage) {
		if (frontImage == null)
			return;
		this.frontImage = frontImage;
	}

	public List<ToothData> getBaseMarkedTeeth() {
		return baseMarkedTeeth;
	}

	public void setBaseMarkedTeeth(List<ToothData> baseMarkedTeeth) {
		this.baseMarkedTeeth = baseMarkedTeeth;
	}

	public List<ToothData> getFrontMarkedTeeth() {
		return frontMarkedTeeth;
	}

	public void setFrontMarkedTeeth(List<ToothData> frontMarkedTeeth) {
		this.frontMarkedTeeth = frontMarkedTeeth;
	}

	public List<ToothData> getBaseAdjustedTeeth() {
		return baseAdjustedTeeth;
	}

	public void setBaseAdjustedTeeth(List<ToothData> baseAdjustedTeeth) {
		this.baseAdjustedTeeth = baseAdjustedTeeth;
	}

	public List<ToothData> getFrontAdjustedTeeth() {
		return frontAdjustedTeeth;
	}

	public void setFrontAdjustedTeeth(List<ToothData> frontAdjustedTeeth) {
		this.frontAdjustedTeeth = frontAdjustedTeeth;
	}

	public int getBaseAdjustedTeethBright() {
		return baseAdjustedTeethBright;
	}

	public void setBaseAdjustedTeethBright(int baseAdjustedTeethBright) {
		this.baseAdjustedTeethBright = baseAdjustedTeethBright;
	}

	public int getBaseAdjustedTeethYellow() {
		return baseAdjustedTeethYellow;
	}

	public void setBaseAdjustedTeethYellow(int baseAdjustedTeethYellow) {
		this.baseAdjustedTeethYellow = baseAdjustedTeethYellow;
	}

	public int getFrontAdjustedTeethBright() {
		return frontAdjustedTeethBright;
	}

	public void setFrontAdjustedTeethBright(int frontAdjustedTeethBright) {
		this.frontAdjustedTeethBright = frontAdjustedTeethBright;
	}

	public int getFrontAdjustedTeethYellow() {
		return frontAdjustedTeethYellow;
	}

	public void setFrontAdjustedTeethYellow(int frontAdjustedTeethYellow) {
		this.frontAdjustedTeethYellow = frontAdjustedTeethYellow;
	}

	public List<Point2D.Float> getLipPoints() {
		return lipPoints;
	}

	public void setLipPoints(List<Point2D.Float> lipPoints) {
		this.lipPoints = lipPoints;
	}

}
