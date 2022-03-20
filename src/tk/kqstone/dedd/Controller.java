package tk.kqstone.dedd;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.Float;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import tk.kqstone.dedd.ProjDataHelper.DataCheckException;

public class Controller implements IController {

	private MainUI mainUI;

	public Controller() {
		mainUI = new MainUI();
		mainUI.addController(this);
	}

	@Override
	public ProjData readData() throws IOException, ParserConfigurationException, SAXException, DataCheckException {
		File file = mainUI.getProjFile();
		ProjData data = ProjDataHelper.readFromfile(file);
		return data;

	}

	public static void start() {
		new Controller();
	}

	public static void main(String[] args) {
		start();

	}

	@Override
	public void saveData() throws IOException {
		File file = mainUI.getProjFile();
		if (file == null) {
			throw new IOException("File not assigned");
		}
		ProjData data = new ProjData();
		BasicInfo info = mainUI.getBasicInfo();
		data.setBasicInfo(info);

		WorkSpace workspace = mainUI.getWorkspace();
		BufferedImage baseImage = workspace.getImage(WorkPanel.BASE_VIEW);
		BufferedImage frontImage = workspace.getImage(WorkPanel.FRONT_VIEW);
		if (baseImage != null) {
			data.setBaseImage(baseImage);

			Rectangle baseContentRect = workspace.getContentRect(WorkPanel.BASE_VIEW);
			List<ToothData> baseMarkedTeeth = convert2TeethData(workspace.getMarkedTeeth(WorkPanel.BASE_VIEW),
					baseContentRect);
			List<ToothData> baseAdjustedTeeth = convert2TeethData(workspace.getAdjustedTeeth(WorkPanel.BASE_VIEW),
					baseContentRect);
			int baseAdjustedTeethBright = workspace.getAdjustedTeethBright(WorkPanel.BASE_VIEW);
			int baseAdjustedTeethYellow = workspace.getAdjustedTeethYellow(WorkPanel.BASE_VIEW);

			data.setBaseMarkedTeeth(baseMarkedTeeth);
			data.setBaseAdjustedTeeth(baseAdjustedTeeth);
			data.setBaseAdjustedTeethBright(baseAdjustedTeethBright);
			data.setBaseAdjustedTeethYellow(baseAdjustedTeethYellow);
		}

		if (frontImage != null) {
			data.setFrontImage(frontImage);

			Rectangle frontContentRect = workspace.getContentRect(WorkPanel.FRONT_VIEW);
			List<Point2D.Float> lipPoints = convert2Float(workspace.getLipPoints(WorkPanel.FRONT_VIEW),
					frontContentRect);
			data.setLipPoints(lipPoints);

			List<ToothData> frontMarkedTeeth = convert2TeethData(workspace.getMarkedTeeth(WorkPanel.FRONT_VIEW),
					frontContentRect);
			List<ToothData> frontAdjustedTeeth = convert2TeethData(workspace.getAdjustedTeeth(WorkPanel.FRONT_VIEW),
					frontContentRect);
			int frontAdjustedTeethBright = workspace.getAdjustedTeethBright(WorkPanel.FRONT_VIEW);
			int frontAdjustedTeethYellow = workspace.getAdjustedTeethYellow(WorkPanel.FRONT_VIEW);
			data.setFrontMarkedTeeth(frontMarkedTeeth);
			data.setFrontAdjustedTeeth(frontAdjustedTeeth);
			data.setFrontAdjustedTeethBright(frontAdjustedTeethBright);
			data.setFrontAdjustedTeethYellow(frontAdjustedTeethYellow);
		}
		ProjDataHelper.saveTofile(data, file);
	}

	static List<ToothData> convert2TeethData(List<Tooth> teeth, Rectangle contentRect) {
		if (teeth == null)
			return null;
		int contentX = contentRect.x;
		int contentY = contentRect.y;
		int contentWidth = contentRect.width;
		int contentHeight = contentRect.height;
		List<ToothData> teethData = new ArrayList<>();
		for (Tooth tooth : teeth) {
			Rect2D rect = tooth.rect();
			ToothData td = new ToothData();
			td.setP1(new Point2D.Float(((float) rect.getX1() - contentX) / contentWidth,
					((float) rect.getY1() - contentY) / contentHeight));
			td.setP2(new Point2D.Float(((float) rect.getX2() - contentX) / contentWidth,
					((float) rect.getY2() - contentY) / contentHeight));
			td.setLength(tooth.realLength());
			td.setSite(tooth.site());
			teethData.add(td);
		}
		return teethData;
	}

	static List<Tooth> convert2Teeth(List<ToothData> teethData, Rectangle contentRect) {
		if (teethData == null)
			return null;
		int contentX = contentRect.x;
		int contentY = contentRect.y;
		int contentWidth = contentRect.width;
		int contentHeight = contentRect.height;
		List<Tooth> teeth = new ArrayList<>();
		for (ToothData td : teethData) {
			Point2D p1 = new Point2D.Float(td.getP1().x * contentWidth + contentX,
					td.getP1().y * contentHeight + contentY);
			Point2D p2 = new Point2D.Float(td.getP2().x * contentWidth + contentX,
					td.getP2().y * contentHeight + contentY);
			Rect2D rect = new Rect2D.Float(p1, p2);
			Tooth tooth = new Tooth(td.getSite(), rect, td.getLength());
			teeth.add(tooth);
		}
		return teeth;
	}

	static List<Point2D.Float> convert2Float(List<Point> points, Rectangle contentRect) {
		if (points == null)
			return null;
		int contentX = contentRect.x;
		int contentY = contentRect.y;
		int contentWidth = contentRect.width;
		int contentHeight = contentRect.height;
		List<Point2D.Float> point2Ds = new ArrayList<>();
		for (Point p : points) {
			Point2D.Float p2d = new Point2D.Float((p.x - contentX) / (float) contentWidth,
					(p.y - contentY) / (float) contentHeight);
			point2Ds.add(p2d);
		}
		return point2Ds;
	}

	static List<Point> convert2P(List<Point2D.Float> point2Ds, Rectangle contentRect) {
		if (point2Ds == null)
			return null;
		int contentX = contentRect.x;
		int contentY = contentRect.y;
		int contentWidth = contentRect.width;
		int contentHeight = contentRect.height;
		List<Point> points = new ArrayList<>();
		for (Point2D.Float p2d : point2Ds) {
			Point p = new Point(Math.round(p2d.x * contentWidth) + contentX,
					Math.round(p2d.y * contentHeight) + contentY);
			points.add(p);
		}
		return points;
	}
}
